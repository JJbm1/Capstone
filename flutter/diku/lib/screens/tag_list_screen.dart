import 'package:flutter/material.dart';
import 'tag_schedule_screen.dart';

class TagListScreen extends StatelessWidget {
  final List<String>? scheduleContents;

  const TagListScreen({super.key, this.scheduleContents});

  // 일정 내용에서 태그 추출 (필요 시 사용)
  List<String> extractTags() {
    final tagSet = <String>{};
    final tagRegex = RegExp(r"#\w+");
    if (scheduleContents != null) {
      for (final content in scheduleContents!) {
        tagSet.addAll(tagRegex.allMatches(content).map((e) => e.group(0)!));
      }
    }
    return tagSet.toList();
  }

  @override
  Widget build(BuildContext context) {
    // 일정 내용 기반 태그 추출 or 고정 5개 태그
    final tags = (scheduleContents == null || scheduleContents!.isEmpty)
        ? ['#직장', '#학교', '#공부', '#운동', '#약속']
        : extractTags();

    return Scaffold(
      appBar: AppBar(title: const Text("일정 태그 목록")),
      body: ListView.builder(
        itemCount: tags.length,
        itemBuilder: (context, index) {
          final tag = tags[index];
          return ListTile(
            title: Text(tag),
            leading: const Icon(Icons.label),
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => TagScheduleScreen(tag: tag),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
