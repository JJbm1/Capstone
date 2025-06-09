package com.example.chatbotserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherResponseDto {
    private String weather;       // 예: 맑음
    private double temperature;   // 예: 24.0
    private double rain;          // 예: 0.0
    private double pm25;          // 예: 25.0
    private String mask;          // 예: 마스크가 필요없습니다
    private String umbrella;      // 예: 우산을 꼭 챙기세요

    // ✅ GPT에 넘길 요약 문구 생성
    public String getSummary() {
        return String.format(
                "오늘 날씨는 '%s'이고, 기온은 %.1f도입니다.\n" +
                        "강수량은 %.1fmm이고, 미세먼지(PM2.5)는 %.1f입니다.\n" +
                        "마스크: %s / 우산: %s",
                weather, temperature, rain, pm25, mask, umbrella
        );
    }
}
