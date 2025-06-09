package com.example.chatbotserver.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        String result = userService.signup(user);
        if (result.equals("회원가입이 완료되었습니다.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> req) {
        String uname = req.get("uname");
        String pw = req.get("pw");
        String result = userService.login(uname, pw);
        if (result.equals("로그인 성공")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 회원 정보 수정
    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody com.example.chatbotserver.dto.UserUpdateRequest req) {
        String result = userService.updateUser(req);
        if (result.equals("회원 정보가 수정되었습니다.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 비밀번호 변경
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> req) {
        String uname = req.get("uname");
        String oldPw = req.get("oldPw");
        String newPw = req.get("newPw");
        String result = userService.changePassword(uname, oldPw, newPw);
        if (result.equals("비밀번호가 성공적으로 변경되었습니다.")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    // 회원 탈퇴 (아이디+비밀번호 필요)
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody Map<String, String> req) {
        String uname = req.get("uname");
        String pw = req.get("pw");
        boolean deleted = userService.deleteUser(uname, pw);
        if (deleted) {
            return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("탈퇴 실패(정보 불일치 등)");
        }
    }

    // 전체 유저 리스트 조회 (필요하다면)
    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
