package org.example.dataprotal.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.service.TranslateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class LingvaTranslateService implements TranslateService {
    private final RestTemplate restTemplate;

    @Value("${lingva-translate.base-url}")
    String baseUrl;

    public String translate(String from, String to, String text) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/{from}/{to}/{text}")
                .buildAndExpand(from, to, text)
                .toUriString();

        TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);
        return response != null ? response.getTranslation() : null;
    }

    @Setter
    @Getter
    public static class TranslationResponse {
        private String translation;
    }
}
