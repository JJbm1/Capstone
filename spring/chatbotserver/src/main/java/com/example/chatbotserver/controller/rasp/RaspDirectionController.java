package com.example.chatbotserver.controller.rasp;

import com.example.chatbotserver.service.DirectionService;
import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.vector.VectorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rasp/direction")
@RequiredArgsConstructor
public class RaspDirectionController {

    private final DirectionService directionService;
    private final ChatGptService chatGptService;
    private final VectorSearchService vectorSearchService;

    @GetMapping
    public Map<String, String> getDirection(@RequestParam("message") String message) {
        try {
            // 기존 서비스에서 길찾기 정보 얻기
            String directionInfo = directionService.getDirectionInfo(message);

            // 벡터DB에서 유사 질문 참고 문장 검색
            String vectorResult = vectorSearchService.search(message);

            // GPT 프롬프트 구성
            String prompt = String.format(
                    "사용자가 '%s'라고 물었어.\n" +
                    "길찾기 정보는 다음과 같아:\n%s\n\n" +
                    "참고할 비슷한 질문들은 다음과 같아:\n%s\n\n" +
                    "이 모든 정보를 바탕으로 자연스럽고 친절하게 안내해줘.",
                    message, directionInfo, vectorResult
            );

            String response = chatGptService.askGpt(prompt);
            return Map.of("data", response);

        } catch (Exception e) {
            return Map.of("data", "길찾기 정보를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
