import mysql.connector # Mysql DB 연결
import requests # API 호출
import traceback # 예외 추적적
import pandas as pd # CSV 데이터 처리용
import csv 
import re # 정규식(시간 포맷 필터링용)
from datetime import datetime, timedelta #시간 처리

# ✅ MySQL 연결 함수
def connect_to_database():
    print("=== 실행 시작 ===")
    print(" DB 연결 시도 중...")

    try:
        print(" 연결 시도 직전")
        # MySQL 연결 설정
        conn = mysql.connector.connect(
            host='127.0.0.1',
            port=3306,
            user='root',
            password='1234',
            database='capstone',
            connection_timeout=5,
            use_pure=True
        )
        print(" DB 연결 성공")
        return conn
    except mysql.connector.Error as err:
        print(" DB 연결 실패:", err)
        return None
    except Exception as e:
        print(" 일반 에러 발생:", e)
        return None

# ✅ 기온, 강수량, 미세먼지에 따라 옷차림 및 마스크 권장 문구 생성
def generate_recommendation(temp, rain, pm):
    if temp >= 28:
        outfit_txt = "민소매, 반팔, 반바지, 원피스"
    elif 27 >= temp > 23:
        outfit_txt = "반팔, 얇은 셔츠, 반바지, 면바지"
    elif 22 >= temp > 20:
        outfit_txt = "얇은 가디건, 긴팔, 면바지, 청바지"
    elif 19 >= temp > 17:
        outfit_txt = "얇은 니트, 맨투맨, 가디건, 청바지"
    elif 16 >= temp > 12:
        outfit_txt = "자켓, 가디건, 야상, 스타킹, 청바지, 면바지"
    elif 11 >= temp > 9:
        outfit_txt = "자켓, 트렌치코트, 야상, 니트, 청바지, 스타킹"
    elif 8 >= temp > 5:
        outfit_txt = "코트, 가죽자켓, 히트텍, 니트, 레깅스"
    else:
        outfit_txt = "패딩, 두꺼운 코트, 기모제품"

    # 비가 오면 우산 문구 추가
    if rain > 0:
        outfit_txt += ", 우산 또는 우비"

    # 미세먼지 농도 기준 마스크 착용 여부
    mask = "마스크를 착용하세요!" if pm > 35 else "마스크가 필요없습니다!"
    return outfit_txt, mask

# ✅ DB 연결
db = connect_to_database()
print("=== 테스트 종료 ===")

if db:
    cursor = db.cursor()

    # ✅ OpenWeather API 사용을 위한 정보
    API_KEY = "db46b60a35bc14a5afdb65245c709dda"
    SPRING_SERVER_URL = "http://localhost:8080"

    # ✅ 날씨 조회 대상 도시
    cities = [
        {"name": "Daegu", "lat": 35.8714, "lon": 128.6014},
        {"name": "Hayang", "lat": 35.9131, "lon": 128.8185}
    ]

    # ✅ 각 도시별로 날씨 및 공기질 정보 가져오기
    for city in cities:
        try:
            url_weather = f"http://api.openweathermap.org/data/2.5/weather?lat={city['lat']}&lon={city['lon']}&appid={API_KEY}&units=metric"
            url_air = f"http://api.openweathermap.org/data/2.5/air_pollution?lat={city['lat']}&lon={city['lon']}&appid={API_KEY}"

            weather_response = requests.get(url_weather).json()
            air_response = requests.get(url_air).json()

            if "main" in weather_response and "list" in air_response:
                # 날씨 정보 파싱
                temp = weather_response["main"]["temp"]
                hum = weather_response["main"]["humidity"]
                w_desc = weather_response["weather"][0]["description"]
                wind = weather_response["wind"]["speed"]
                rain = weather_response.get("rain", {}).get("1h", 0)

                # 공기질 정보 파싱
                pm = air_response["list"][0]["components"]["pm2_5"]
                aqi = air_response["list"][0]["main"]["aqi"]

                # 옷차림 및 마스크 문구 생성
                outfit_txt, mask = generate_recommendation(temp, rain, pm)

                # DB에 삽입
                cursor.execute("""
                    INSERT INTO outfit (
                        city, nation, temp, t_min, t_max, hum, w_desc, wind, rain, pm,
                        mask, umbrella, outfit_txt, time
                    ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, NOW())
                """, (
                    city["name"], "South Korea", temp, temp - 2, temp + 2, hum,
                    w_desc, wind, rain, pm, mask, rain > 0, outfit_txt
                ))
                db.commit()
        except Exception as e:
            print(f" 오류 발생: {e}")

    # ✅ 버스 정보 수집 및 DB 저장
    try:
        bus_api_key = "j8/UXQG00H5AhO2xHF2FHkcx4u10IRXRrKHD5NSSSbyYLEOKZ4KJoqMAW24mNB6+pNBDTzB3Cjc4fgLPVxMO7Q=="
        bus_url = f"http://apis.data.go.kr/6270000/dbmsapi01?serviceKey={bus_api_key}&numOfRows=10&pageNo=1"
        bus_response = requests.get(bus_url).json()

        if bus_response and bus_response.get('items'):
            for bus_data in bus_response['items']:
                # 버스 정보 파싱
                route = bus_data['routeId']
                station = bus_data['stationName']
                arr = bus_data['arriveTime']
                dep = bus_data['departureTime']
                dest = bus_data['destination']

                # DB 삽입
                cursor.execute("""
                    INSERT INTO transport (type, route, station, arr, dep, dest, time)
                    VALUES ('bus', %s, %s, %s, %s, %s, NOW())
                """, (route, station, arr, dep, dest))
                db.commit()
    except Exception:
        pass

# ✅ CSV 지하철 시간표 → DB로 삽입
def insert_subway_schedule_csv(path, db):
    cur = db.cursor()
    try:
        df = pd.read_csv(path, encoding='euc-kr')
        df = df.loc[:, ~df.columns.str.contains('^Unnamed')]

        # ✅ '요일별' → 요일, 방향 나누기
        df['day'] = df['요일별'].str.extract(r'(평일|매일)')
        df['to_dest'] = df['요일별'].str.extract(r'\((상|하)\)')

        # ✅ melt로 시간데이터 세로로 변환
        df_long = df.melt(
            id_vars=['line', 'day', 'to_dest', '역명', '구분'],
            var_name='timecode', value_name='time_val'
        ).dropna()

        # ✅ 시간 포맷 필터링 (HH:MM 또는 HH:MM:SS)
        def is_valid_time(t):
            return isinstance(t, str) and re.match(r'^\d{1,2}:\d{2}(:\d{2})?$', t)

        df_long = df_long[df_long['time_val'].apply(is_valid_time)]

        # ✅ INSERT 실행
        for _, row in df_long.iterrows():
            try:
                day = row['day'] if row['day'] in ['평일', '매일'] else '매일'

                cur.execute("""
                    INSERT INTO subway (line, station, timecode, time_val, type, day, to_dest)
                    VALUES (%s, %s, %s, %s, %s, %s, %s)
                """, (
                    row['line'], row['역명'], row['timecode'], row['time_val'],
                    row['구분'], day, row['to_dest']
                ))
                db.commit()
                print(f"✅ 삽입: {row['역명']} - {row['구분']} {row['timecode']} → {row['time_val']}")
            except Exception as e:
                print(f"❌ INSERT 실패: {row['역명']} - {e}")
    except Exception as e:
        print(f"📂 CSV 처리 에러: {e}")
        
# ✅ 내일 일정에 대해 알림 출력
def notify_upcoming_schedules():
    now = datetime.now()
    tomorrow = now + timedelta(days=1)
    tomorrow_str = tomorrow.strftime('%Y-%m-%d')

    cursor.execute("""
        SELECT u.uname, s.title, s.s_time
        FROM schedule s
        JOIN users u ON s.uid = u.id
        WHERE s.s_date = %s AND s.alarm = TRUE
    """, (tomorrow_str,))
    rows = cursor.fetchall()
    for row in rows:
        print(f"알림: 사용자 {row[0]}의 일정 '{row[1]}'이 내일 {row[2]}에 예정되어 있습니다.")

# ✅ 사용자의 동일 시간대 질문 기록을 루틴 후보로 저장
def update_routine_candidates(uid, qTime):
    today = datetime.now().date()
    cursor.execute("""
        SELECT id, qCnt
        FROM routine_tmp
        WHERE uid = %s AND qTime = %s AND qDate = %s
    """, (uid, qTime, today))
    row = cursor.fetchone()

    if row:
        # 이미 존재 → 카운트 증가
        new_count = row[1] + 1
        cursor.execute("""
            UPDATE routine_tmp
            SET qCnt = %s
            WHERE id = %s
        """, (new_count, row[0]))
    else:
        # 새로 등록
        cursor.execute("""
            INSERT INTO routine_tmp (uid, qTime, qDate, qCnt)
            VALUES (%s, %s, %s, 1)
        """, (uid, qTime, today))
    db.commit()

# ✅ 일정한 시간대에 반복된 질문이 일정 횟수(기본 3회) 이상이면 자동 루틴 등록
def register_frequent_routines(threshold=3):
    cursor.execute("""
        SELECT uid, qTime
        FROM routine_tmp
        WHERE qCnt >= %s AND auto_routine = FALSE
    """, (threshold,))
    rows = cursor.fetchall()

    for uid, r_time in rows:
        r_name = f"AutoRoutine_{r_time.strftime('%H%M')}"
        r_days = "Mon,Tue,Wed,Thu,Fri,Sat,Sun"  # 전체 요일 포함

        cursor.execute("""
            INSERT INTO routine (uid, r_name, r_time, r_days)
            VALUES (%s, %s, %s, %s)
        """, (uid, r_name, r_time, r_days))

        # routine_tmp 테이블에 auto_routine 플래그 true로 변경
        cursor.execute("""
            UPDATE routine_tmp
            SET auto_routine = TRUE
            WHERE uid = %s AND qTime = %s
        """, (uid, r_time))

        print(f"루틴등록 완료: 사용자 {uid} - 시간 {r_time}")
    db.commit()

# ✅ 자동 알림 및 루틴 등록 실행
notify_upcoming_schedules()
register_frequent_routines()

# ✅ 커서와 DB 연결 종료
try:
    cursor.close()
    db.close()
except:
    pass
