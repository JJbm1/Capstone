package com.example.chatbotserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subway")
@Getter
@Setter
public class SubwayInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String line;        // 1호선/2호선 등
    private String station;     // 역 이름

    @Column(name = "arrival_info")
    private String arrivalInfo; // 예: "2분 후 도착"
}
