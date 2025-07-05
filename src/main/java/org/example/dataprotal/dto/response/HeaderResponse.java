package org.example.dataprotal.dto.response;

import org.example.dataprotal.enums.Language;

import java.util.List;

public record HeaderResponse(Language language,
                             int newNotificationCount,
                             List<NotificationResponseForOverView> newNotifications,
                             String profileImage) {
}
