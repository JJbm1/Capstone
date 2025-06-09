package com.example.chatbotserver.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "routine")
public class Routine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 실제 테이블 칼럼명이 "uid" 이므로 명시적으로 매핑
    @Column(name = "uid", nullable = false)
    private Long uid;

    // 테이블 칼럼명이 "r_name" 이므로 @Column(name="r_name") 으로 매핑
    @Column(name = "r_name", nullable = false, length = 100)
    private String rName;

    // 테이블 칼럼명이 "r_time" 이므로 @Column(name="r_time") 으로 매핑
    @Column(name = "r_time", nullable = false)
    private LocalTime rTime;

    // 기존에 매핑하셨던 r_days는 그대로 두시면 됩니다 (테이블에도 "r_days" 칼럼이 있음)
    @Column(name = "r_days", nullable = false)
    private String rDays; // 예: "Mon,Tue,Wed"
}
