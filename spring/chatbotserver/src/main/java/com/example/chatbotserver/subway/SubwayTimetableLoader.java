package com.example.chatbotserver.subway;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ▶ resources/subway/subwayTime_1_low.csv 파일을 읽어서
 *    “역 이름 → 도착 시간 리스트” 맵을 만드는 컴포넌트
 *    애플리케이션 시작 시 한 번만 CSV를 로드합니다.
 */
@Component
public class
SubwayTimetableLoader {

    // "역 이름"을 key로, 해당 역의 LocalTime 리스트를 value로 저장
    private final Map<String, List<LocalTime>> stationToTimes = new HashMap<>();

    // CSV에 들어있는 시간 문자열(예: "05:10")을 파싱할 포맷터
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    @PostConstruct
    public void init() {
        try {
            // ▶ resources/subway/subwayTime_1_low.csv 를 로드
            ClassPathResource resource = new ClassPathResource("subway/subwayTime_1_low.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                // 첫 번째 줄(헤더)은 건너뛰기
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                // CSV 한 줄을 ','로 분리
                String[] tokens = line.split(",");
                if (tokens.length < 2) continue;

                String stationName = tokens[0].trim(); // 예: "반월당"
                String timeStr    = tokens[1].trim();  // 예: "05:10"
                LocalTime time = LocalTime.parse(timeStr, timeFormatter);

                // 해당 역 키가 없으면 새 List 생성 후 추가
                stationToTimes
                        .computeIfAbsent(stationName, k -> new ArrayList<>())
                        .add(time);
            }
            reader.close();

            // 각 역별 도착 시간 리스트를 오름차순 정렬
            for (List<LocalTime> times : stationToTimes.values()) {
                Collections.sort(times);
            }

            System.out.println("✅ SubwayTimetableLoader ▶ CSV 로드 완료 (역 개수 = "
                    + stationToTimes.size() + ")");
        } catch (Exception e) {
            System.err.println("❌ SubwayTimetableLoader ▶ CSV 로딩 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 특정 역(stationName)과 현재 시각(now) 이후(같거나 이후)에 도착하는
     * 첫 번째 열차 시간을 Optional로 반환합니다.
     */
    public Optional<LocalTime> getNextArrivalTime(String stationName, LocalTime now) {
        List<LocalTime> times = stationToTimes.getOrDefault(stationName, Collections.emptyList());
        for (LocalTime t : times) {
            if (t.equals(now) || t.isAfter(now)) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    /**
     * (필요 시) 특정 역의 전체 도착 시간 리스트를 반환합니다.
     */
    public List<LocalTime> getAllTimesForStation(String stationName) {
        return stationToTimes.getOrDefault(stationName, Collections.emptyList());
    }
}
