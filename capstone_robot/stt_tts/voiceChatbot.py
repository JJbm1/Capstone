import speech_recognition as sr
import requests
from gtts import gTTS

from flask_weather_client import ask_weather
from flask_bus_client import ask_bus
from flask_subway_client import ask_subway
from flask_outfit_client import ask_outfit
from flask_direction_client import ask_direction
from flask_schedule_client import extract_schedule_info, add_schedule, get_today_schedule

import os
import tempfile
import time
import sys

# 종료 키워드
exit_trigger_words = [
    "종료할게", "끝내자", "종료하자", "그만하자", "끄자", "그만",
    "종 료 할게", "종료 해", "끝", "종료 할게", "종뇨할게", "종뇨 할게",
    "끝내자", "끈 내자", "끝 내자", "끝낼래", "끝 낼래", "그만할래", "그만 할래"
]

# 길찾기 관련 키워드/패턴들
direction_keywords = [
    "어떻게 가", "가는 길", "길 찾아줘", "길 좀",
    "에서", "까지", "어디서", "어디까지", "어디로", "가는 방법", "로 가", "으로 가"
]

# 음성 인식 (STT)
def listen():
    recognizer = sr.Recognizer()
    mic = sr.Microphone()

    with mic as source:
        print("🎤 [듣기 시작] 마이크 대기 중....")
        recognizer.adjust_for_ambient_noise(source)
        audio = recognizer.listen(source, timeout=7, phrase_time_limit=10)

    try:
        text = recognizer.recognize_google(audio, language='ko-KR')
        print(f"[최종 인식된 문장] {text}")

        if any(word in text for word in exit_trigger_words):

            print("[종료 감지] 종료 관련 문장 포함됨")
            sys.exit(99)

        return text
    except sr.UnknownValueError:
        return "음성을 인식하지 못했어요."
    except sr.RequestError:
        return "STT 서버 요청에 실패했어요."

# TTS 재생 (mpg321 사용)
def speak(text):
    tts = gTTS(text=text, lang='ko')
    with tempfile.NamedTemporaryFile(delete=False, suffix=".mp3") as fp:
        temp_path = fp.name
        tts.save(temp_path)

    os.system(f"mpg321 {temp_path} > /dev/null 2>&1")
    os.remove(temp_path)

# 길찾기 키워드 포함
def contains_direction_keywords(message: str) -> bool:
    return any(keyword in message for keyword in direction_keywords)

# 실행 흐름
if __name__ == '__main__':
    while True:
        user_message = listen()

        # 종료 조건 추가 (예: "끝" 이라고 말하면 종료)
        if any(trigger in user_message for trigger in exit_trigger_words):
            print("챗봇 종료합니다.")
            break

        # 날씨 관련 질문
        if "날씨" in user_message or "기온" in user_message:
            gpt_response = ask_weather(user_message)

        # 옷차림 관련 질문
        elif "옷" in user_message or "옷차림" in user_message or "입" in user_message:
            outfit_response = ask_outfit(user_message)
            print(f"🧥 옷차림 응답: {outfit_response}")
            speak(outfit_response)
            continue

        # 버스 관련 질문
        elif "버스" in user_message or "정류장" in user_message:
            gpt_response = ask_bus(user_message)

        # 길찾기 관련 질문
        elif contains_direction_keywords(user_message):
            gpt_response = ask_direction(user_message)

        # 지하철 관련 질문
        elif "지하철" in user_message or "호선" in user_message or "역" in user_message:
            gpt_response = ask_subway(user_message)
            print("🎤 지하철 응답:", gpt_response)

        # 일정 등록 관련 질문 처리 예시
        elif any(x in user_message for x in ["일정 등록", "일정 추가"]):
            username = "testuser"  # TODO: 실제 사용자 이름 사용 시 교체
            date, time_str, content = extract_schedule_info(user_message)
            response = add_schedule(username, date, time_str, content)
            print(f"📅 일정 등록 응답: {response}")
            speak(response)
            continue

        # 오늘 일정 확인 질문 처리 예시
        elif any(x in user_message for x in ["오늘 일정", "오늘 뭐 있어", "오늘 뭐해"]):
            response = get_today_schedule()
            print(f"📅 오늘 일정 응답: {response}")
            speak(response)
            continue

        else:
            gpt_response = "다시 질문해주세요."
            
        if gpt_response:
            print(f"🤖 GPT 응답: {gpt_response}")
            speak(gpt_response)

        time.sleep(0.1)  # 약간의 텀 (옵션)
