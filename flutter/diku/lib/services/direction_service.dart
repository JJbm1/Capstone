import 'dart:convert';
import 'package:http/http.dart' as http;

Future<String> fetchDirectionInfo(String start, String end) async {
  final uri = Uri.parse("http://10.0.2.2:8080/api/direction/find");
  final response = await http.post(
    uri,
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({
      "start": start,
      "end": end,
    }),
  );

  if (response.statusCode == 200) {
    final json = jsonDecode(response.body);
    return json['data'];
  } else {
    throw Exception('길찾기 요청 실패');
  }
}
