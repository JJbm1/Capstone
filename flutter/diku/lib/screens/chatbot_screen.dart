import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ChatBotScreen extends StatefulWidget {
  final String feature;
  const ChatBotScreen({super.key, required this.feature});

  @override
  _ChatBotScreenState createState() => _ChatBotScreenState();
}

class _ChatBotScreenState extends State<ChatBotScreen> {
  final TextEditingController _controller = TextEditingController();
  List<Map<String, String>> messages = [];

  // ──────────── 1) 지역명 추출 함수 ────────────
  String extractRegion(String message) {
    if (message.contains("대구")) return "Daegu";
    if (message.contains("하양")) return "Hayang";
    return "";
  }

  // ──────────── 2) 날씨 요약 ────────────
  Future<String> fetchChatWeather(String region) async {
    try {
      final uri = Uri.parse(
        'http://10.0.2.2:8080/chat/weather?message=${Uri.encodeComponent("$region 날씨 알려줘")}',
      );
      final response = await http.get(uri);
      final decodedBody = utf8.decode(response.bodyBytes);
      final data = jsonDecode(decodedBody);
      return data['data'] ?? '날씨 정보를 불러올 수 없습니다.';
    } catch (e) {
      return "날씨 정보를 가져오지 못했습니다.";
    }
  }

  // ──────────── 3) 옷차림 추천 ────────────
  Future<String> fetchOutfitRecommend(
      String city,
      int temperature,
      double rain,
      double pm25
      ) async {
    try {
      final uri = Uri.parse(
        'http://10.0.2.2:8080/api/outfit/recommend'
            '?city=${Uri.encodeComponent(city)}'
            '&temperature=$temperature'
            '&rain=$rain'
            '&pm25=$pm25',
      );
      final response = await http.get(uri);
      final decodedBody = utf8.decode(response.bodyBytes);
      final data = jsonDecode(decodedBody);
      return data['data'] ?? '옷차림 정보를 불러올 수 없습니다.';
    } catch (e) {
      return "옷차림 정보를 가져오지 못했습니다.";
    }
  }

  // ──────────── 4) 교통 요청 ────────────
  Future<String> fetchTransport(String message) async {
    try {
      final uri = Uri.parse(
        'http://10.0.2.2:8080/chat/transport?message=${Uri.encodeComponent(message)}',
      );
      final response = await http.get(uri);
      final decodedBody = utf8.decode(response.bodyBytes);
      final data = jsonDecode(decodedBody);
      return data['data'] ?? "교통 정보를 불러올 수 없습니다.";
    } catch (e) {
      return "교통 정보를 가져오지 못했습니다.";
    }
  }

  // ──────────── 4-1) 길찾기 요청 (수정된 부분) ────────────
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

  // ──────────── 5) 사용자가 전송 버튼을 눌렀을 때 처리 ────────────
  void sendMessage() async {
    String userMessage = _controller.text.trim();
    if (userMessage.isEmpty) return;

    setState(() {
      messages.add({"sender": "user", "message": userMessage});
      _controller.clear();
    });

    String botMessage = "";

    try {
      // (A) 날씨 + 옷차림 동시에 물어볼 때
      if (userMessage.contains("날씨") && userMessage.contains("옷차림")) {
        String region = extractRegion(userMessage);
        if (region.isNotEmpty) {
          final summary = await fetchChatWeather(region);
          setState(() {
            messages.add({"sender": "bot", "message": summary});
          });
          final weatherData = await fetchWeatherData(region);
          final temp = (weatherData['temperature'] as num).toDouble();
          final rain = (weatherData['rain'] as num).toDouble();
          final pm25 = (weatherData['pm25'] as num).toDouble();

          final outfitText = await fetchOutfitRecommend(
              region,
              temp.toInt(),
              rain,
              pm25
          );
          botMessage = outfitText;
        } else {
          botMessage = "지역을 입력해주세요. 예: '대구 날씨 어때? 옷차림 추천해줘'";
        }
      }
      // (B) 날씨만 물어볼 때
      else if (userMessage.contains("날씨")) {
        String region = extractRegion(userMessage);
        if (region.isNotEmpty) {
          botMessage = await fetchChatWeather(region);
        } else {
          botMessage = "지역을 입력해주세요. 예: '대구 날씨 어때?'";
        }
      }
      // (C) 옷차림만 물어볼 때
      else if (userMessage.contains("옷차림") || userMessage.contains("옷")) {
        String region = extractRegion(userMessage);
        if (region.isNotEmpty) {
          final weatherData = await fetchWeatherData(region);
          final temp = (weatherData['temperature'] as num).toDouble();
          final rain = (weatherData['rain'] as num).toDouble();
          final pm25 = (weatherData['pm25'] as num).toDouble();

          botMessage = await fetchOutfitRecommend(
              region,
              temp.toInt(),
              rain,
              pm25
          );
        } else {
          botMessage = "지역을 입력해주세요. 예: '대구 옷차림 알려줘'";
        }
      }
      // (D) 길찾기 질문일 때
      else if (userMessage.contains("어떻게 가") || userMessage.contains("길 알려줘")) {
        RegExp reg = RegExp(r'(.+)에서 (.+)까지');
        var match = reg.firstMatch(userMessage);
        if (match != null) {
          String start = match.group(1)!.trim();
          String end = match.group(2)!.trim();
          botMessage = await fetchDirectionInfo(start, end);
        } else {
          botMessage = "출발지와 도착지를 '출발지에서 도착지까지' 형식으로 입력해주세요.";
        }
      }
      // (E) 그 외는 기존 교통 정보 처리
      else {
        botMessage = await fetchTransport(userMessage);
      }
    } catch (e) {
      botMessage = "오류가 발생했습니다: $e";
    }

    setState(() {
      messages.add({"sender": "bot", "message": botMessage});
    });
  }

  // ──────────── 6) 날씨 데이터(원본)만 가져오는 함수 ────────────
  Future<Map<String, dynamic>> fetchWeatherData(String region) async {
    try {
      final uri = Uri.parse(
        'http://10.0.2.2:8080/api/weather?region=${Uri.encodeComponent(region)}',
      );

      final response = await http.get(uri);

      final decodedBody = utf8.decode(response.bodyBytes);

      final data = jsonDecode(decodedBody);
      return data as Map<String, dynamic>;
    } catch (e) {
      return {
        "weather": "알 수 없음",
        "temperature": 0.0,
        "rain": 0.0,
        "pm25": 0.0,
      };
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("${widget.feature} 챗봇")),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: messages.length,
              padding: const EdgeInsets.symmetric(vertical: 12),
              itemBuilder: (context, index) {
                var message = messages[index];
                bool isUserMessage = message["sender"] == "user";
                return Align(
                  alignment:
                  isUserMessage ? Alignment.centerRight : Alignment.centerLeft,
                  child: Container(
                    padding: const EdgeInsets.all(12),
                    margin:
                    const EdgeInsets.symmetric(vertical: 8, horizontal: 12),
                    decoration: BoxDecoration(
                      color: isUserMessage ? Colors.blueAccent : Colors.grey.shade300,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Text(
                      message["message"]!,
                      style: TextStyle(
                        color: isUserMessage ? Colors.white : Colors.black,
                        fontSize: 16,
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(
                      hintText: "메시지를 입력하세요...",
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.all(Radius.circular(8)),
                        borderSide: BorderSide.none,
                      ),
                      filled: true,
                      fillColor: Colors.white,
                    ),
                    onSubmitted: (_) => sendMessage(),
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  decoration: BoxDecoration(
                    color: Colors.blueAccent,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: IconButton(
                    icon: const Icon(Icons.send, color: Colors.white),
                    onPressed: sendMessage,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 10),
        ],
      ),
    );
  }
}
