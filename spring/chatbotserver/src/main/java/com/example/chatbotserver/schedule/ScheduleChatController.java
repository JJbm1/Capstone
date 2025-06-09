package com.example.chatbotserver.schedule;

import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/chat/schedule", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8")
public class ScheduleChatController {

    private final ChatGptService chatGptService;
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    // 일정 등록
    @PostMapping
    public Map<String, String> registerSchedule(
            @RequestParam("username") String username,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time,
            @RequestBody String content
    ) {
        scheduleService.saveSchedule(username, content, date, time);
        String prompt = String.format(
                "사용자가 %s일 %s시에 아래 일정을 등록했어:\n\n%s\n\n등록 완료 메시지를 자연스럽고 친절하게 안내해줘.",
                date, time, content
        );
        String result = chatGptService.askGpt(prompt);
        return Map.of("data", result);
    }

    // 오늘 일정 확인
    @GetMapping("/today")
    public Map<String, String> getTodaySchedule() {
        List<ScheduleInfo> todaySchedules = scheduleService.getTodaySchedules();

        String result;
        if (todaySchedules.isEmpty()) {
            result = chatGptService.askGpt(
                    "사용자가 '오늘 뭐 있어?'라고 물었는데, 오늘 일정이 아무것도 없어. 자연스럽게 알려줘."
            );
        } else {
            StringBuilder sb = new StringBuilder();
            for (ScheduleInfo schedule : todaySchedules) {
                sb.append("- ").append(schedule.getContent()).append("\n");
            }
            String prompt = "사용자가 '오늘 뭐 있어?'라고 물었어.\n아래는 오늘 일정이야:\n\n" + sb +
                    "\n이걸 자연스럽고 따뜻하게 요약해서 알려줘.";
            result = chatGptService.askGpt(prompt);
        }

        return Map.of("data", result);
    }

    // 사용자 + 날짜별 일정 조회 API
    @GetMapping("/list")
    public List<ScheduleInfo> getScheduleList(
            @RequestParam("username") String username,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        User user = userRepository.findByUname(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + username));
        Long uid = user.getId();
        return scheduleService.getSchedulesByUserAndDate(uid, date);
    }

    // 태그별 일정 조회 API 추가
    @GetMapping("/by-tag")
    public List<ScheduleInfo> getSchedulesByTag(
            @RequestParam("tag") String tag
    ) {
        return scheduleService.getSchedulesByTag(tag);
    }
}
