import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'connection_status_widget.dart'; // ✅ 연결 상태 위젯은 별도 파일에서 import

class RobotRemoteControlButtons extends StatefulWidget {
  const RobotRemoteControlButtons({super.key});

  @override
  _RobotRemoteControlButtonsState createState() => _RobotRemoteControlButtonsState();
}

class _RobotRemoteControlButtonsState extends State<RobotRemoteControlButtons> {
  final String raspberryPiIP = "http://192.168.137.185:5000";
  final List<String> _commandHistory = [];

  void _sendCommand(String command) async {
    final url = Uri.parse('$raspberryPiIP/control?command=$command');
    try {
      final response = await http.get(url);
      debugPrint("✅ '$command' 명령 전송 완료: \${response.statusCode}");

      setState(() {
        _commandHistory.insert(0, command);
        if (_commandHistory.length > 5) {
          _commandHistory.removeLast();
        }
      });
    } catch (e) {
      debugPrint("❌ 명령 전송 실패: \$e");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const ConnectionStatusWidget(),
        const SizedBox(height: 20),
        Container(
          padding: const EdgeInsets.all(30),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(30),
            boxShadow: [
              BoxShadow(
                color: Colors.grey.shade400,
                blurRadius: 12,
                offset: const Offset(0, 6),
              ),
            ],
          ),
          child: SizedBox(
            height: 280,
            width: 250,
            child: Stack(
              alignment: Alignment.center,
              children: [
                _dPadButton(Icons.arrow_upward, "forward", Alignment.topCenter),
                _dPadButton(Icons.arrow_downward, "backward", Alignment.bottomCenter),
                _dPadButton(Icons.arrow_back, "left", Alignment.centerLeft),
                _dPadButton(Icons.arrow_forward, "right", Alignment.centerRight),
                ElevatedButton(
                  onPressed: () => _sendCommand("stop"),
                  style: ElevatedButton.styleFrom(
                    shape: const CircleBorder(),
                    padding: const EdgeInsets.all(24),
                    backgroundColor: Colors.black87,
                    foregroundColor: Colors.white,
                    elevation: 4,
                  ),
                  child: const Icon(Icons.stop, size: 28),
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 20),
        _buildCommandHistory(),
      ],
    );
  }

  Widget _dPadButton(IconData icon, String command, Alignment alignment) {
    return Align(
      alignment: alignment,
      child: ElevatedButton(
        onPressed: () => _sendCommand(command),
        style: ElevatedButton.styleFrom(
          shape: const CircleBorder(),
          padding: const EdgeInsets.all(16),
          backgroundColor: Colors.grey.shade200,
          foregroundColor: Colors.black87,
          elevation: 2,
        ),
        child: Icon(icon, size: 20),
      ),
    );
  }

  Widget _buildCommandHistory() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          '🕘 최근 명령 기록',
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 10),
        Container(
          width: double.infinity,
          padding: const EdgeInsets.all(12),
          margin: const EdgeInsets.symmetric(horizontal: 20),
          decoration: BoxDecoration(
            color: Colors.grey[100],
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: Colors.grey.shade300),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: _commandHistory.isEmpty
                ? [const Text("아직 명령이 없습니다.")]
                : _commandHistory.map((cmd) => Text("• \$cmd")).toList(),
          ),
        ),
      ],
    );
  }
}
