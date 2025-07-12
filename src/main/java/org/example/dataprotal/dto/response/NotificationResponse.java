package org.example.dataprotal.dto.response;

import org.example.dataprotal.enums.Language;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationResponse(Long id,
                                   String title,
                                   String message,
                                   Long senderId,
                                   String senderEmail,
                                   Language language,
                                   LocalDateTime receivedTime,
                                   Map<Language, String> availableLanguages) {
}
