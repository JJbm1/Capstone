import 'dart:convert';
import 'package:http/http.dart' as http;

class WeatherService {
  // ✅ 에뮬레이터에서는 localhost 대신 10.0.2.2 사용
  static const String baseUrl = 'http://10.0.2.2:8080/chat/weather';

  // ✅ 사용자 메시지로 날씨 요청
  Future<String> fetchWeather(String message) async {
    try {
      final uri = Uri.parse('$baseUrl?message=$message');
      final response = await http.get(uri);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['data'] ?? '날씨 정보를 불러올 수 없습니다.';
      } else {
        return '❌ 서버 오류: ${response.statusCode}';
      }
    } catch (e) {
      return '⚠️ 요청 실패: $e';
    }
  }
}
