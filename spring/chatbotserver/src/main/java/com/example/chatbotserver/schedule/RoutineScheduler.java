package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineScheduler {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;
    private final RoutinePushService routinePushService;

    /**
     * 매 분 0초에 실행하도록 변경
     * (cron 표현식: 초 분 시 일 월 요일 → 0 * * * * * 은 매 분 0초에 동작)
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendRoutines() {
        // 현재 시각(시:분)만 추출 (초, 나노초는 0으로 설정)
        LocalTime now = LocalTime.now()
                .withSecond(0)
                .withNano(0);

        // (선택) 요일 정보가 필요하다면 아래처럼 todayShort를 이용할 수 있음
        // String todayShort = LocalDate.now()
        //         .getDayOfWeek()
        //         .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        log.info("🟡 루틴 스케줄러 실행 → 현재 시각 = {}", now);

        // 모든 사용자 조회
        List<User> users = userRepository.findAll();
        for (User user : users) {
            // 해당 사용자가 등록한 루틴 목록 조회
            List<Routine> routines = routineRepository.findByUid(user.getId());

            for (Routine routine : routines) {
                // 1) 시간 비교: rTime(시:분)과 now가 같으면 알림 대상
                boolean timeMatch = routine.getRTime().equals(now);

                // 2) (선택) 요일 비교: 만약 r.getRDays()에 “Mon” 등으로 요일 저장 중이라면
                //    String todayShort = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                //    boolean dayMatch = routine.getRDays().contains(todayShort);
                //    if (timeMatch && dayMatch) { … }

                if (timeMatch) {
                    // 실제 알림 전송
                    routinePushService.sendRoutine(user, routine);
                    log.info("✅ [{}]님, {} 시각 루틴 알림 전송 → 루틴명: {}",
                            user.getUname(), now, routine.getRName());
                }
            }
        }

        log.info("✅ 루틴 스케줄러 완료");
    }
}
