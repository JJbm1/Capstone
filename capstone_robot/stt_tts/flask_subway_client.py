# 지하철 관련 질문을 Spring 서버에 보내고 응답을 받는 클라이언트 코드
import requests

def ask_subway(message):
    """
    STT로 인식된 자연어 메시지를 Spring 서버에 보내고,
    지하철 응답을 받아 반환합니다.
    """
    try:
        # 백엔드 IP 주소 넣기
        url = "http://10.32.35.29:8080/api/gpt/ask"
        params = {"message": message}
        response = requests.get(url, params=params)

        if response.status_code == 200:
            return response.text
        else:
            return f"서버 오류: {response.status_code}"
    except Exception as e:
        return f"요청 중 예외 발생: {e}"
