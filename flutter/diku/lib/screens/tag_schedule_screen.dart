import 'package:flutter/material.dart';
import '../services/schedule_service.dart';  // schedule_service.dart 내 함수 import

class TagScheduleScreen extends StatelessWidget {
  final String tag;
  const TagScheduleScreen({super.key, required this.tag});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('$tag 일정 목록')),
      body: FutureBuilder<List<Map<String, dynamic>>>(
        future: getSchedulesByTag(tag),  // 함수 직접 호출
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(child: Text('해당 태그의 일정이 없습니다.'));
          }
          final schedules = snapshot.data!;
          return ListView.builder(
            itemCount: schedules.length,
            itemBuilder: (context, index) {
              final schedule = schedules[index];
              return ListTile(
                title: Text(schedule['content'] ?? ''),
                subtitle: Text('${schedule['date']} ${schedule['time']}'),
              );
            },
          );
        },
      ),
    );
  }
}
