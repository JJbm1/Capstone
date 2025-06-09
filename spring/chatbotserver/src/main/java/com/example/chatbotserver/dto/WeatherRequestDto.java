package com.example.chatbotserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class WeatherRequestDto {
    private String city; // ✅ Flutter에서 {"city": "Daegu"} 형태로 전송
}
