package com.example.chatbotserver.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserPasswordController {

    private final UserService userService;

    // ▶️ 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> request) {
        String uname = request.get("uname");              // ✅ 'username' → 'uname'
        String oldPw = request.get("oldPw");              // ✅ 'oldPassword' → 'oldPw'
        String newPw = request.get("newPw");              // ✅ 'newPassword' → 'newPw'

        String result = userService.changePassword(uname, oldPw, newPw);
        return ResponseEntity.ok(result);
    }
}
