package com.example.chatbotserver.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 길찾기 요청 DTO
 * 사용자가 Flutter에서 출발지(start), 도착지(end)를 JSON으로 전송
 */
@Getter
@Setter
public class DirectionRequestDto {
    private String start;
    private String end;
}
