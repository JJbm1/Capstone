package com.example.chatbotserver.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String uname;         // ✅ 사용자 식별자 (username → uname)
    private String email;         // 수정할 이메일
    private String name;          // 수정할 닉네임
    private String deviceToken;   // 수정할 디바이스 토큰
}
