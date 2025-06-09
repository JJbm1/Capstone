import os
import time

# 가상환경 Python 실행 파일 경로를 상수로 지정
VENV_PYTHON = "/home/pi/capstone/bin/python3"
# wakeupListener.py가 있는 기본 디렉토리 경로
BASE_DIR    = "/home/pi"

print("🤖 DIRO 음성 비서 시작 (반복 대기 모드)")

# 무한 반복 루프: 계속해서 웨이크워드를 대기
while True:
    print("👂 웨이크워드 '디로야' 대기 중...")
    # wakeupListener.py 실행 (os.system으로 서브 프로세스 실행)
    ret_code = os.system(f"{VENV_PYTHON} {os.path.join(BASE_DIR, 'wakeupListener.py')}")

    # 🛑 종료 코드가 99면 루프 종료 (사용자가 종료 요청한 경우)
    if os.WEXITSTATUS(ret_code) == 99:
        print("🛑 사용자가 종료를 요청했습니다. 루프를 종료합니다.")
        break

    print("🔁 명령 완료. 다시 대기 중...\n")
    time.sleep(2)  # 너무 빠르게 반복되지 않도록 잠깐 대기
