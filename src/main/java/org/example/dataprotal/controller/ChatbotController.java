package org.example.dataprotal.controller;

import lombok.RequiredArgsConstructor;
import org.example.dataprotal.service.BotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotController {
    private final BotService botService;

    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String userMessage = body.get("message");
        String botResponse = botService.askBot(userMessage.toLowerCase());
        return ResponseEntity.ok(Collections.singletonMap("response", botResponse));
    }
}
