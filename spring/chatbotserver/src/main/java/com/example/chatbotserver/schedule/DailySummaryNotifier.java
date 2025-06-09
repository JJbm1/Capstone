package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ──────────────────────────────────────────────────────────────────────────────
 * 매일 아침 06:00(KST)에 “오늘(당일) 일정”과 “오늘 실행 대상 루틴”을 사용자별로 묶어서
 * SchedulePushService, RoutinePushService 를 통해 한 번에 알림을 보낸다.
 * ──────────────────────────────────────────────────────────────────────────────
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailySummaryNotifier {

    private final ScheduleRepository scheduleRepository;
    private final RoutineRepository  routineRepository;
    private final UserRepository     userRepository;

    private final SchedulePushService schedulePushService;
    private final RoutinePushService  routinePushService;

    /**
     * ──────────────────────────────────────────────────────────────────────────
     * 매일 06:00:00 (Asia/Seoul) 에 실행됨
     * cron 표현식: 초 분 시 일 월 요일  (Sek: KST)
     * ──────────────────────────────────────────────────────────────────────────
     */
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void sendDailySchedulesAndRoutines() {
        LocalDate today     = LocalDate.now();                       // 오늘 날짜 (한국시간 기준)
        DayOfWeek dayOfWeek = today.getDayOfWeek();                  // 오늘 요일 정보
        String todayShort = dayOfWeek.getDisplayName(
                TextStyle.SHORT, Locale.ENGLISH
        ); // “Mon”, “Tue”, “Wed” 등 영문 축약 형태

        log.info("[DailySummaryNotifier] 실행 시각: {}", LocalDateTime.now());

        // 1) 오늘(날짜) 기준 “일반 일정” 목록 조회
        List<ScheduleInfo> allSchedulesToday = scheduleRepository.findByDate(today);

        // 2) 전체 루틴 중 “오늘 실행 대상”인 루틴만 필터링
        List<Routine> allRoutines = routineRepository.findAll();
        List<Routine> todayRoutines = allRoutines.stream()
                .filter(r -> {
                    // r.getRDays()가 “Mon,Tue,Wed,Thu,…” 형식으로 저장되어 있다고 가정
                    String[] days = r.getRDays().split(",");
                    return Arrays.asList(days).contains(todayShort);
                })
                .collect(Collectors.toList());

        // 3) “일반 일정”을 사용자별로 그룹핑 (UID → List<ScheduleInfo>)
        Map<Long, List<ScheduleInfo>> schedulesByUser = allSchedulesToday.stream()
                .collect(Collectors.groupingBy(ScheduleInfo::getUid));

        // 4) “오늘 루틴”을 사용자별로 그룹핑 (UID → List<Routine>)
        Map<Long, List<Routine>> routinesByUser = todayRoutines.stream()
                .collect(Collectors.groupingBy(Routine::getUid));

        // 5) 두 Map의 키(UID) 합집합을 구해서 사용자별로 알림 전송
        Set<Long> allUids = new HashSet<>();
        allUids.addAll(schedulesByUser.keySet());
        allUids.addAll(routinesByUser.keySet());

        for (Long uid : allUids) {
            Optional<User> optUser = userRepository.findById(uid);
            if (optUser.isEmpty()) {
                log.warn("→ UID={} 사용자 정보가 없습니다. SKIP", uid);
                continue;
            }
            User user = optUser.get();

            // ──── “오늘 일정” 푸시 ────
            if (schedulesByUser.containsKey(uid)) {
                List<ScheduleInfo> userSchedules = schedulesByUser.get(uid);
                log.info("  ▶ {}님(UID={}) 오늘 일정 {}개:", user.getUname(), uid, userSchedules.size());
                userSchedules.forEach(s -> log.info("     • [{}] {} (태그: {})",
                        s.getTime(), s.getContent(), s.getTag()));
                schedulePushService.sendDailyScheduleSummary(user, userSchedules);
            }

            // ──── “오늘 루틴” 푸시 ────
            if (routinesByUser.containsKey(uid)) {
                List<Routine> userRoutines = routinesByUser.get(uid);
                log.info("  ▶ {}님(UID={}) 오늘 루틴 {}개:", user.getUname(), uid, userRoutines.size());
                userRoutines.forEach(r -> log.info("     • [{}] {}", r.getRTime(), r.getRName()));
                routinePushService.sendDailyRoutineSummary(user, userRoutines);
            }
        }

        log.info("[DailySummaryNotifier] 완료");
    }
}
