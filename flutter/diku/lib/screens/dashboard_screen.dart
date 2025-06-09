import 'package:flutter/material.dart';
import '../widgets/connection_status_widget.dart';
import '../widgets/robot_remote_control_buttons.dart';
import '../screens/chatbot_screen.dart';
import '../screens/calendar_screen.dart';
import '../screens/routine_list_screen.dart';  // 루틴 목록 화면으로 변경

class DashboardScreen extends StatelessWidget {
  final String userName;

  const DashboardScreen({super.key, required this.userName});

  String getGreetingMessage() {
    final hour = DateTime.now().hour;
    if (hour < 12) {
      return '좋은 아침이에요!\n오늘도 DIRO가 함께할게요 🤖☀️';
    } else if (hour < 18) {
      return '안녕하세요!\nDIRO와 함께하는 멋진 하루 되세요 🌟';
    } else {
      return '좋은 저녁이에요!\n오늘 하루도 DIRO와 편안하게 마무리해요 🌙';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const SizedBox(),
        actions: [
          IconButton(
            icon: const Icon(Icons.arrow_forward_ios),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {},
          ),
          IconButton(
            icon: const Icon(Icons.more_horiz),
            onPressed: () {
              Navigator.pushNamed(context, '/recent');
            },
          ),
        ],
      ),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: SingleChildScrollView(
                padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 20),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      getGreetingMessage(),
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 20),
                    const Center(child: RobotRemoteControlButtons()),
                    const SizedBox(height: 30),
                    // 최근 명령 기록 등 추가 내용이 여기에 들어가면 됩니다.
                  ],
                ),
              ),
            ),

            // 하단 버튼 고정 영역
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  _buildCustomButton(
                    icon: Icons.layers,
                    label: '기능',
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => const CustomFunctionMenuScreen(),
                        ),
                      );
                    },
                  ),
                  _buildCustomButton(
                    icon: Icons.work,
                    label: '업무',
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => CalendarScreen(userName: userName),
                        ),
                      );
                    },
                  ),
                  _buildCustomButton(
                    icon: Icons.alarm,
                    label: '루틴 목록',
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => RoutineListScreen(userName: userName),
                        ),
                      );
                    },
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  static Widget _buildCustomButton({
    required IconData icon,
    required String label,
    required VoidCallback onPressed,
  }) {
    return ElevatedButton.icon(
      onPressed: onPressed,
      icon: Icon(icon, size: 20, color: Colors.white),
      label: Text(
        label,
        style: const TextStyle(fontSize: 14, color: Colors.white),
      ),
      style: ElevatedButton.styleFrom(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        backgroundColor: Colors.blueAccent,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
      ),
    );
  }
}

class CustomFunctionMenuScreen extends StatelessWidget {
  const CustomFunctionMenuScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('기능 선택')),
      body: ListView(
        children: [
          ListTile(
            leading: const Icon(Icons.cloud),
            title: const Text('AI 날씨'),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => const ChatBotScreen(feature: "AI 날씨"),
                ),
              );
            },
          ),
          ListTile(
            leading: const Icon(Icons.directions),
            title: const Text('AI 길찾기'),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (_) => const ChatBotScreen(feature: "AI 길 찾기"),
                ),
              );
            },
          ),
        ],
      ),
    );
  }
}
