package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineNotifier {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutinePushService routinePushService;

    /**
     * 매일 아침 06:00:00에 한 번만 실행되도록 크론 수정
     * └─ "0 0 6 * * *" : 초(0), 분(0), 시(6), 매일(*), 매월(*), 매주(*)
     */
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void checkAndNotifyRoutines() {
        // 스케줄러가 실행되는 시점은 06:00:00이므로, now = 06:00:00
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        log.info("[루틴 알림] 현재 시간(강제로 06:00으로 맞춰짐): {}", now);

        // RTime이 정확히 06:00인 루틴만 필터링
        List<Routine> matched = routineRepository.findAll().stream()
                .filter(r -> r.getRTime().withSecond(0).equals(now))
                .toList();

        for (Routine routine : matched) {
            userRepository.findById(routine.getUid()).ifPresent(user -> {
                log.info("✅ 루틴 실행: {} ({})", routine.getRName(), user.getUname());
                routinePushService.sendRoutine(user, routine);
            });
        }
    }
}
