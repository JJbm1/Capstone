package com.example.chatbotserver.bus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class BusStationFetcher {

    public static void main(String[] args) {
        String apiKey = "o%2BddpNemtjeeR%2FpdLdW%2FRpEed6UjWiD3%2F9qUXTqGOkNIaQTHuqyE3Aabltx9Wbq9D5uULViZkymVDflSNLE%2FaA%3D%3D";
        String url = "https://apis.data.go.kr/6270000/dbmsapi01/getBasic?serviceKey=" + apiKey + "&_type=json";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                System.out.println("❌ 요청 실패: " + response);
                return;
            }

            String jsonData = response.body().string();

            // JSON 파일로 저장
            try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream("bus_stations.json"), StandardCharsets.UTF_8)) {
                writer.write(jsonData);
                System.out.println("✅ JSON 저장 완료 (UTF-8): bus_stations.json");
            }

        } catch (IOException e) {
            System.out.println("❌ 오류 발생: " + e.getMessage());
        }
    }
}
