package com.example.chatbotserver.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ChatGptService {

    @Value("${gpt.api-key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-4"; // 또는 "gpt-3.5-turbo"

    private final OkHttpClient client;

    public ChatGptService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public String askGpt(String userMessage) {
        try {
            String payload = buildRequestBody(userMessage);
            Request request = buildRequest(payload);

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                log.info("GPT 응답: {}", responseBody);
                System.out.println("GPT 응답 본문:\n" + responseBody);

                if (!response.isSuccessful()) {
                    log.error("❌ GPT 응답 실패: code = {}, body = {}", response.code(), responseBody);
                    return "GPT 호출 실패 (코드: " + response.code() + ")";
                }

                return parseGptResponse(responseBody);
            }
        } catch (Exception e) {
            log.error("❌ GPT 요청 처리 중 오류 발생", e);
            return "GPT 처리 중 오류 발생: " + e.getMessage();
        }
    }

    private String buildRequestBody(String message) {
        JSONObject userMsg = new JSONObject()
                .put("role", "user")
                .put("content", message);

        JSONArray messages = new JSONArray().put(userMsg);

        return new JSONObject()
                .put("model", MODEL)
                .put("messages", messages)
                .put("temperature", 0.7)
                .toString();
    }

    private Request buildRequest(String jsonBody) {
        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        return new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
    }

    private String parseGptResponse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        JSONArray choices = json.getJSONArray("choices");
        return choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
                .trim();
    }
}
