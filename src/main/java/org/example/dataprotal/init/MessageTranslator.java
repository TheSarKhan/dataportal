package org.example.dataprotal.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.service.TranslateService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageTranslator {
    private final TranslateService translateService;

    @EventListener(ApplicationReadyEvent.class)
    public void generateTranslatedFiles() throws IOException, InterruptedException {
        Instant start = Instant.now();
        translateService.translateFiles();
        Instant finish = Instant.now();
        log.info("Translation process took {} ms.", finish.toEpochMilli() - start.toEpochMilli());
    }
}
