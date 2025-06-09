package com.example.chatbotserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;

    // 🔍 마이페이지 - 사용자 정보 조회
    @GetMapping("/{uname}")  // ✅ 'username' → 'uname'
    public ResponseEntity<User> getUserInfo(@PathVariable String uname) {
        User user = userRepository.findByUname(uname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return ResponseEntity.ok(user);
    }
}
