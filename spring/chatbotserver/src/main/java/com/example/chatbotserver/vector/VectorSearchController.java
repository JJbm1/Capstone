package com.example.chatbotserver.vector;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class VectorSearchController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/search-vector")
    public String searchVector(@RequestParam String query) {
        String url = "http://localhost:5005/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return restTemplate.getForObject(url, String.class);
    }
}
