package com.example.chatbotserver.controller;

import com.example.chatbotserver.service.DirectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/direction")
public class DirectionController {

    private final DirectionService directionService;

    /**
     * 예: POST /api/direction/find
     * Body: { "start": "하양역", "end": "대구역" }
     */
    @PostMapping("/find")
    public Map<String, String> findDirection(@RequestBody Map<String, String> request) {
        String start = request.get("start");
        String end = request.get("end");

        String response = directionService.getDirectionInfo(start, end);
        return Collections.singletonMap("data", response);
    }
}
