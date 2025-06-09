import 'dart:convert';
import 'package:http/http.dart' as http;

/// GPT 응답 요청 서비스
Future<String> fetchAIResponse(String query, String feature) async {
  final response = await http.post(
    Uri.parse("http://10.0.2.2:8080/chat/$feature?message=$query"),
  );

  if (response.statusCode == 200) {
    final data = json.decode(response.body);
    return data['data'];
  } else {
    return "AI 응답 요청 실패: ${response.statusCode}";
  }
}
