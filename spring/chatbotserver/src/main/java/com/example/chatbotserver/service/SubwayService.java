package com.example.chatbotserver.service;

import com.example.chatbotserver.subway.SubwayTimetableLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubwayService {

    // CSV 로더 빈을 주입받습니다.
    private final SubwayTimetableLoader timetableLoader;

    // 안내 메시지에 사용할 포맷터 (예: "15:05")
    private final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("H:mm");

    /**
     * CSV 기반으로 “다음 도착 시간”을 계산하여 문자열로 반환합니다.
     *
     * @param line    지하철 호선 (예: "1호선" 또는 "2호선")
     * @param station 역 이름 (예: "반월당")
     * @return 자연어 안내 메시지
     */
    public String getSubwayResponse(String line, String station) {
        // 1) 현재 시각(시:분)만 취득 (초/나노초 제외)
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);

        // 2) 현재는 CSV에 1호선 정보만 있으므로, “1” 또는 “1호선”이 아닌 경우 안내 메시지 반환
        if (!"1".equals(line) && !"1호선".equals(line)) {
            return "죄송합니다. 현재는 1호선 정보만 제공합니다.";
        }

        // 3) CSV 로더에서 다음 열차 시간 조회
        Optional<LocalTime> nextOpt = timetableLoader.getNextArrivalTime(station, now);

        if (nextOpt.isEmpty()) {
            // 해당 역에 오늘 남은 열차가 없을 때
            return String.format(
                    "지금 %d시 %d분 기준으로, %s역 1호선 다음 열차가 없습니다.",
                    now.getHour(), now.getMinute(), station
            );
        }

        // 4) 다음 열차 시간이 있으면 자연어 안내 메시지 반환
        LocalTime nextTime = nextOpt.get();
        String nextTimeStr = nextTime.format(outputFormatter);
        return String.format(
                "지금 %d시 %d분 기준, %s역 1호선 다음 열차는 %s에 도착합니다.",
                now.getHour(), now.getMinute(), station, nextTimeStr
        );
    }
}
