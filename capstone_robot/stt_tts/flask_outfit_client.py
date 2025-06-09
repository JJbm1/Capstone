# 옷차림 추천을 위한 Flask 클라이언트
import requests

def ask_outfit(message):
    # Java Spring 서버의 RaspOutfitController 엔드포인트 주소
    url = "http://10.32.35.29:8080/rasp/outfit"

    try:
        response = requests.get(url, params={"message": message}, timeout=30)

        if response.status_code == 200:
            data = response.json()
            return data.get("data", "응답 없음")
        else:
            return f"상태 코드 오류: {response.status_code}"
    except Exception as e:
        return f"요청 실패 : {e}"
