package com.example.chatbotserver.controller.rasp;

import com.example.chatbotserver.dto.BusRequestDto;
import com.example.chatbotserver.service.BusService;
import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.vector.VectorSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/rasp/bus")
@RequiredArgsConstructor
public class RaspBusController {

    private final BusService busService;
    private final ChatGptService chatGptService;
    private final VectorSearchService vectorSearchService;

    @GetMapping
    public Map<String, String> handleBus(@RequestParam("message") String message) {
        // ✅ 메시지에서 정류장 이름과 버스 번호 추출
        BusRequestDto dto = extractBusInfo(message);

        // ✅ 버스 도착 정보 및 벡터 검색 결과 가져오기
        String busInfo = busService.getBusArrivalInfo(dto);
        String vectorResult = vectorSearchService.search(message);

        // ✅ GPT 프롬프트 구성
        String prompt = String.format(
                "사용자가 '%s'라고 물었어.\n\n" +
                "버스 도착 정보는 다음과 같아:\n%s\n\n" +
                "참고할 비슷한 질문은 다음과 같아:\n%s\n\n" +
                "이 내용을 바탕으로 자연스럽고 친절하게 안내해줘.",
                message, busInfo, vectorResult
        );

        // ✅ GPT 응답 반환
        String gptResponse = chatGptService.askGpt(prompt);
        return Map.of("data", gptResponse);
    }

    // ✅ 메시지에서 정류장 이름 + 버스 번호 추출 메서드
    private BusRequestDto extractBusInfo(String message) {
        // 1. 버스 번호 추출 (예: "609번")
        String busNumber = "";
        Matcher matcher = Pattern.compile("(\\d{2,4})번").matcher(message);
        if (matcher.find()) {
            busNumber = matcher.group(1);
        }

        // 2. 정류장 이름 추출
        String station = message
                .replaceAll("정류장에서", "")
                .replaceAll("정류장", "")
                .replaceAll("정류장에", "")
                .replaceAll("에", "")
                .replaceAll("\\d{2,4}번.*", "")
                .replaceAll("버스", "")
                .replaceAll("언제.*", "")
                .replaceAll("[^가-힣0-9 ]", "")
                .trim();

        return new BusRequestDto(station, busNumber);
    }
}
