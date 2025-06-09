package com.example.chatbotserver.service;

import com.example.chatbotserver.dto.BusRequestDto;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class BusService {

    @Value("${bus.api-key}")
    private String apiKey;

    private final ChatGptService chatGptService;

    // ✅ JSON 파일에서 정류장 이름으로 bsId 조회
    public static String findBsIdByStationName(String stationName) {
        try {
            String json = Files.readString(Paths.get("bus_stations.json"), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(json);
            JSONArray stations = obj.getJSONObject("body")
                    .getJSONObject("items")
                    .getJSONArray("bs");

            // 1. 정확히 일치하는 정류장 우선
            for (int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);
                String bsNm = station.getString("bsNm").trim();
                if (bsNm.equals(stationName)) {
                    System.out.println("✅ [정확 일치] " + bsNm + " → " + station.getString("bsId"));
                    return station.getString("bsId");
                }
            }

            // 2. 없으면 부분일치 fallback
            for (int i = 0; i < stations.length(); i++) {
                JSONObject station = stations.getJSONObject(i);
                String bsNm = station.getString("bsNm").trim();
                if (bsNm.contains(stationName)) {
                    System.out.println("⚠️ [부분 일치] " + bsNm + " → " + station.getString("bsId"));
                    return station.getString("bsId");
                }
            }

            System.out.println("❌ '" + stationName + "' 정류장 못 찾음");
            return null;

        } catch (Exception e) {
            System.out.println("❌ 오류 발생: " + e.getMessage());
            return null;
        }
    }



    // ✅ 정류장명 + 버스번호로 도착 정보 조회
    public String getBusArrivalInfo(BusRequestDto requestDto) {
        try {
            String bsId = findBsIdByStationName(requestDto.getStationName());
            if (bsId == null) {
                return "해당 정류장의 ID를 찾을 수 없습니다.";
            }

            String url = String.format(
                    "https://apis.data.go.kr/6270000/dbmsapi01/getRealtime?serviceKey=%s&bsId=%s&routeNo=%s",
                    apiKey,
                    URLEncoder.encode(bsId, StandardCharsets.UTF_8),
                    URLEncoder.encode(requestDto.getBusNumber(), StandardCharsets.UTF_8)
            );

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            if (!response.isSuccessful() || response.body() == null) {
                return "버스 정보를 불러올 수 없습니다.";
            }

            String raw = response.body().string();

            String prompt = String.format(
                    "사용자가 '%s 정류장'에서 '%s번 버스' 도착 정보를 물어봤어.\n" +
                            "아래는 API에서 받은 원본 응답이야:\n%s\n" +
                            "이걸 기반으로 자연스럽게 대답해줘. (한국어)",
                    requestDto.getStationName(),
                    requestDto.getBusNumber(),
                    raw
            );

            return chatGptService.askGpt(prompt);

        } catch (IOException e) {
            return "버스 도착 정보를 불러오는 중 오류가 발생했습니다.";
        }
    }
}
