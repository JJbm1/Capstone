package com.example.chatbotserver.controller;

import com.example.chatbotserver.service.OutfitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/outfit")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitService outfitService;

    @GetMapping("/recommend")
    public Map<String, String> recommend(
            @RequestParam String city,
            @RequestParam double temperature
    ) {
        String rawRecommendation = outfitService.recommendClothesByTemp(city, temperature);
        String friendlyResponse = outfitService.makeFriendlyResponse(rawRecommendation);

        Map<String, String> response = new HashMap<>();
        response.put("data", friendlyResponse);
        return response;
    }
}
