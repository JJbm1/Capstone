package com.example.chatbotserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubwayRequestDto {
    private String line;     // 예: "1호선" 또는 "2호선"
    private String station;  // 예: "영대병원", "반월당"
}
