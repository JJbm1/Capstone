package com.example.chatbotserver.schedule;

import com.example.chatbotserver.user.User;
import com.example.chatbotserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/routine")
@RequiredArgsConstructor
public class RoutineController {

    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public List<Routine> getUserRoutines(@PathVariable String username) {
        User user = userRepository.findByUname(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return routineRepository.findByUid(user.getId());
    }
}  