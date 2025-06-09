# 날씨 정보를 요청하는 Flask 클라이언트
import requests

def ask_weather(message):
    spring_url = "http://10.32.35.29:8080/rasp/weather"
    try:
        response = requests.get(spring_url, params={"message": message}, timeout=15)

        if response.status_code == 200:
            data = response.json()
            return data.get("data", "응답 없음")
        else:
            return f"❌ 상태 코드 오류: {response.status_code}"
    except Exception as e:
        return f"요청 실패: {e}"
