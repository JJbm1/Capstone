package com.example.chatbotserver.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * Flutter에서 GET /chat/transport?message=... 형태로 요청을 보내면
 * 내부에서 TransportChatService.search(message)를 호출하여,
 * { "data": "...응답..." } 형식으로 반환.
 */
@RestController
@RequestMapping("/chat/transport")
public class TransportChatController {

    private final TransportChatService transportChatService;

    @Autowired
    public TransportChatController(TransportChatService transportChatService) {
        this.transportChatService = transportChatService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> handleTransport(@RequestParam("message") String message) {
        String answer = transportChatService.search(message);
        return ResponseEntity.ok(Collections.singletonMap("data", answer));
    }
}
