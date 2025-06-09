import 'package:flutter/material.dart';
import '../services/routine_service.dart';

class RoutineListScreen extends StatefulWidget {
  final String userName;
  const RoutineListScreen({super.key, required this.userName});

  @override
  State<RoutineListScreen> createState() => _RoutineListScreenState();
}

class _RoutineListScreenState extends State<RoutineListScreen> {
  List<Map<String, dynamic>> routineList = [];

  @override
  void initState() {
    super.initState();
    loadAutoRoutines();
  }

  Future<void> loadAutoRoutines() async {
    final data = await RoutineService.fetchAutoRoutines(widget.userName);
    setState(() {
      routineList = data;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("루틴 목록")),
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: routineList.isEmpty
            ? const Center(child: Text("아직 자동 등록된 루틴이 없습니다."))
            : ListView.separated(
          itemCount: routineList.length,
          separatorBuilder: (context, index) => const Divider(),
          itemBuilder: (context, index) {
            final routine = routineList[index];
            return ListTile(
              leading: const Icon(Icons.alarm),
              title: Text(routine['rName'] ?? ''),
              subtitle: Text("시간: ${routine['rTime'] ?? ''}"),
            );
          },
        ),
      ),
    );
  }
}
