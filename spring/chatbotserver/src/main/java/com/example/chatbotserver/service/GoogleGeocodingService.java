package com.example.chatbotserver.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GoogleGeocodingService {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${google.api.key}")
    private String apiKey;

    /**
     * 사용자가 입력한 주소를 위도, 경도로 변환
     * 예: "하양역" → "35.913123,128.816987"
     */
    public String getLatLng(String address) {
        try {
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s",
                    java.net.URLEncoder.encode(address, "UTF-8"),
                    apiKey
            );

            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                log.warn("❌ Geocoding API 응답 실패");
                return null;
            }

            String body = response.body().string();
            log.info("📍 Geocoding 응답: {}", body);

            JsonNode root = objectMapper.readTree(body);
            JsonNode results = root.path("results");

            if (results.isArray() && results.size() > 0) {
                JsonNode location = results.get(0).path("geometry").path("location");
                String lat = location.path("lat").asText();
                String lng = location.path("lng").asText();
                return lat + "," + lng;
            } else {
                log.warn("❌ 주소 변환 실패: {}", address);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Geocoding API 호출 오류", e);
            return null;
        }
    }
}
