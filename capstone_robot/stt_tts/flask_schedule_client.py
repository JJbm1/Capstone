# 일정 등록/조회 클라이언트
# 이 코드는 Flask 서버와 통신하여 일정을 등록하고 조회하는 기능을 제공
import requests
import re
from datetime import datetime

BASE_URL = "http://10.32.35.29:8080/chat/schedule"  # Spring 서버 IP

def extract_schedule_info(message):
    date_pattern = r"(\d{1,2})월\s*(\d{1,2})일|\b(\d{1,2})/(\d{1,2})\b"
    date_match = re.search(date_pattern, message)

    month, day = None, None
    if date_match:
        if date_match.group(1) and date_match.group(2):
            month = int(date_match.group(1))
            day = int(date_match.group(2))
        elif date_match.group(3) and date_match.group(4):
            month = int(date_match.group(3))
            day = int(date_match.group(4))

    time_pattern = r"(\d{1,2})시\s*(\d{1,2})?분?|\b(\d{1,2}):(\d{1,2})\b"
    time_match = re.search(time_pattern, message)

    hour, minute = 9, 0
    if time_match:
        if time_match.group(1):
            hour = int(time_match.group(1))
            minute = int(time_match.group(2) or 0)
        elif time_match.group(3):
            hour = int(time_match.group(3))
            minute = int(time_match.group(4))

    now = datetime.now()
    if month and day:
        try:
            date_obj = datetime(now.year, month, day)
        except ValueError:
            date_obj = now
    else:
        date_obj = now

    content = re.sub(date_pattern, "", message)
    content = re.sub(time_pattern, "", content)
    content = content.replace("일정 등록", "").replace("일정 추가", "").strip()

    # 날짜/시간 표현 제거
    content = re.sub(r"(내일|모레|오늘|어제|오전|오후|\d{1,2}월|\d{1,2}일|\d{1,1,2}:\d{1,2}|\d{1,2}시\s*\d{0,2}분?)", "", content)

    # 명령어 제거
    content = re.sub(r"(일정\s*(등록|추가)|알려\s*줘|등록\s*해\s*줘|해\s*줘|추가\s*해\s*줘|넣어\s*줘|해줄래|추가해줄래)", "", content)


    # 조사 제거
    content = re.sub(r"^(에|를|을|은|는|이|가)\s*", "", content)

    # 공백 제거
    content = content.strip()

    # 기본값 처리
    if not content:
        content = "일정"

    return date_obj.strftime("%Y-%m-%d"), f"{hour:02d}:{minute:02d}:00", content

def get_today_schedule():
    try:
        res = requests.get(f"{BASE_URL}/today", timeout=20)
        if res.status_code == 200:
            return res.json().get("data", "오늘 일정이 없어요.")
        else:
            return f"오류 발생: {res.status_code}"
    except Exception as e:
        return f"오류 발생: {e}"

def get_tomorrow_schedule():
    # 향후 구현 가능
    return "내일 일정 기능은 아직 준비 중입니다."

def add_schedule(username, date, time, content):
    url = BASE_URL
    params = {
        "username": username,
        "date": date,
        "time": time,
    }
    headers = {"Content-Type": "text/plain; charset=utf-8"}
    try:
        response = requests.post(url, params=params, data=content.encode('utf-8'), headers=headers, timeout=20)
        if response.status_code == 200:
            return response.json().get("data", "응답 없음")
        else:
            return f"오류 발생: {response.status_code}"
    except Exception as e:
        return f"요청 중 오류 발생: {e}"
