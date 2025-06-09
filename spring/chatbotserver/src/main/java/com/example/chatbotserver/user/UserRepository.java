package com.example.chatbotserver.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUname(String uname); // ✅ 수정 완료
}

