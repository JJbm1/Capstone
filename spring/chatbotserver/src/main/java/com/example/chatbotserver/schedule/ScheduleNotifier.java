package com.example.chatbotserver.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduleNotifier {

    private final ScheduleService scheduleService;
    private final FirebasePushService firebasePushService;

    // ✅ 테스트 시에는 cron = "0 * * * * *" 으로 1분마다 실행 가능
    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul") // 매일 6시
    public void notifyTodaySchedule() {
        log.info("🕕 [스케줄러] 오늘 일정 푸시 알림 시작");

        List<ScheduleInfo> schedules = scheduleService.getTodaySchedules();

        if (schedules.isEmpty()) {
            log.info("📭 오늘 일정 없음. 푸시 생략");
            return;
        }

        // ✅ 오늘 일정 목록 요약
        StringBuilder content = new StringBuilder();
        for (ScheduleInfo s : schedules) {
            content.append("- ").append(s.getContent()).append("\n");
        }

        // ✅ 푸시 내용 구성
        String title = "📅 오늘 일정 알림";
        String body = content.toString();

        // ✅ 푸시 전송
        String testToken = "dY0UqGQGSSKZoU4w65d1wg:APA91bFgl9Z14-YrUmLkFo7L1E11JxW1YsDkPWld1MnKjg3RO7nyjTT6r-HvKUeHXzMBnIlr8lH_l9qjGnFSVgJE5_YiLSg7ZAnA7FDR1mmgW0U7yzd7pDE";
        firebasePushService.sendPush(testToken, title, body);
    }
}
