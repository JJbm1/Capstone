package com.example.chatbotserver.service;

import com.example.chatbotserver.entity.OutfitInfo;
import com.example.chatbotserver.repository.OutfitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OutfitService {

    private final OutfitRepository outfitRepository;
    private final ChatGptService chatGptService;

    public String recommendClothesByTemp(String city, double temperature) {
        // 메서드명: findByCityAndMinTempLessThanEqualAndMaxTempGreaterThanEqual
        List<OutfitInfo> list = outfitRepository.findByCityAndMinTempLessThanEqualAndMaxTempGreaterThanEqual(
                city, temperature, temperature);

        if (list == null || list.isEmpty()) {
            return "추천 결과가 없습니다.";
        }
        return list.get(0).getRecommendation();
    }

    public String makeFriendlyResponse(String rawRecommendation) {
        if (rawRecommendation == null || rawRecommendation.isEmpty()) {
            return "죄송합니다. 추천할 내용이 없습니다.";
        }

        String prompt = "사용자에게 옷차림을 추천하는 친절하고 자연스러운 문장으로 바꿔줘: \"" + rawRecommendation + "\"";

        String gptResponse = chatGptService.askGpt(prompt);

        return gptResponse;
    }
}
