package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleScheduler {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final FirebasePushService firebasePushService;
    private final RoutinePushService routinePushService;

    // ✅ 매일 아침 6시에 오늘 일정 알림 전송
    @Scheduled(cron = "0 0 6 * * *")
    public void sendTodaySchedules() {
        log.info("📅 일정 스케줄러 시작");

        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<ScheduleInfo> schedules = scheduleRepository.findByUidAndDate(user.getId(), LocalDate.now());

            if (!schedules.isEmpty()) {
                StringBuilder body = new StringBuilder("📌 오늘 일정:\n");
                for (ScheduleInfo schedule : schedules) {
                    body.append("- ").append(schedule.getTime()).append(" ").append(schedule.getContent()).append("\n");
                }

                String message = body.toString();

                // ✅ Firebase 푸시 (메서드명 수정: sendFcm → sendPush)
                firebasePushService.sendPush(
                        user.getDeviceToken(),
                        "오늘 일정 안내",
                        message
                );

                // ✅ 라즈베리파이 전송 (getUsername → getUname)
                routinePushService.sendToRaspberry(
                        user.getUname(),
                        message
                );
            }
        }

        log.info("✅ 일정 스케줄러 완료");
    }
}
