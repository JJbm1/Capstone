package com.example.chatbotserver.schedule;

import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RoutineTmpRepository routineTmpRepository;
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final ChatGptService chatGptService;

    private static final List<String> VALID_TAGS =
            Arrays.asList("#직장", "#학교", "#공부", "#운동", "#약속");

    public void saveSchedule(String username, String content, LocalDate date, LocalTime time) {
        User user = userRepository.findByUname(username)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다: " + username));
        Long uid = user.getId();

        String prompt = String.format(
                "일정 내용: '%s'\n" +
                        "아래 다섯 가지 태그 중에서 하나(#직장, #학교, #공부, #운동, #약속)만 정확히 하나를 골라," +
                        "다른 말 없이 태그만 한 줄로 출력해주세요.",
                content
        );
        String rawTag = chatGptService.askGpt(prompt);
        String trimmed = rawTag.trim();

        String finalTag = VALID_TAGS.contains(trimmed) ? trimmed : "#직장";

        ScheduleInfo schedule = new ScheduleInfo();
        schedule.setUid(uid);
        schedule.setContent(content);
        schedule.setDate(date);
        schedule.setTime(time);
        schedule.setTag(finalTag);
        scheduleRepository.save(schedule);

        updateRoutineCandidate(uid, content, time);
        checkAndRegisterRoutine(uid, content, time);
    }

    public List<ScheduleInfo> getTodaySchedules() {
        return scheduleRepository.findByDate(LocalDate.now());
    }

    // uid + 날짜 기준 일정 조회 메서드
    public List<ScheduleInfo> getSchedulesByUserAndDate(Long uid, LocalDate date) {
        return scheduleRepository.findByUidAndDate(uid, date);
    }

    // 태그별 일정 조회 메서드 (컨트롤러에서 호출할 때 필요하면 추가)
    public List<ScheduleInfo> getSchedulesByTag(String tag) {
        return scheduleRepository.findByTag(tag);
    }

    public void updateRoutineCandidate(Long uid, String content, LocalTime qTime) {
        LocalDate today = LocalDate.now();

        routineTmpRepository.findByUidAndContentAndQTimeAndQDate(uid, content, qTime, today)
                .ifPresentOrElse(tmp -> {
                    tmp.setQCnt(tmp.getQCnt() + 1);
                    routineTmpRepository.save(tmp);
                }, () -> {
                    RoutineTmp tmp = new RoutineTmp();
                    tmp.setUid(uid);
                    tmp.setContent(content);
                    tmp.setQTime(qTime);
                    tmp.setQDate(today);
                    tmp.setQCnt(1);
                    tmp.setAutoRoutine(false);
                    routineTmpRepository.save(tmp);
                });
    }

    public void checkAndRegisterRoutine(Long uid, String content, LocalTime qTime) {
        routineTmpRepository.findByUidAndContentAndQTime(uid, content, qTime)
                .filter(tmp -> tmp.getQCnt() >= 3 && !tmp.isAutoRoutine())
                .ifPresent(tmp -> {
                    Routine routine = new Routine();
                    routine.setUid(uid);
                    routine.setRName(content);
                    routine.setRTime(qTime);
                    routine.setRDays("Mon,Tue,Wed,Thu,Fri,Sat,Sun");
                    routineRepository.save(routine);

                    tmp.setAutoRoutine(true);
                    routineTmpRepository.save(tmp);
                });
    }
}
