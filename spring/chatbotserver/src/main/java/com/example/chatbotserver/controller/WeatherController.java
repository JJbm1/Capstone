package com.example.chatbotserver.controller;

import com.example.chatbotserver.dto.WeatherResponseDto;
import com.example.chatbotserver.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public WeatherResponseDto getWeather(@RequestParam String region) {
        return weatherService.getWeatherInfo(region);
    }
}
