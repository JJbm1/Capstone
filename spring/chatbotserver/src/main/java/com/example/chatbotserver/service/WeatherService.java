package com.example.chatbotserver.service;

import com.example.chatbotserver.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class WeatherService {

    @Value("${openweather.api.key}")
    private String apiKey;

    public WeatherResponseDto getWeatherInfo(String region) {
        try {
            String city = region.equals("대구") ? "Daegu" : "Hayang";

            String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city +
                    "&appid=" + apiKey + "&units=metric&lang=kr";

            RestTemplate restTemplate = new RestTemplate();
            JSONObject json = new JSONObject(restTemplate.getForObject(url, String.class));

            String weather = json.getJSONArray("weather").getJSONObject(0).getString("description");
            double temperature = json.getJSONObject("main").getDouble("temp");
            double rain = json.has("rain") ? json.getJSONObject("rain").optDouble("1h", 0.0) : 0.0;

            // pm2.5는 임시값 또는 DB에서 추후 연결
            double pm25 = 25.0;

            String mask = pm25 > 35 ? "마스크를 착용하세요." : "마스크가 필요없습니다.";
            String umbrella = rain > 0 ? "우산을 꼭 챙기세요." : "우산은 필요 없어요.";

            return new WeatherResponseDto(weather, temperature, rain, pm25, mask, umbrella);

        } catch (Exception e) {
            return new WeatherResponseDto("정보 없음", 0, 0, 0, "오류 발생", "오류 발생");
        }
    }
}
