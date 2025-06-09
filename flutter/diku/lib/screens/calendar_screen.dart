import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:table_calendar/table_calendar.dart';
import '../widgets/add_schedule_modal.dart';
import '../screens/tag_list_screen.dart';  // 태그 화면 import
import '../services/schedule_service.dart';

class CalendarScreen extends StatefulWidget {
  final String userName;

  const CalendarScreen({super.key, required this.userName});

  @override
  State<CalendarScreen> createState() => _CalendarScreenState();
}

class _CalendarScreenState extends State<CalendarScreen> {
  DateTime _focusedDay = DateTime.now();
  DateTime? _selectedDay;
  List<Map<String, dynamic>> _dailySchedules = [];

  @override
  void initState() {
    super.initState();
    _selectedDay = _focusedDay;
    _fetchSchedulesForSelectedDay();
  }

  Future<void> _fetchSchedulesForSelectedDay() async {
    if (_selectedDay == null) return;
    final formattedDate = DateFormat('yyyy-MM-dd').format(_selectedDay!);
    final list = await fetchScheduleList(widget.userName, formattedDate);
    setState(() {
      _dailySchedules = list;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("📆 ${widget.userName}님의 업무 일정"),
        centerTitle: true,
        actions: [
          IconButton(
            icon: const Icon(Icons.label),
            tooltip: "태그 보기",
            onPressed: () async {
              if (_selectedDay == null) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text("먼저 날짜를 선택해주세요.")),
                );
                return;
              }

              final formattedDate = DateFormat('yyyy-MM-dd').format(_selectedDay!);
              final list = await fetchScheduleList(widget.userName, formattedDate);
              final contents = list.map((e) => e['content'] as String).toList();

              if (!mounted) return;

              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => TagListScreen(scheduleContents: contents),
                ),
              );
            },
          )
        ],
      ),
      body: Column(
        children: [
          SizedBox(
            height: 350,
            child: TableCalendar(
              focusedDay: _focusedDay,
              firstDay: DateTime.utc(2020, 1, 1),
              lastDay: DateTime.utc(2030, 12, 31),
              selectedDayPredicate: (day) => isSameDay(_selectedDay, day),
              onDaySelected: (selectedDay, focusedDay) {
                setState(() {
                  _selectedDay = selectedDay;
                  _focusedDay = focusedDay;
                });
                _fetchSchedulesForSelectedDay();
              },
            ),
          ),
          const SizedBox(height: 20),
          Text(
            _selectedDay != null
                ? DateFormat('yyyy-MM-dd').format(_selectedDay!)
                : "날짜를 선택해주세요",
            style: const TextStyle(fontSize: 16),
          ),
          const SizedBox(height: 20),
          Expanded(
            child: _dailySchedules.isEmpty
                ? const Center(child: Text('해당 날짜에 일정이 없습니다.'))
                : ListView.builder(
              itemCount: _dailySchedules.length,
              itemBuilder: (context, index) {
                final schedule = _dailySchedules[index];
                return ListTile(
                  title: Text(schedule['content']),
                  subtitle: Text("${schedule['time']} / ${schedule['tag']}"),
                );
              },
            ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          if (_selectedDay == null) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text("먼저 날짜를 선택해주세요.")),
            );
            return;
          }

          showModalBottomSheet(
            context: context,
            isScrollControlled: true,
            builder: (context) => AddScheduleModal(
              selectedDate: _selectedDay!,
              userName: widget.userName,
            ),
          ).whenComplete(() {
            _fetchSchedulesForSelectedDay();
          });
        },
        child: const Icon(Icons.add),
      ),
    );
  }
}
