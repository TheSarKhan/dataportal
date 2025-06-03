package org.example.dataprotal.service.impl;

import lombok.RequiredArgsConstructor;
  import org.example.dataprotal.service.BotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {
    private final RestTemplate restTemplate;
    @Value("${spring.application.bot-url}")
    private String botUrl;

    @Override
    public String askBot(String question) {
        String url = botUrl + "/api/ask";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("question", question); // Python artık "question" bekliyor

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        Map responseBody = response.getBody();
        if (responseBody == null || Boolean.FALSE.equals(responseBody.get("success"))) {
            return responseBody != null ? responseBody.get("message").toString() : "Cavab yoxdur";
        }

        Object answer = responseBody.get("answer");
        return answer != null ? answer.toString() : "Bot cavab verə bilmədi.";
    }


}
