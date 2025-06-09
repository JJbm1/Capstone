package com.example.chatbotserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectionService {

    private final GoogleDirectionService googleDirectionService;
    private final ChatGptService chatGptService;

    public String getDirectionInfo(String message) {
        log.info("🛰️ 사용자 입력 메시지: {}", message);

        // 1. GPT에게 자연어에서 출발지/도착지 추출 요청 (형식 명시)
        String prompt = String.format(
                "다음 문장은 사용자의 대중교통 길찾기 요청입니다:\n\"%s\"\n" +
                        "출발지와 도착지를 추론하여 다음 형식으로 응답하세요:\n" +
                        "출발지: [출발지]\n도착지: [도착지]",
                message
        );

        String extracted = chatGptService.askGpt(prompt);
        log.info("📦 GPT 출발지/도착지 추출 결과: {}", extracted);

        // 2. GPT 응답에서 출발지, 도착지 파싱
        String start = null;
        String destination = null;

        try {
            for (String line : extracted.split("\n")) {
                if (line.startsWith("출발지:")) {
                    start = line.replace("출발지:", "").trim();
                } else if (line.startsWith("도착지:")) {
                    destination = line.replace("도착지:", "").trim();
                }
            }
        } catch (Exception e) {
            log.error("❌ 출발지/도착지 파싱 오류", e);
            return "출발지와 도착지를 추출하지 못했습니다.";
        }

        if (start == null || destination == null || start.isEmpty() || destination.isEmpty()) {
            return "출발지 또는 도착지를 인식하지 못했습니다.";
        }

        // 3. 주소 보정 적용
        start = convertToFullAddress(start);
        destination = convertToFullAddress(destination);

        log.info("🚏 추출된 출발지: {}, 도착지: {}", start, destination);
        log.info("🛰️ 구글 길찾기 요청 시작 - from: {} to: {}", start, destination);

        // 4. 구글 길찾기 API 요청 (요약 정보 획득)
        String routeSummary = googleDirectionService.getRouteComparison(start, destination);
        log.info("🧭 구글 길찾기 요약 결과:\n{}", routeSummary);

        // 5. GPT에게 최종 자연어 안내 요청
        String finalPrompt = String.format(
                "'%s'에서 '%s'까지 가는 방법을 알려주세요. 다음은 구글 길찾기 요약 정보입니다:\n%s\n\n" +
                        "위 정보를 바탕으로 자연스럽고 친절하게 대중교통 길찾기 방법을 안내해주세요.",
                start, destination, routeSummary
        );

        log.info("💬 GPT 최종 안내 프롬프트 생성, 호출 중...");
        String gptResponse = chatGptService.askGpt(finalPrompt);
        log.info("✅ GPT 최종 응답 완료: {}", gptResponse);

        return gptResponse;
    }

    public String getDirectionInfo(String start, String destination) {
        String message = start + "에서 " + destination + "까지";
        return getDirectionInfo(message);
    }

    private String convertToFullAddress(String place) {
        switch (place) {
            case "하양역":
                return "경상북도 경산시 하양읍 하양역";
            case "경일대학교":
                return "경상북도 경산시 하양읍 가마실길 50 경일대학교";
            case "하양":
                return "경상북도 경산시 하양읍";
            case "대구역":
                return "대구광역시 북구 칠성동2가 302-155 대구역";
            case "동대구역":
                return "대구광역시 동구 동대구로 550 동대구역";
            case "반월당":
                return "대구광역시 중구 달구벌대로 2077 반월당역";
            case "동성로":
                return "대구광역시 중구 동성로2가 동성로거리";
            case "중앙로":
                return "대구광역시 중구 중앙로";
            default:
                return place;
        }
    }
}
