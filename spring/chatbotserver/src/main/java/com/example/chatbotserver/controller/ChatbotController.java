package com.example.chatbotserver.controller;

import com.example.chatbotserver.dto.QuestionRequest;
import com.example.chatbotserver.schedule.RoutinePushService;  // 라즈베리 전송 서비스 임포트
import com.example.chatbotserver.service.ChatGptService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ChatbotController {

    private final ChatGptService chatGptService;
    private final RoutinePushService routinePushService;  // 라즈베리파이 전송 서비스

    public ChatbotController(ChatGptService chatGptService, RoutinePushService routinePushService) {
        this.chatGptService = chatGptService;
        this.routinePushService = routinePushService;
    }

    @PostMapping("/chatbot")
    public Map<String, String> chatbot(@RequestBody QuestionRequest request) {
        String question = request.getQuestion();

        // GPT API 호출해서 자연스러운 응답 생성
        String response = chatGptService.askGpt(question);

        // 라즈베리파이로 응답 전송 (user명은 예시, 필요 시 변경)
        routinePushService.sendToRaspberry("robot", response);

        Map<String, String> result = new HashMap<>();
        result.put("response", response);
        return result;
    }
}
