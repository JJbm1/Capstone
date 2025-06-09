package com.example.chatbotserver.controller;

import com.example.chatbotserver.dto.BusRequestDto;
import com.example.chatbotserver.dto.WeatherResponseDto;
import com.example.chatbotserver.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gpt")
public class GptController {

    private final ChatGptService chatGptService;
    private final WeatherService weatherService;
    private final OutfitService outfitService;
    private final BusService busService;
    private final SubwayService subwayService;
    private final DirectionService directionService;

    @GetMapping("/ask")
    public String askToGptWithParam(@RequestParam String message) {
        // ✅ 길찾기: "하양에서 반월당까지 가는 길"
        if (message.contains("에서") && message.contains("까지")) {
            try {
                int startIdx = message.indexOf("에서");
                int endIdx = message.indexOf("까지");

                String start = message.substring(0, startIdx).trim();
                String end = message.substring(startIdx + 2, endIdx).trim();

                return directionService.getDirectionInfo(start, end);
            } catch (Exception e) {
                return "출발지와 목적지를 정확히 입력해 주세요.";
            }
        }

        // ✅ 옷차림 질문
        if (message.contains("입을 옷") || message.contains("옷차림") || message.contains("뭐 입지")) {
            WeatherResponseDto dto = weatherService.getWeatherInfo("대구"); // 온도 기준
            int temp = (int) dto.getTemperature();
            // 예시: 임의로 city를 Daegu로, temp는 double로 전달
            return outfitService.recommendClothesByTemp("Daegu", temp);
        }

        // ✅ 지하철 질문 (예: "1호선 중앙로역")
        if (message.contains("호선") && message.contains("역")) {
            try {
                String line = null;
                String station = null;

                if (message.contains("1호선")) line = "1";
                else if (message.contains("2호선")) line = "2";

                int stationIndex = message.indexOf("역");
                int start = message.lastIndexOf(" ", stationIndex);
                station = message.substring(start + 1, stationIndex).trim();

                if (line != null && station != null) {
                    return subwayService.getSubwayResponse(line, station);
                }
            } catch (Exception e) {
                return "지하철 호선 또는 역명을 정확히 입력해 주세요.";
            }
        }

        // ✅ 버스 질문 (예: "반월당정류장 309번 언제 와?")
        if (message.contains("번") && message.contains("정류장")) {
            String station = null;
            String busNumber = null;

            try {
                int stationIndex = message.indexOf("정류장");
                station = message.substring(0, stationIndex).trim();

                int busIndex = message.indexOf("번");
                int start = message.lastIndexOf(" ", busIndex);
                busNumber = message.substring(start + 1, busIndex).trim();
            } catch (Exception e) {
                return "정류장 이름과 버스 번호를 제대로 인식하지 못했어요. 다시 입력해 주세요.";
            }

            if (station != null && busNumber != null && !station.isEmpty() && !busNumber.isEmpty()) {
                BusRequestDto dto = new BusRequestDto(station, busNumber);
                return busService.getBusArrivalInfo(dto);
            } else {
                return "정류장 이름과 버스 번호를 정확히 입력해 주세요.";
            }
        }

        // ✅ 날씨 질문 (대구/하양)
        if (message.contains("대구")) {
            WeatherResponseDto dto = weatherService.getWeatherInfo("대구");
            String prompt = buildWeatherPrompt("대구", dto);
            return chatGptService.askGpt(prompt);
        } else if (message.contains("하양")) {
            WeatherResponseDto dto = weatherService.getWeatherInfo("하양");
            String prompt = buildWeatherPrompt("하양", dto);
            return chatGptService.askGpt(prompt);
        }

        // ✅ 기타 일반 질문은 GPT에 그대로 전달
        return chatGptService.askGpt(message);
    }

    private String buildWeatherPrompt(String region, WeatherResponseDto dto) {
        String umbrellaLine = dto.getRain() > 0 ? "- 우산 여부: " + dto.getUmbrella() + "\n" : "";

        return String.format(
                "'%s' 지역의 날씨를 사용자에게 알려줘.\n" +
                        "- 날씨: %s\n" +
                        "- 기온: %.1f도\n" +
                        "- 비: %.1fmm\n" +
                        "- 미세먼지: %.1f\n" +
                        "- 마스크 착용: %s\n" +
                        "%s" +
                        "이 정보를 바탕으로 자연스럽고 친절하게 한국어로 안내해줘.",
                region,
                dto.getWeather(),
                dto.getTemperature(),
                dto.getRain(),
                dto.getPm25(),
                dto.getMask(),
                umbrellaLine
        );
    }
}
