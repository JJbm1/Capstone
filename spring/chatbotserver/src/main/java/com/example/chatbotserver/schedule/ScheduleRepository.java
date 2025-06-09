package com.example.chatbotserver.schedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<ScheduleInfo, Long> {
    List<ScheduleInfo> findByUidAndDate(Long uid, LocalDate date);
    List<ScheduleInfo> findByDate(LocalDate date);

    // 태그별 일정 조회 메서드 추가
    List<ScheduleInfo> findByTag(String tag);
}
