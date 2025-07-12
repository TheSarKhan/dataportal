package org.example.dataprotal.service;

import jakarta.security.auth.message.AuthException;
import org.example.dataprotal.dto.request.NotificationRequest;
import org.example.dataprotal.dto.response.NotificationResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;

import java.util.List;

public interface NotificationService {
    String sendNotificationForActivateProfile(String email);

    NotificationResponse sendNotification(NotificationRequest request);

    List<NotificationResponseForOverView> getNotSeenNotifications() throws AuthException;

    List<NotificationResponse> getAllNotifications() throws AuthException;

    List<NotificationResponse> getAllSendNotification() throws AuthException;

    NotificationResponse getNotificationById(Long id) throws AuthException;

    List<NotificationResponse> searchNotificationByTitle(String title);

    NotificationResponse updateNotification(Long notificationId, NotificationRequest request);

    List<NotificationResponse> deleteNotificationById(Long id) throws AuthException;

    String deleteAllNotifications() throws AuthException;

    NotificationResponse translateNotification(Long id, String language) throws AuthException;
}
