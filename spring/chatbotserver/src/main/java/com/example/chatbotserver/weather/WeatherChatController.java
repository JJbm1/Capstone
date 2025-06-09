package com.example.chatbotserver.weather;

import com.example.chatbotserver.dto.WeatherResponseDto;
import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.service.OutfitService;
import com.example.chatbotserver.service.WeatherService;
// ↓ 벡터 검색 import는 주석 처리하거나 지워도 됩니다.
// import com.example.chatbotserver.vector.VectorSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/chat/weather")
@CrossOrigin(origins = "*")
public class WeatherChatController {

    private final WeatherService weatherService;
    private final OutfitService outfitService;
    private final ChatGptService chatGptService;
    // ↓ 벡터검색 서비스는 사용하지 않으므로 주석 처리 또는 제거
    // private final VectorSearchService vectorSearchService;

    public WeatherChatController(
            WeatherService weatherService,
            OutfitService outfitService,
            ChatGptService chatGptService
            // , VectorSearchService vectorSearchService  // 주석 처리
    ) {
        this.weatherService = weatherService;
        this.outfitService = outfitService;
        this.chatGptService = chatGptService;
        // this.vectorSearchService = vectorSearchService;
    }

    @GetMapping
    public Map<String, String> handleWeather(@RequestParam String message) {
        // 1) 메시지에서 “대구” 또는 “하양” 키워드만 추출해서 날씨 조회
        String cityKeyword = extractCity(message);   // 예: "Daegu" 또는 "Hayang"
        WeatherResponseDto weatherDto = weatherService.getWeatherInfo(cityKeyword);
        String weatherSummary = weatherDto.getSummary();
        // 예: "오늘 대구는 맑음이고, 기온은 21.9도입니다. 강수량은 0.0mm, 미세먼지(PM2.5)는 25.0입니다. 마스크: 필요없음. 우산: 필요없음."

        // 2) “옷차림” 요청 키워드 판단
        boolean wantsOutfit = message.contains("옷")
                || message.contains("옷차림")
                || message.contains("입");

        String result;

        if (wantsOutfit) {
            // 3-1) 옷차림 추천 포함
            double temperature = weatherDto.getTemperature();
            String outfitRec = outfitService.recommendClothesByTemp(cityKeyword, temperature);

            // 이제부터는 벡터 검색을 하지 않고, 날씨+옷차림 정보만으로 GPT 호출
            String prompt = String.format(
                    "사용자가 '%s'라고 물었어요.\n\n" +
                            "현재 날씨 정보:\n%s\n\n" +
                            "추천 옷차림:\n%s\n\n" +
                            "이 정보를 바탕으로, 자연스럽고 친절한 말투로 답변해 주세요.",
                    message, weatherSummary, outfitRec
            );
            result = chatGptService.askGpt(prompt);

        } else {
            // 3-2) 순수 날씨만 묻는 경우(벡터 검색 없이)
            String prompt = String.format(
                    "사용자가 '%s'라고 물었어요.\n\n" +
                            "현재 날씨 정보:\n%s\n\n" +
                            "위 정보를 바탕으로, 자연스럽고 친절한 말투로 응답해 주세요.",
                    message, weatherSummary
            );
            result = chatGptService.askGpt(prompt);
        }

        // 4) 최종 응답: Flutter가 Map의 data 필드만 꺼내 씁니다.
        return Map.of("data", result);
    }

    // 메시지에서 “대구” 또는 “하양” 단어가 포함되어 있으면 해당 키워드를 리턴
    // 포함되지 않으면 기본값 “Daegu” 사용
    private String extractCity(String message) {
        if (message.contains("대구")) {
            return "Daegu";
        } else if (message.contains("하양")) {
            return "Hayang";
        }
        return "Daegu";
    }
}
