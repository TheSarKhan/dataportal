package org.example.dataprotal.service.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.service.TranslateService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

@Slf4j
@Service
public class LingvaTranslateService implements TranslateService {
    private final RestTemplate restTemplate;

    private static final List<String> TARGET_LANGUAGES = Arrays.stream(Language.values())
            .map(language -> language.name().toLowerCase())
            .filter(language -> !language.equals(Language.EN.name().toLowerCase())).toList();

    private static final String BASE_PATH = "src/main/resources/i18n/";

    private static final String BASE_FILE = "messages_en.properties";

    @Value("${lingva-translate.base-url}")
    String baseUrl;

    public LingvaTranslateService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String translate(String from, String to, String text) {
        String url = UriComponentsBuilder
                .fromUriString(baseUrl + "/{from}/{to}/{text}")
                .buildAndExpand(from, to, text)
                .toUriString();

        TranslationResponse response = restTemplate.getForObject(url, TranslationResponse.class);
        return response != null ? response.getTranslation() : null;
    }

    @Override
    public String translateFiles() throws IOException, InterruptedException {
        File baseFile = new File(BASE_PATH + BASE_FILE);

        if (!baseFile.exists()) {
            log.error("Base message file not found: " + BASE_FILE);
            return "Base message file not found: " + BASE_FILE;
        }

        Properties baseMessages = readProperties(baseFile);

        for (String lang : TARGET_LANGUAGES) {
            Properties translated = new Properties();

            for (String key : baseMessages.stringPropertyNames()) {
                String originalValue = baseMessages.getProperty(key);
                String translatedValue = translate("en", lang, originalValue);
                log.info("Translating '{}' to {}: '{}'", originalValue, lang, translatedValue);
                translated.setProperty(key, translatedValue);
                Thread.sleep(50);
            }

            writeProperties(translated, BASE_PATH + "messages_" + lang + ".properties");
            log.info("messages_{}.properties generated successfully.", lang);
        }
        log.info("Translation process finished successfully.");
        return "Translation process finished successfully.";
    }

    private Properties readProperties(File file) throws IOException {
        Properties properties = new Properties();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.startsWith("#") && line.contains("=")) {
                    int idx = line.indexOf('=');
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    properties.setProperty(key, value);
                }
            }
        }
        return properties;
    }

    private void writeProperties(Properties properties, String path) throws IOException {
        try (OutputStream output = new FileOutputStream(path)) {
            properties.store(output, null);
        }
    }

    @Setter
    @Getter
    public static class TranslationResponse {
        private String translation;
    }
}
