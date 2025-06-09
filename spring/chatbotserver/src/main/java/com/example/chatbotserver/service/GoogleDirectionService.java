package com.example.chatbotserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDirectionService {

    @Value("${google.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoogleGeocodingService geocodingService;

    public String getRouteComparison(String startAddress, String endAddress) {
        // 주소 → 좌표 변환
        String startLatLng = geocodingService.getLatLng(startAddress);
        String endLatLng = geocodingService.getLatLng(endAddress);

        if (startLatLng == null || endLatLng == null) {
            return "출발지 또는 도착지를 찾을 수 없습니다.";
        }

        StringBuilder result = new StringBuilder();

        // 각 이동 수단 모드별로 결과 요청
        for (String mode : new String[]{"driving", "transit"}) {
            String summary = getDirectionSummary(startLatLng, endLatLng, mode);
            result.append(String.format("%s 이동: %s\n", translateMode(mode), summary));
        }

        return result.toString().trim();
    }

    private String getDirectionSummary(String startLatLng, String endLatLng, String mode) {
        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&mode=%s%s&key=%s",
                    startLatLng,
                    endLatLng,
                    mode,
                    mode.equals("transit") ? "&departure_time=now" : "",
                    apiKey
            );

            log.info("🚗 Google 길찾기 요청 - 모드: {}, URL: {}", mode, url);

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            String body = response.body().string();
            log.info("📦 구글 길찾기 API 응답 ({}): {}", mode, body);

            JsonNode root = objectMapper.readTree(body);

            if ("OK".equals(root.path("status").asText()) && root.path("routes").size() > 0) {
                JsonNode leg = root.path("routes").get(0).path("legs").get(0);
                String duration = leg.path("duration").path("text").asText();
                String distance = leg.path("distance").path("text").asText();
                return duration + " (" + distance + ")";
            } else {
                return "경로가 없습니다.";
            }

        } catch (Exception e) {
            log.error("❌ Google Direction 요청 실패", e);
            return "요청 실패";
        }
    }

    private String translateMode(String mode) {
        switch (mode) {
            case "driving":
                return "자동차";
            case "transit":
                return "대중교통";
            default:
                return mode;
        }
    }
}
