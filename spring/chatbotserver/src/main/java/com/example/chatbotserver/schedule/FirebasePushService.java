package com.example.chatbotserver.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushService {

    // 🔁 너의 Firebase 프로젝트 ID로 바꿔줘
    private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/diku/messages:send";

    private final RestTemplate restTemplate = new RestTemplate();

    // 푸시 전송
    public void sendPush(String targetToken, String title, String body) {
        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // FCM 메시지 구조
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            Map<String, Object> message = new HashMap<>();
            message.put("token", targetToken);
            message.put("notification", notification);

            Map<String, Object> request = new HashMap<>();
            request.put("message", message);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(FCM_ENDPOINT, entity, String.class);

            log.info("[FCM] 푸시 전송 결과: {}", response.getBody());

        } catch (Exception e) {
            log.error("[FCM] 푸시 전송 실패", e);
        }
    }

    // 서비스 계정으로 액세스 토큰 발급
    private String getAccessToken() throws Exception {
        InputStream is = new ClassPathResource("diku.json").getInputStream(); // 🔁 너의 파일 이름
        GoogleCredentials credentials = GoogleCredentials.fromStream(is)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}
