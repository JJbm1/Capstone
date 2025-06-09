# 루틴 알림용 Flask 서버
from flask import Flask, request, jsonify
import logging
from gtts import gTTS
import subprocess
import tempfile
import os

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

@app.route("/notify", methods=["POST"])
def notify():
    """
    JSON 바디 형식: {"user": "...", "msg": "..."}
    1) "user님, msg" 문장으로 gTTS 한국어 MP3 생성
    2) mpg123으로 MP3 재생
    3) 임시 파일 삭제
    """
    data = request.get_json()
    if not data:
        return jsonify({"error": "Invalid JSON"}), 400

    user = data.get("user")
    msg  = data.get("msg")
    if not user or not msg:
        return jsonify({"error": "user 또는 msg 키가 없습니다."}), 400

    text_to_speak = f"{user}님, {msg}"
    app.logger.info(f"▶ Flask 수신 → {text_to_speak}")

    try:
        # 1) 임시 MP3 파일 생성
        #    suffix=".mp3"로 하면 NamedTemporaryFile()이 자동으로 .mp3 확장자를 줌
        with tempfile.NamedTemporaryFile(suffix=".mp3", delete=False) as fp:
            tmp_mp3_path = fp.name

        # 2) gTTS를 이용하여 한국어 음성으로 MP3 파일 생성
        tts = gTTS(text=text_to_speak, lang='ko')
        tts.save(tmp_mp3_path)

        # 3) mpg123으로 생성된 MP3 파일 재생
        #    -q: 조용히 (progress bar 등 출력 억제)
        subprocess.call(["mpg123", "-q", tmp_mp3_path])
        
        # 4) 재생 완료 후 임시 파일 삭제
        os.remove(tmp_mp3_path)

        return jsonify({"status": "success"}), 200

    except Exception as e:
        app.logger.error("TTS 출력 중 에러", exc_info=True)
        # 예외 발생 시에도 임시 파일이 남아 있을 수 있으므로, 제거 시도
        try:
            if 'tmp_mp3_path' in locals() and os.path.exists(tmp_mp3_path):
                os.remove(tmp_mp3_path)
        except:
            pass
        return jsonify({"error": str(e)}), 500


if __name__ == "__main__":
    # 외부에서 접근 가능하도록 host=0.0.0.0
    app.run(host="0.0.0.0", port=5001)
