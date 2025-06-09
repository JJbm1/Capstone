import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/schedule_service.dart';

class TodayScheduleScreen extends StatefulWidget {
  final String userName;

  const TodayScheduleScreen({super.key, required this.userName});

  @override
  State<TodayScheduleScreen> createState() => _TodayScheduleScreenState();
}

class _TodayScheduleScreenState extends State<TodayScheduleScreen> {
  List<Map<String, dynamic>> schedules = [];
  DateTime selectedDate = DateTime.now();

  @override
  void initState() {
    super.initState();
    loadScheduleListForDate(selectedDate);
  }

  Future<void> loadScheduleListForDate(DateTime date) async {
    final dateString = DateFormat('yyyy-MM-dd').format(date);
    final data = await fetchScheduleList(widget.userName, dateString);
    setState(() {
      schedules = data;
    });
  }

  void onDateSelected(DateTime date) {
    setState(() {
      selectedDate = date;
    });
    loadScheduleListForDate(date);
  }

  void showEditDialog(int id, String oldContent) {
    final controller = TextEditingController(text: oldContent);

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("일정 수정"),
        content: TextField(controller: controller),
        actions: [
          TextButton(
            child: const Text("취소"),
            onPressed: () => Navigator.pop(context),
          ),
          TextButton(
            child: const Text("수정"),
            onPressed: () async {
              final result = await updateSchedule(scheduleId: id, newContent: controller.text);
              ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(result)));
              Navigator.pop(context);
              loadScheduleListForDate(selectedDate);
            },
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("선택한 날짜 일정")),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextButton(
              child: Text("날짜 선택: ${DateFormat('yyyy-MM-dd').format(selectedDate)}"),
              onPressed: () async {
                final picked = await showDatePicker(
                  context: context,
                  initialDate: selectedDate,
                  firstDate: DateTime(2020),
                  lastDate: DateTime(2100),
                );
                if (picked != null) {
                  onDateSelected(picked);
                }
              },
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: schedules.length,
              itemBuilder: (context, index) {
                final item = schedules[index];
                return ListTile(
                  title: Text(item['content']),
                  subtitle: Text("${item['date']} ${item['time']}"),
                  trailing: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      IconButton(
                        icon: const Icon(Icons.edit),
                        onPressed: () => showEditDialog(item['id'], item['content']),
                      ),
                      IconButton(
                        icon: const Icon(Icons.delete),
                        onPressed: () async {
                          final msg = await deleteSchedule(item['id']);
                          ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg)));
                          loadScheduleListForDate(selectedDate);
                        },
                      ),
                    ],
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
