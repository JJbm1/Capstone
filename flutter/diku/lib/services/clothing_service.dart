import 'dart:convert';
import 'package:http/http.dart' as http;

/// 옷차림 추천 요청 (GET 방식, 쿼리 파라미터 포함)
Future<String> fetchClothingRecommendation({
  required int temperature,
  required double rain,
  required double pm25,
}) async {
  final uri = Uri.parse(
    'http://10.0.2.2:8080/api/outfit/recommend?temperature=$temperature&rain=$rain&pm25=$pm25',
  );

  try {
    final response = await http.get(uri);

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body);
      // data 필드가 String인지, Map인지 상황에 맞게 처리
      if (data['data'] is String) {
        return data['data']!;
      } else if (data['data'] is Map && data['data']['outfit_txt'] != null) {
        return data['data']['outfit_txt'];
      } else {
        return '추천 결과를 가져올 수 없습니다.';
      }
    } else {
      return '❌ 서버 오류: ${response.statusCode}';
    }
  } catch (e) {
    return '⚠️ 요청 실패: $e';
  }
}
