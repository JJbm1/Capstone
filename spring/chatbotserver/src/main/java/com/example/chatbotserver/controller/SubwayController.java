package com.example.chatbotserver.controller;

import com.example.chatbotserver.dto.SubwayRequestDto;
import com.example.chatbotserver.service.SubwayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subway")
@RequiredArgsConstructor
public class SubwayController {

    private final SubwayService subwayService;

    /**
     * ▶ POST /api/subway/info
     * 요청 예시 (JSON):
     * {
     *   "line": "1",
     *   "station": "반월당"
     * }
     *
     * 반환 예시 (JSON):
     * {
     *   "data": "지금 15시 05분 기준, 반월당역 1호선 다음 열차는 15:10에 도착합니다."
     * }
     */
    @PostMapping("/info")
    public ResponseEntity<Map<String, String>> getSubwayInfo(
            @RequestBody SubwayRequestDto requestDto
    ) {
        String line    = requestDto.getLine();
        String station = requestDto.getStation();

        // CSV 기반으로 실제 도착 정보 조회
        String result = subwayService.getSubwayResponse(line, station);

        // Flutter 등 외부 클라이언트는 "data" 필드만 파싱한다고 가정
        return ResponseEntity.ok(Map.of("data", result));
    }
}
