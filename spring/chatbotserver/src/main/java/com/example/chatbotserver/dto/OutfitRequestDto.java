package com.example.chatbotserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class OutfitRequestDto {
    private int temperature; // ✅ Flutter에서 {"temperature": 18} 형태로 전송
}
