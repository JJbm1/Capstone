import 'package:http/http.dart' as http;

class RaspberryPiService {
  static const String raspberryPiIP = "http://192.168.137.164:5000";

  /// 연결 상태 확인
  static Future<bool> checkConnection() async {
    try {
      final response = await http
          .get(Uri.parse('$raspberryPiIP/ping'))
          .timeout(const Duration(seconds: 2));
      return response.statusCode == 200;
    } catch (_) {
      return false;
    }
  }

  /// 명령 전송
  static Future<bool> sendCommand(String command) async {
    try {
      final url = Uri.parse('$raspberryPiIP/control?command=$command');
      final response = await http.get(url);
      return response.statusCode == 200;
    } catch (_) {
      return false;
    }
  }
}
