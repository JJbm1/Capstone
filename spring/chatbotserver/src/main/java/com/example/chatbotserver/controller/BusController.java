package com.example.chatbotserver.controller;

import com.example.chatbotserver.dto.BusRequestDto;
import com.example.chatbotserver.service.BusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus")
public class BusController {

    private final BusService busService;

    @PostMapping("/arrival")
    public Map<String, String> getBusArrival(@RequestBody BusRequestDto dto) {
        String result = busService.getBusArrivalInfo(dto);
        return Map.of("data", result); // ✅ Flutter 파싱 가능
    }
}

