package com.example.chatbotserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor               // 기본 생성자
@AllArgsConstructor              // 모든 필드를 받는 생성자 (String, String)
public class BusRequestDto {
    private String stationName;
    private String busNumber;
}
