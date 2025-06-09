package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * ──────────────────────────────────────────────────────────────────────────────
 * 기존에 있던 루틴 푸시 서비스에, “당일 루틴 목록 요약” 메서드를 추가해서
 * DailySummaryNotifier 등에서 호출할 수 있도록 수정한 전체 코드입니다.
 * ──────────────────────────────────────────────────────────────────────────────
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoutinePushService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 1) 일정 알림(루틴 개별 실행 시 호출) 메서드 (기존 구현)
     */
    public void sendRoutine(User user, Routine routine) {
        // 사용자에게 보낼 메시지 본문 생성
        String body = "⏰ " + routine.getRTime() + "에 루틴 [" + routine.getRName() + "]이 있어요!";
        // 1) FCM Push 전송
        sendFcm(user.getDeviceToken(), "루틴 알림", body);
        // 2) 라즈베리파이로 HTTP 요청 → 음성 출력 트리거
        sendToRaspberry(user.getUname(), body);
    }

    /**
     * 2) “오늘 실행할 루틴 요약”을 사용자에게 보내는 메서드 (신규 추가)
     *    예: 매일 아침 8시에 “오늘 루틴 목록” 정리본을 보내고 싶을 때 호출
     */
    public void sendDailyRoutineSummary(User user, List<Routine> routines) {
        StringBuilder sb = new StringBuilder();
        sb.append("안녕하세요 ").append(user.getUname()).append("님,\n");
        sb.append("오늘 실행할 루틴 목록:\n");
        for (Routine r : routines) {
            sb.append(" • [")
                    .append(r.getRTime())
                    .append("] ")
                    .append(r.getRName())
                    .append("\n");
        }
        String summaryBody = sb.toString();
        log.info("[푸시] 루틴 목록 to {}:\n{}", user.getUname(), summaryBody);

        // 실제로 FCM 또는 다른 푸시로 전송하려면 아래 코드를 활성화하세요.
        // 예시: sendFcm(user.getDeviceToken(), "오늘의 루틴 목록", summaryBody);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // ▼ 이하 기존에 있던 FCM / RaspberryPi 전송 메서드 전체를 포함합니다 ▼
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * FCM으로 Push 메시지 전송
     * @param token 디바이스 토큰
     * @param title 알림 제목
     * @param body 알림 내용
     */
    public void sendFcm(String token, String title, String body) {
        try {
            String accessToken = getAccessToken();
            String fcmUrl = "https://fcm.googleapis.com/v1/projects/diku-7a467/messages:send";

            // 메시지 페이로드 구성
            Map<String, Object> notification = Map.of(
                    "title", title,
                    "body", body
            );
            Map<String, Object> message = new HashMap<>();
            message.put("token", token);
            message.put("notification", notification);

            Map<String, Object> request = Map.of("message", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(fcmUrl, entity, String.class);
            log.info("✅ FCM 전송 성공: {}", response.getBody());
        } catch (Exception e) {
            log.error("❌ FCM 전송 실패", e);
        }
    }

    /**
     * 라즈베리파이(Flask 서버)로 HTTP POST 요청을 보내 음성 알림을 트리거
     * @param uname 사용자 이름 (로봇 음성 멘트에 포함 가능)
     * @param message 로봇에게 읽어줄 메시지 본문
     */
    public void sendToRaspberry(String uname, String message) {
        try {
            // 실제 라즈베리파이 IP:PORT 및 엔드포인트로 수정하세요
            String raspberryUrl = "http://192.168.137.117:5000/notify";
            Map<String, String> payload = Map.of(
                    "user", uname,
                    "msg", message
            );

            restTemplate.postForEntity(raspberryUrl, payload, String.class);
            log.info("✅ 라즈베리파이 전송 완료 → 메시지: {}", message);
        } catch (Exception e) {
            log.error("❌ 라즈베리파이 전송 실패", e);
        }
    }

    /**
     * FCM용 OAuth2 액세스 토큰 발급
     * @return Firebase Cloud Messaging용 액세스 토큰 문자열
     * @throws Exception 키 파일 로딩 또는 토큰 갱신 실패 시 예외 발생
     */
    private String getAccessToken() throws Exception {
        // resources 폴더에 있는 Firebase 서비스 계정 JSON 파일 이름이 capstone.json이라 가정
        InputStream is = new ClassPathResource("capstone.json").getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(is)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
