package com.example.chatbotserver.user; // ✅ 이 선언 꼭 있어야 함

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uname", nullable = false, unique = true)
    private String uname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "pw", nullable = false)
    private String pw;

    private String name;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "created", updatable = false)
    private LocalDateTime created;

    @Column(name = "updated")
    private LocalDateTime updated;

    // ✅ 저장되기 전 실행 (생성일 설정)
    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    // ✅ 업데이트 전 실행 (수정일 갱신)
    @PreUpdate
    protected void onUpdate() {
        this.updated = LocalDateTime.now();
    }
}
