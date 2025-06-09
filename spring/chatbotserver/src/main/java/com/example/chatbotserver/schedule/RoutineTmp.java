package com.example.chatbotserver.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "routine_tmp")
public class RoutineTmp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DB에 uid 라는 컬럼이 있으므로 그대로 매핑
    @Column(name = "uid", nullable = false)
    private Long uid;

    // DB에 content 라는 컬럼이 있으므로 그대로 매핑
    @Column(name = "content", nullable = false)
    private String content;

    // → DB에는 컬럼명이 'qTime' 이므로 @Column(name="qTime") 으로 맞춰줍니다.
    @Column(name = "qTime", nullable = false)
    private LocalTime qTime;

    // → DB에는 컬럼명이 'qDate' 이므로 똑같이 매핑
    @Column(name = "qDate", nullable = false)
    private LocalDate qDate;

    // → DB에는 컬럼명이 'qCnt' 이므로 그대로 매핑
    @Column(name = "qCnt", nullable = false)
    private int qCnt;

    // auto_routine 은 DB에도 snake_case 그대로 남아 있으므로 변경 불필요
    @Column(name = "auto_routine", nullable = false)
    private boolean autoRoutine;
}
