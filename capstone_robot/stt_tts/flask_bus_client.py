# 버스 정보 요청 클라이언트
import requests

def ask_bus(message):
    spring_url = "http://10.32.35.29:8080/rasp/bus"
    try:
        response = requests.get(spring_url, params={"message": message}, timeout=25)
        if response.status_code == 200:
            return response.json().get("data", "응답 없음")
        else:
            return f"❌ 상태 코드 오류: {response.status_code}"
    except Exception as e:
        return f"요청 실패: {e}"
