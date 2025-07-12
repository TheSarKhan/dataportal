package org.example.dataprotal.mapper;

import org.example.dataprotal.dto.response.NotificationResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.model.user.Notification;

import java.util.HashMap;
import java.util.Map;

public class NotificationMapper {
    public static NotificationResponseForOverView notificationToNotificationResponseForOverView(Notification notification) {
        return new NotificationResponseForOverView(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getReceivedTime());
    }

    public static NotificationResponse notificationToNotificationResponse(Notification notification, String senderEmail) {
        Map<Language, String> languages = new HashMap<>();
        for (Language language : Language.values()) {
            languages.put(language, language.getFullName());
        }
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getSenderId(),
                senderEmail,
                notification.getLanguage(),
                notification.getReceivedTime(),
                languages
        );
    }
}
