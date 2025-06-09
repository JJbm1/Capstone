package com.example.chatbotserver.repository;

import com.example.chatbotserver.entity.OutfitInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutfitRepository extends JpaRepository<OutfitInfo, Long> {
    List<OutfitInfo> findByCityAndMinTempLessThanEqualAndMaxTempGreaterThanEqual(String city, double minTemp, double maxTemp);
}
