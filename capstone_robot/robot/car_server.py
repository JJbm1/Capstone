from flask import Flask, request, jsonify  # ✅ jsonify 꼭 포함
from flask_cors import CORS
import serial
import time

app = Flask(__name__)
CORS(app)

SERIAL_PORT = '/dev/ttyACM0'  # 상황에 따라 변경 필요
BAUD_RATE = 9600

try:
    arduino = serial.Serial(SERIAL_PORT, BAUD_RATE, timeout=1)
    time.sleep(2)
    print("✅ 아두이노 시리얼 연결 성공")
except Exception as e:
    arduino = None
    print(f"❌ 아두이노 연결 실패: {e}")

@app.route('/control', methods=['GET'])
def control():
    command = request.args.get('command', '').strip().lower()
    allowed = ['forward', 'backward', 'left', 'right', 'stop']

    if command not in allowed:
        return jsonify({'status': 'error', 'message': 'Invalid command'}), 400

    if arduino:
        try:
            arduino.write((command + '\n').encode())
            print(f"📤 명령 전송됨: {command}")
            return jsonify({'status': 'ok', 'command': command})
        except Exception as e:
            print(f"❌ 시리얼 전송 실패: {e}")
            return jsonify({'status': 'error', 'message': str(e)}), 500
    else:
        print("❌ 아두이노 객체 없음")
        return jsonify({'status': 'error', 'message': 'Arduino not connected'}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
