package com.example.chatbotserver.user;

import com.example.chatbotserver.dto.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public String signup(User user) {
        if (userRepository.findByUname(user.getUname()).isPresent()) {
            return "이미 존재하는 사용자입니다.";
        }
        userRepository.save(user);
        return "회원가입이 완료되었습니다.";
    }

    public String login(String uname, String pw) {
        return userRepository.findByUname(uname)
                .filter(u -> u.getPw().equals(pw))
                .map(u -> "로그인 성공")
                .orElse("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    public String updateUser(UserUpdateRequest request) {
        return userRepository.findByUname(request.getUname())
                .map(user -> {
                    user.setEmail(request.getEmail());
                    user.setName(request.getName());
                    user.setDeviceToken(request.getDeviceToken());
                    userRepository.save(user);
                    return "회원 정보가 수정되었습니다.";
                })
                .orElse("해당 사용자를 찾을 수 없습니다.");
    }

    public String changePassword(String uname, String oldPw, String newPw) {
        return userRepository.findByUname(uname)
                .map(user -> {
                    if (!user.getPw().equals(oldPw)) {
                        return "기존 비밀번호가 잘못되었습니다.";
                    }
                    user.setPw(newPw);
                    userRepository.save(user);
                    return "비밀번호가 성공적으로 변경되었습니다.";
                })
                .orElse("사용자를 찾을 수 없습니다.");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 회원탈퇴 - 아이디, 비밀번호 모두 확인
    public boolean deleteUser(String uname, String pw) {
        return userRepository.findByUname(uname)
                .filter(user -> user.getPw().equals(pw))
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }
}
