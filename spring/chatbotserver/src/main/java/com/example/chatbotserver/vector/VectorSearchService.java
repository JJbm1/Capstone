package com.example.chatbotserver.vector;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VectorSearchService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String search(String query) {
        String url = "http://localhost:5005/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return restTemplate.getForObject(url, String.class);
    }
}
