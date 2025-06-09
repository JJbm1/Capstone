# 길찾기 요청을 보내는 Flask 클라이언트
import requests

SPRING_SERVER_URL = "http://10.32.35.29:8080/chat/transport"

def ask_direction(message: str) -> str:
    try:
        # timeout을 충분히 늘려줌
        response = requests.get(SPRING_SERVER_URL, params={"message": message}, timeout=60)
        response.raise_for_status()

        # JSON 응답에서 "data" 키 추출
        return response.json().get("data", "길찾기 정보를 불러오지 못했어요.")

    except requests.exceptions.ReadTimeout:
        return "서버 응답이 너무 오래 걸립니다. 나중에 다시 시도해주세요."

    except requests.exceptions.RequestException as e:
        print(f"[ERROR] 길찾기 요청 실패: {e}")
        return "서버와 연결할 수 없습니다. 다시 시도해주세요."
