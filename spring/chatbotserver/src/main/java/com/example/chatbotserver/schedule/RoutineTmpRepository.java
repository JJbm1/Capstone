package com.example.chatbotserver.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface RoutineTmpRepository extends JpaRepository<RoutineTmp, Long> {

    // qCnt 조회 안 하고, q_date와 q_time만 꺼내어 필터할 때 사용
    @Query("SELECT r FROM RoutineTmp r " +
            "WHERE r.uid      = :uid " +
            "  AND r.content  = :content " +
            "  AND r.qTime    = :qTime " +
            "  AND r.qDate    = :qDate")
    Optional<RoutineTmp> findByUidAndContentAndQTimeAndQDate(
            @Param("uid") Long uid,
            @Param("content") String content,
            @Param("qTime") LocalTime qTime,
            @Param("qDate") LocalDate qDate
    );

    // qCnt 기준으로 3 이상인지 확인할 때 사용
    @Query("SELECT r FROM RoutineTmp r " +
            "WHERE r.uid      = :uid " +
            "  AND r.content  = :content " +
            "  AND r.qTime    = :qTime")
    Optional<RoutineTmp> findByUidAndContentAndQTime(
            @Param("uid") Long uid,
            @Param("content") String content,
            @Param("qTime") LocalTime qTime
    );
}
