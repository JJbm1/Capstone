import 'dart:convert';
import 'package:http/http.dart' as http;

/// 일정 등록
Future<String> registerSchedule({
  required String username,
  required String date,
  required String time,
  required String content,
}) async {
  final uri = Uri.parse('http://10.0.2.2:8080/chat/schedule?username=$username&date=$date&time=$time');

  try {
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'text/plain'},
      body: content,
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['data'] ?? '일정 등록 실패';
    } else {
      return '❌ 등록 오류: ${response.statusCode}';
    }
  } catch (e) {
    return '⚠️ 요청 실패: $e';
  }
}

/// 날짜 파라미터 추가 + UTF-8 디코딩 적용
Future<List<Map<String, dynamic>>> fetchScheduleList(String username, String date) async {
  final uri = Uri.parse('http://10.0.2.2:8080/chat/schedule/list?username=$username&date=$date');

  try {
    final response = await http.get(uri);

    if (response.statusCode == 200) {
      final jsonString = utf8.decode(response.bodyBytes);  // UTF-8 디코딩 중요
      return List<Map<String, dynamic>>.from(json.decode(jsonString));
    } else {
      return [];
    }
  } catch (e) {
    return [];
  }
}

/// 일정 삭제
Future<String> deleteSchedule(int scheduleId) async {
  final uri = Uri.parse('http://10.0.2.2:8080/chat/schedule/$scheduleId');
  try {
    final response = await http.delete(uri);
    return response.statusCode == 200 ? "삭제 완료" : "❌ 삭제 실패";
  } catch (e) {
    return "⚠️ 삭제 실패: $e";
  }
}

/// 일정 수정
Future<String> updateSchedule({
  required int scheduleId,
  required String newContent,
}) async {
  final uri = Uri.parse('http://10.0.2.2:8080/chat/schedule/$scheduleId');
  try {
    final response = await http.put(
      uri,
      headers: {'Content-Type': 'text/plain'},
      body: newContent,
    );
    return response.statusCode == 200 ? "수정 완료" : "❌ 수정 실패";
  } catch (e) {
    return "⚠️ 수정 실패: $e";
  }
}

/// 태그별 일정 조회 (태그 URI 인코딩 추가)
Future<List<Map<String, dynamic>>> getSchedulesByTag(String tag) async {
  final encodedTag = Uri.encodeComponent(tag);  // 태그 인코딩 필수
  final uri = Uri.parse('http://10.0.2.2:8080/chat/schedule/by-tag?tag=$encodedTag');
  try {
    final response = await http.get(uri);
    if (response.statusCode == 200) {
      final jsonString = utf8.decode(response.bodyBytes);  // UTF-8 디코딩 적용
      return List<Map<String, dynamic>>.from(json.decode(jsonString));
    } else {
      return [];
    }
  } catch (e) {
    return [];
  }
}
