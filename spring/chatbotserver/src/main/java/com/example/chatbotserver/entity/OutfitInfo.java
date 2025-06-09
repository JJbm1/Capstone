package com.example.chatbotserver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "outfit")
@Getter
@Setter
public class OutfitInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String nation;

    @Column
    private Double temp;

    @Column(name = "t_min", nullable = false)
    private Double minTemp;

    @Column(name = "t_max", nullable = false)
    private Double maxTemp;

    @Column
    private Integer hum;

    @Column(name = "w_desc")
    private String weatherDesc;

    @Column
    private Double wind;

    @Column
    private Double rain;

    @Column
    private Integer pm;

    @Column
    private String mask;

    @Column
    private Boolean umbrella;

    @Column(name = "outfit_txt")
    private String recommendation;

    @Column
    private Timestamp time;
}
