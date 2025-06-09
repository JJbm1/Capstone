package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class SchedulePushService {

    /**
     * “당일 일정 요약”을 사용자에게 보내는 메서드.
     * 실제 푸시 서버(FCM, APNs, 카카오푸시 등)와 연동하거나, 개발 초기에는
     * 단순히 콘솔에 로그만 남겨도 됩니다.
     */
    public void sendDailyScheduleSummary(User user, List<ScheduleInfo> schedules) {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요 ").append(user.getName()).append("님,\n");
        sb.append("오늘(").append(LocalDate.now()).append(") 등록된 일정 목록:\n");
        for (ScheduleInfo s : schedules) {
            sb.append(" • [")
                    .append(s.getTime())
                    .append("] ")
                    .append(s.getContent())
                    .append("  (태그: ").append(s.getTag()).append(")\n");
        }
        log.info("[푸시] 일정 to {}:\n{}", user.getUname(), sb.toString());

        // TODO: 실제 FCM/APNs/KakaoPush 등으로 푸시 메시지 전송 코드 작성
    }
}
