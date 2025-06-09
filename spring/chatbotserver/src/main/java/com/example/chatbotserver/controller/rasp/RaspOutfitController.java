package com.example.chatbotserver.controller.rasp;

import com.example.chatbotserver.service.OutfitService;
import com.example.chatbotserver.service.WeatherService;
import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.vector.VectorSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rasp/outfit")
public class RaspOutfitController {

    private final WeatherService weatherService;
    private final OutfitService outfitService;
    private final ChatGptService chatGptService;
    private final VectorSearchService vectorSearchService;

    public RaspOutfitController(WeatherService weatherService,
                                OutfitService outfitService,
                                ChatGptService chatGptService,
                                VectorSearchService vectorSearchService) {
        this.weatherService = weatherService;
        this.outfitService = outfitService;
        this.chatGptService = chatGptService;
        this.vectorSearchService = vectorSearchService;
    }

    @GetMapping
    public Map<String, String> getOutfitRecommendation(@RequestParam("message") String message) {
        // 1. 지역 추출
        String city = extractCity(message);
        System.out.println("✅ 지역 추출: " + city);

        // 2. 실시간 날씨 API → 현재 기온만 사용
        double temperature = weatherService.getWeatherInfo(city).getTemperature();
        System.out.println("🌡️ 현재 기온: " + temperature);

        // 3. DB에서 옷차림 추천
        String rawRecommendation = outfitService.recommendClothesByTemp(city, temperature);
        System.out.println("🧥 추천 옷차림 (DB): " + rawRecommendation);

        // 4. GPT로 자연스럽게 바꾸기
        String friendlyRecommendation = outfitService.makeFriendlyResponse(rawRecommendation);
        System.out.println("💬 GPT 문장 변환 결과: " + friendlyRecommendation);

        // 5. 벡터 검색 결과 추가
        String vectorResult = vectorSearchService.search(message);
        System.out.println("🔍 벡터DB 검색 결과: " + vectorResult);

        // 6. GPT 프롬프트 (날씨 문구 제거됨!)
        String prompt = String.format(
            "사용자가 '%s'라고 물었어.\n\n" +
            "다음 옷차림 문장을 반드시 포함해서 자연스럽게 문장을 만들어줘:\n" +
            "\"%s\"\n\n" +  // ✅ 원문 추천 문구
            "또한 아래 문장을 참고해서 보완해도 좋아:\n" +
            "%s\n\n" +
            "마지막으로 비슷한 질문도 참고해:\n%s\n\n" +
            "위 정보들을 반영해 친근하고 부드러운 톤으로 말해줘.",
            message, rawRecommendation, friendlyRecommendation, vectorResult
        );


        System.out.println("📤 최종 GPT 프롬프트:\n" + prompt);

        // 7. GPT 호출
        String gptResponse = chatGptService.askGpt(prompt);

        return Map.of("data", gptResponse);
    }

    private String extractCity(String message) {
        if (message.contains("대구")) return "Daegu";
        if (message.contains("하양")) return "Hayang";
        return "Daegu";
    }
}
