import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

class ConnectionStatusWidget extends StatefulWidget {
  const ConnectionStatusWidget({super.key});

  @override
  _ConnectionStatusWidgetState createState() => _ConnectionStatusWidgetState();
}

class _ConnectionStatusWidgetState extends State<ConnectionStatusWidget> {
  bool isConnected = false;
  final String raspberryPiIP = "http://192.168.137.185";

  Future<void> _checkConnection() async {
    try {
      final response = await http.get(Uri.parse('$raspberryPiIP/ping')).timeout(const Duration(seconds: 2));
      setState(() {
        isConnected = response.statusCode == 200;
      });
    } catch (_) {
      setState(() {
        isConnected = false;
      });
    }
  }

  @override
  void initState() {
    super.initState();
    _checkConnection();
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(isConnected ? Icons.wifi : Icons.wifi_off,
            color: isConnected ? Colors.green : Colors.red),
        const SizedBox(width: 8),
        Text(
          isConnected ? "라즈베리파이 연결됨" : "연결되지 않음",
          style: TextStyle(
            color: isConnected ? Colors.green : Colors.red,
            fontWeight: FontWeight.bold,
          ),
        ),
        IconButton(
          icon: const Icon(Icons.refresh),
          onPressed: _checkConnection,
          tooltip: "연결 상태 새로고침",
        )
      ],
    );
  }
}
