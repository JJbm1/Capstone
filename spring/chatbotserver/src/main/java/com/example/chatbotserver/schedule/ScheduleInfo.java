package com.example.chatbotserver.schedule;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "schedule")
public class ScheduleInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * DB의 컬럼명이 'uid' 이므로, name="uid" 로 매핑합니다.
     */
    @Column(name = "uid", nullable = false)
    private Long uid;

    /**
     * DB의 컬럼명이 'title' 이므로, 엔티티 필드명을 content라고 하더라도
     * 반드시 name="title" 로 매핑해야 합니다.
     */
    @Column(name = "title", nullable = false)
    private String content;

    /**
     * DB의 컬럼명이 'tag' 이므로, name="tag" 로 매핑하거나
     * 그냥 생략해도 컬럼명이 같으므로 동작합니다. (여기서는 명시적으로 적었습니다.)
     */
    @Column(name = "tag", nullable = false)
    private String tag;

    /**
     * DB의 컬럼명이 's_date' 이므로, 엔티티 필드 date를 name="s_date"로 매핑합니다.
     */
    @Column(name = "s_date", nullable = false)
    private LocalDate date;

    /**
     * DB의 컬럼명이 's_time' 이므로, 엔티티 필드 time을 name="s_time"로 매핑합니다.
     */
    @Column(name = "s_time", nullable = false)
    private LocalTime time;

    /**
     * DB의 컬럼명이 'alarm' 이므로, 생략해도 되지만 명시적으로 적어주어도 무방합니다.
     * 기본값은 TRUE 로 설정되었습니다.
     */
    @Column(name = "alarm")
    private Boolean alarm = true;

    /**
     * DB의 컬럼명이 'created' 이므로, insertable=false, updatable=false 로 지정하여
     * JPA가 자동으로 생성된 값을 덮어쓰지 않도록 합니다.
     */
    @Column(name = "created", insertable = false, updatable = false)
    private LocalDateTime created;

    /**
     * DB의 컬럼명이 'updated' 이므로, insertable=false, updatable=false 로 지정하여
     * JPA가 자동으로 수정된 값을 덮어쓰지 않도록 합니다.
     */
    @Column(name = "updated", insertable = false, updatable = false)
    private LocalDateTime updated;

    public ScheduleInfo() {
        // JPA용 기본 생성자
    }

    // 필요 시 추가 생성자, toString(), equals(), hashCode() 등 구현
}
