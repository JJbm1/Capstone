package com.example.chatbotserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class BusApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bus.api-key}") // yml에 인코딩된 상태로 저장돼 있어야 함!
    private String apiKey;

    public String getArrivalTime(String stationName, String busNumber) {
        try {
            String url = "https://apis.data.go.kr/6270000/dbmsapi01"
                    + "?serviceKey=" + apiKey  // ❗인코딩된 상태 그대로 사용
                    + "&station=" + URLEncoder.encode(stationName, StandardCharsets.UTF_8)
                    + "&line=" + URLEncoder.encode(busNumber, StandardCharsets.UTF_8);

            String xml = restTemplate.getForObject(url, String.class);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

            NodeList items = doc.getElementsByTagName("item");
            if (items.getLength() == 0) {
                return "해당 정류장의 버스 도착 정보를 찾을 수 없습니다.";
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < items.getLength(); i++) {
                String lineNo = doc.getElementsByTagName("lineNo").item(i).getTextContent();
                String predictTime1 = doc.getElementsByTagName("predictTime1").item(i).getTextContent();
                result.append(String.format("%s번 버스는 약 %s분 후 도착 예정입니다.\n", lineNo, predictTime1));
            }

            return result.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "버스 도착 정보를 파싱하는 중 오류가 발생했습니다.";
        }
    }
}
