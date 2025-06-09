import "package:http/http.dart" as http;

Future<void> testServer() async {
  final uri = Uri.parse("http://localhost:8080/api/ping");

  try {
    final response = await http.get(uri);

    if (response.statusCode == 200) {
      print('서버 응답 : ${response.body}');
    } else {
      print('서버 에러 : ${response.statusCode}');
    }
  } catch (e) {
    print('연결 실패 : $e');
  }
}