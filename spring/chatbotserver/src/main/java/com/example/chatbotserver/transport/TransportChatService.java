package com.example.chatbotserver.transport;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TransportChatService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String search(String message) {
        try {
            if (message.contains("호선") || message.contains("지하철")) {
                return callSubwayApi(message);
            } else if (message.contains("정류장") || message.contains("버스") || message.contains("급행")) {
                return callBusApi(message);
            } else if (message.contains("에서") && message.contains("까지")) {
                return callDirectionApi(message);
            } else {
                return "정류장 이름과 버스 번호 또는 출발지와 목적지를 정확히 입력해주세요.";
            }
        } catch (Exception e) {
            return "교통 정보를 처리하는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    private String callSubwayApi(String message) {
        String station = extractStationName(message);
        String line = extractSubwayLine(message);

        if (station.isEmpty() || line.isEmpty()) {
            return "지하철역 이름과 호선을 정확히 입력해주세요. 예시: '반월당역 1호선 언제와?'";
        }

        String url = "http://localhost:8080/api/subway/info";

        Map<String, String> payload = new HashMap<>();
        payload.put("station", station);
        payload.put("line", line);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return extractData(response, "지하철 도착 정보를");
    }

    private String callBusApi(String message) {
        String stationName = extractStationName(message);
        String busNumber = extractBusNumber(message);

        if (stationName.isEmpty() || busNumber.isEmpty()) {
            return "정류장 이름과 버스 번호를 정확히 입력해주세요. 예시: '강북소방서앞 정류장 급행2 언제와?'";
        }

        String url = "http://localhost:8080/api/bus/arrival";

        Map<String, String> payload = new HashMap<>();
        payload.put("stationName", stationName);
        payload.put("busNumber", busNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return extractData(response, "버스 도착 정보를");
    }

    private String callDirectionApi(String message) {
        String url = "http://localhost:8080/api/direction/find";
        String start = message.substring(0, message.indexOf("에서")).trim();
        String end = message.substring(message.indexOf("에서") + 2, message.indexOf("까지")).trim();

        Map<String, String> payload = new HashMap<>();
        payload.put("start", start);
        payload.put("end", end);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        return extractData(response, "길찾기 정보를");
    }

    private String extractData(ResponseEntity<Map> response, String errorPrefix) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Object dataField = response.getBody().get("data");
            return (dataField != null)
                    ? dataField.toString()
                    : errorPrefix + " 불러오는 중 문제가 발생했습니다.";
        } else {
            return errorPrefix + " 불러올 수 없습니다.";
        }
    }

    private String extractStationName(String msg) {
        int idx = msg.indexOf("역");
        if (idx != -1) return msg.substring(0, idx).trim();

        idx = msg.indexOf("정류장");
        if (idx != -1) return msg.substring(0, idx + 3).trim();

        return "";
    }

    private String extractSubwayLine(String msg) {
        int idx = msg.indexOf("호선");
        if (idx != -1) {
            int start = idx - 1;
            while (start >= 0 && Character.isDigit(msg.charAt(start))) start--;
            return msg.substring(start + 1, idx + 2).trim();
        }
        return "";
    }

    private String extractBusNumber(String msg) {
        StringBuilder sb = new StringBuilder();
        boolean started = false;
        for (char c : msg.toCharArray()) {
            if (Character.isDigit(c) || (c >= '가' && c <= '힣')) {
                sb.append(c);
                started = true;
            } else if (started) {
                break;
            }
        }
        return sb.toString().trim();
    }
}
