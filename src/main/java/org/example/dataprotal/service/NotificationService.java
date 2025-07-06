package org.example.dataprotal.service;

import org.example.dataprotal.dto.request.NotificationRequest;
import org.example.dataprotal.dto.response.NotificationResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.model.user.User;

import java.util.List;

public interface NotificationService {
    String sendNotificationForActivateProfile(String email);

    NotificationResponse sendNotification(NotificationRequest request, Language language);

    List<NotificationResponseForOverView> getNotSeenNotifications(User currentUser);

    NotificationResponse getNotificationById(Long id);

    List<NotificationResponse> searchNotificationByTitle(String title);

    void deleteNotificationById();

    void deleteAllNotifications();

    void translateNotification();

}
