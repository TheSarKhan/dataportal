package org.example.dataprotal.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.service.TranslateService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTranslator {
    private final TranslateService translateService;

    private static final List<String> TARGET_LANGUAGES = Arrays.stream(Language.values())
            .map(language -> language.name().toLowerCase()).toList();

    private static final String BASE_PATH = "src/main/resources/i18n/";

    private static final String BASE_FILE = "messages_en.properties";

    @EventListener(ApplicationReadyEvent.class)
    public void generateTranslatedFiles() throws IOException, InterruptedException {
        File baseFile = new File(BASE_PATH + BASE_FILE);

        if (!baseFile.exists()) {
            log.error("Base message file not found: " + BASE_FILE);
            return;
        }

        Properties baseMessages = readProperties(baseFile);

        for (String lang : TARGET_LANGUAGES) {
            Properties translated = new Properties();

            for (String key : baseMessages.stringPropertyNames()) {
                String originalValue = baseMessages.getProperty(key);
                String translatedValue = translateService.translate("en", lang, originalValue);
                log.info("Translating '{}' to {}: '{}'", originalValue, lang, translatedValue);
                translated.setProperty(key, translatedValue);
                Thread.sleep(500);
            }

            writeProperties(translated, BASE_PATH + "messages_" + lang + ".properties");
            log.info("messages_{}.properties generated successfully.", lang);
        }
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
}
