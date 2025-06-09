import 'dart:convert';
import 'package:http/http.dart' as http;

Future<String> fetchBusArrival(String stationName, String busNumber) async {
  final uri = Uri.parse("http://10.32.39.195:8080/api/bus/arrival");
  final response = await http.post(
    uri,
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({
      "stationName": stationName,
      "busNumber": busNumber,
    }),
  );

  if (response.statusCode == 200) {
    final json = jsonDecode(response.body);
    return json['data'];
  } else {
    throw Exception('버스 도착 정보 요청 실패');
  }
}

Future<String> fetchSubwayInfo(String line, String station) async {
  final uri = Uri.parse("http://10.0.2.2:8080/api/subway/info");
  final response = await http.post(
    uri,
    headers: {'Content-Type': 'application/json'},
    body: jsonEncode({
      "line": line,
      "station": station,
    }),
  );

  if (response.statusCode == 200) {
    final json = jsonDecode(response.body);
    return json['data'];
  } else {
    throw Exception('지하철 정보 요청 실패');
  }
}
