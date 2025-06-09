package com.example.chatbotserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "weather_info")
@Getter
@Setter
@NoArgsConstructor
public class WeatherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String region;        // 예: 대구, 하양
    private String weather;       // 예: 맑음, 흐림
    private int fineDust;         // 미세먼지 수치
    private boolean needMask;     // 마스크 착용 권장 여부
}
