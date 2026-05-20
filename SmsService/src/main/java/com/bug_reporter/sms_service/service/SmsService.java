package com.bug_reporter.sms_service.service;

import com.bug_reporter.sms_service.dto.SmsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class SmsService {

    @Value("${textbelt.url}")
    private String textbeltUrl;

    @Value("${textbelt.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(SmsRequest request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("phone", request.to());
        body.add("message", request.body());
        body.add("key", apiKey);

        ResponseEntity<String> response = restTemplate.postForEntity(textbeltUrl, body, String.class);
        String responseBody = response.getBody();
        if (responseBody == null || !responseBody.contains("\"success\":true")) {
            throw new RuntimeException("TextBelt response: " + responseBody);
        }
    }
}
