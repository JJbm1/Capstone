package com.example.chatbotserver.vector;

import lombok.Data;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/vector")
public class VectorSaveController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/save")
    public ResponseEntity<String> saveVector(@RequestBody VectorSaveRequest request) {
        String url = "http://localhost:5005/save";

        // 벡터 DB로 보낼 JSON 데이터 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<VectorSaveRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장 실패: " + e.getMessage());
        }
    }

    @Data
    public static class VectorSaveRequest {
        private String question;
        private String answer;
    }
}
