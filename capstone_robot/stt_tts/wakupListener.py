from vosk import Model, KaldiRecognizer         # Vosk 음성 인식 모듈
import sounddevice as sd                        # 마이크 입력 스트림 제어
import numpy as np                              # 수치 계산용
import queue                                    # 오디오 버퍼 처리를 위한 큐
import json                                     # JSON 데이터 처리 
import os                                       # 시스템 명령 실행
import time                                     # 시간 지연용
import resampy                                  # 샘플링 레이트 변환 (44100 → 16000)

# 가상환경 Python 경로 상수로 지정
VENV_PYTHON = "/home/pi/capstone/bin/python3"
# 스크립트가 있는 디렉토리
BASE_DIR    = "/home/pi"

# Vosk 한국어 모델 (16000Hz 기준)
model = Model("model")
# Kaldi 인식기 초기화 (샘플링 레이트: 16000Hz)
rec = KaldiRecognizer(model, 16000)
# 오디오 입력 데이터를 담을 큐 생성
q = queue.Queue()

# 웨이크워드 (유사 발음 포함) 리스트 정의
trigger_words = [
    "디로야", "디로야 준비됐어",
    "길어야", "기로야", "길어야 준비됐어", "기로야 준비됐어", "뒤로야",
    "티로야", "길 어야", "기 로야", "길 어야 준비됐어", "기 로야 준비됐어",
    "이 로야", "이러 냐", "이", "이", "기로 야", "기로야", "기로얌", "기로 얌",
    "뒤로 야", "뒤로 냐", "기로연", "기로 연", "빌어야", "빌 어야", "빌어 야",
    "기로 얌", "기로 약속", "비료 나", "비록 냐", "기루 냐", "기로 약",
    "기로 얀", "기 로 향","기로냐","기류 와", "들어야"
]

# 오디오 장치 설정
samplerate = 44100          # 마이크 입력 샘플링 주파수
device_index = 1            # 마이크 장치 인덱스 (arecord -l로 확인)

# 현재 사용 중인 오디오 장치 정보 출력
print("🎧 장치 정보:")
print(sd.query_devices(device_index))

# 오디오 콜백 함수 정의: 마이크 입력이 들어오면 큐에 저장
def callback(indata, frames, time, status):
    if status:
        print("⚠️", status)         # 에러나 상태 경고 출력
    q.put(indata.copy())            # 입력된 음성 데이터를 큐에 넣음

print("🎤 듣고 있어요... '디로야' 라고 말해보세요")

# ✅ 조용한 구간 감지용 변수
silence_count = 0
SILENCE_THRESHOLD = 20              # 20번 연속 부분 인식이 없으면 종료 간주(20번 이상 조용하면 종료 처리)

# 마이크 입력 스트림 시작
with sd.InputStream(samplerate=samplerate, blocksize=16000,
                    device=device_index, dtype='int16',
                    channels=1, latency='high',
                    callback=callback):

    while True:
        data = q.get()              # 큐에서 오디오 데이터 가져오기
        time.sleep(0.01)            # 짧은 대기

        # 오디오 데이터를 float32로 변환 후 리샘플링 (44100 → 16000)
        audio_float = data.astype(np.float32) / 32768.0
        resampled = resampy.resample(audio_float.flatten(), samplerate, 16000)
        resampled_int16 = (resampled * 32768).astype(np.int16).tobytes()

        # 완전한 문장이 인식되었을 경우
        if rec.AcceptWaveform(resampled_int16):
            result = json.loads(rec.Result())
            text = result.get("text", "")
            print("🗣️ 인식된 문장:", text)

            # 웨이크워드가 포함되면 호출로 판단
            if any(word in text for word in trigger_words):
                print("✅ 디로야 호출됨!")
                os.system("mpg321 called.mp3 > /dev/null 2>&1")  # 호출 음성 재생
                break

            silence_count = 0  # 인식 성공 → 조용 구간 초기화
        else:
            # 부분 인식된 단어 출력
            partial = json.loads(rec.PartialResult())
            partial_text = partial.get("partial", "")
            print("🧪 부분 인식:", partial_text)

            # 부분 인식 결과가 비어 있으면 조용하다고 판단 -- 조용 구간 카운트 증가
            if partial_text.strip() == "":
                silence_count += 1
            else:
                silence_count = 0

            # 일정 시간 조용하면 강제 종료 → FinalResult로 처리
            if silence_count > SILENCE_THRESHOLD:
                final_result = json.loads(rec.FinalResult())
                text = final_result.get("text", "")
                print("🗣️ [강제 종료된 문장]:", text)

                if any(word in text for word in trigger_words):
                    print("✅ 디로야 (강제) 호출됨!")
                    os.system("mpg321 called.mp3 > /dev/null 2>&1")
                    break

# 스트림 종료 후 음성 챗봇 호출 (voiceChatbot.py 실행)
os.system(f"{VENV_PYTHON} {os.path.join(BASE_DIR, 'voiceChatbot.py')}")
