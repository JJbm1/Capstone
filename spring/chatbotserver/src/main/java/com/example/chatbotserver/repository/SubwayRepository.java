// SubwayRepository.java
package com.example.chatbotserver.repository;

import com.example.chatbotserver.entity.SubwayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubwayRepository extends JpaRepository<SubwayInfo, Long> {
    Optional<SubwayInfo> findByLineAndStation(String line, String station);
}
