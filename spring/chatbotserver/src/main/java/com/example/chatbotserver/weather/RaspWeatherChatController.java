package com.example.chatbotserver.weather;

import com.example.chatbotserver.dto.WeatherResponseDto;
import com.example.chatbotserver.service.ChatGptService;
import com.example.chatbotserver.service.OutfitService;
import com.example.chatbotserver.service.WeatherService;
import com.example.chatbotserver.vector.VectorSearchService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rasp/weather")
public class RaspWeatherChatController {

    private final WeatherService weatherService;
    private final OutfitService outfitService;
    private final ChatGptService chatGptService;
    private final VectorSearchService vectorSearchService;

    public RaspWeatherChatController(WeatherService weatherService,
                                 OutfitService outfitService,
                                 ChatGptService chatGptService,
                                 VectorSearchService vectorSearchService) {
        this.weatherService = weatherService;
        this.outfitService = outfitService;
        this.chatGptService = chatGptService;
        this.vectorSearchService = vectorSearchService;
    }

    @GetMapping
    public Map<String, String> handleWeather(@RequestParam("message") String message) {
        // 1. 날씨 정보 가져오기
        WeatherResponseDto weatherDto = weatherService.getWeatherInfo(message);
        String weather = weatherDto.getSummary();

        // 2. 옷차림 요청 판단
        boolean wantsOutfit = message.contains("옷") || message.contains("입") 
                                || message.contains("옷차림") || message.contains("입으")
                                || message.contains("입을");

        String result;

        if (wantsOutfit) {
            // 지역 추출
            String city = extractCity(message);
            // 온도 추출
            double temperature = weatherDto.getTemperature(); // WeatherResponseDto에 온도 필드가 있다고 가정
            // 3. OutfitService 호출
            String outfitRecommendation = outfitService.recommendClothesByTemp(city, temperature);
            String prompt = message + "\n\n" + weather + "\n\n추천 옷차림: " + outfitRecommendation;

            System.out.println("GPT 프롬프트(옷차림): " + prompt);

            result = chatGptService.askGpt(prompt);
        } else {
            String vectorResult = vectorSearchService.search(message);

            System.out.println("벡터 검색 결과: " + vectorResult);
    
            String prompt = String.format(
            "사용자가 '%s'라고 물었어.\n\n" +
            "날씨 정보는 다음과 같아:\n%s\n\n" +
            "그리고 비슷한 질문에 대한 내용도 참고해:\n%s\n\n" +
            "이 모든 정보를 바탕으로 자연스럽게 대답해줘.",
            message, weather, vectorResult);
            System.out.println("GPT 프롬프트(벡터 포함): " + prompt);

            result = chatGptService.askGpt(prompt);
        }
        return Map.of("data", result); // ✅ 여기서 메서드 종료
    }

    // city 추출 메서드 (필요시 더 정교하게 개선 가능)
    private String extractCity(String message) {
        if (message.contains("대구")) {
            return "Daegu";
        } else if (message.contains("하양")) {
            return "Hayang";
        }
        // 기본값 대구
        return "Daegu";
    }
}
