import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../services/schedule_service.dart';

class AddScheduleModal extends StatefulWidget {
  final DateTime selectedDate;
  final String userName;

  const AddScheduleModal({
    super.key,
    required this.selectedDate,
    required this.userName,
  });

  @override
  State<AddScheduleModal> createState() => _AddScheduleModalState();
}

class _AddScheduleModalState extends State<AddScheduleModal> {
  final TextEditingController _memoController = TextEditingController();
  TimeOfDay _selectedTime = TimeOfDay.now();

  Future<void> _selectTime() async {
    final picked = await showTimePicker(
      context: context,
      initialTime: _selectedTime,
    );

    if (picked != null) {
      setState(() {
        _selectedTime = picked;
      });
    }
  }

  void _register() async {
    final date = DateFormat('yyyy-MM-dd').format(widget.selectedDate);
    final time =
        "${_selectedTime.hour.toString().padLeft(2, '0')}:${_selectedTime.minute.toString().padLeft(2, '0')}:00";
    final content = _memoController.text;

    if (content.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("일정 내용을 입력해주세요.")),
      );
      return;
    }

    final result = await registerSchedule(
      username: widget.userName,
      date: date,
      time: time,
      content: content,
    );

    if (!mounted) return;
    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(result)),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding:
      EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Wrap(
          children: [
            Text(
              "📝 일정 등록 (${DateFormat('yyyy-MM-dd').format(widget.selectedDate)})",
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            TextFormField(
              controller: _memoController,
              decoration: const InputDecoration(
                labelText: "일정 내용",
                border: OutlineInputBorder(),
              ),
              maxLines: 2,
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                const Icon(Icons.access_time),
                TextButton(
                  onPressed: _selectTime,
                  child: Text("시간 선택: ${_selectedTime.format(context)}"),
                ),
              ],
            ),
            const SizedBox(height: 16),
            ElevatedButton.icon(
              onPressed: _register,
              icon: const Icon(Icons.save),
              label: const Text("등록하기"),
              style:
              ElevatedButton.styleFrom(minimumSize: const Size.fromHeight(45)),
            ),
          ],
        ),
      ),
    );
  }
}
