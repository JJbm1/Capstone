import 'dart:convert';
import 'package:http/http.dart' as http;

class RoutineService {
  static const String baseUrl = "http://10.0.2.2:8080";

  /// 루틴 등록 요청
  static Future<String> registerRoutine({
    required String username,
    required String rname,
    required String rtime,
    required String rdays, // 예: "Mon,Tue,Wed"
  }) async {
    final uri = Uri.parse("$baseUrl/chat/routine/register");

    final body = json.encode({
      "username": username,
      "rname": rname,
      "rtime": rtime,
      "rdays": rdays,
    });

    try {
      final response = await http.post(
        uri,
        headers: {'Content-Type': 'application/json'},
        body: body,
      );

      if (response.statusCode == 200) {
        return json.decode(response.body)["data"];
      } else {
        return "❌ 등록 실패: ${response.statusCode}";
      }
    } catch (e) {
      return "⚠️ 오류: $e";
    }
  }

  /// 루틴 목록 불러오기
  static Future<List<Map<String, dynamic>>> fetchRoutineList(String username) async {
    final uri = Uri.parse("$baseUrl/chat/routine/list?username=$username");

    try {
      final response = await http.get(uri);
      if (response.statusCode == 200) {
        return List<Map<String, dynamic>>.from(json.decode(response.body));
      } else {
        return [];
      }
    } catch (e) {
      return [];
    }
  }

  /// 자동 등록된 루틴 목록 불러오기 (RoutineRegisterScreen용)
  static Future<List<Map<String, dynamic>>> fetchAutoRoutines(String username) async {
    final uri = Uri.parse("$baseUrl/chat/routine/auto-list?username=$username");

    try {
      final response = await http.get(uri);
      if (response.statusCode == 200) {
        return List<Map<String, dynamic>>.from(json.decode(response.body)["data"]);
      } else {
        return [];
      }
    } catch (e) {
      return [];
    }
  }
}
