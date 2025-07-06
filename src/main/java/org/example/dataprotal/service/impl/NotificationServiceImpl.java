package org.example.dataprotal.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.request.NotificationRequest;
import org.example.dataprotal.dto.response.NotificationResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.mapper.NotificationMapper;
import org.example.dataprotal.model.user.Notification;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.NotificationRepository;
import org.example.dataprotal.service.NotificationService;
import org.example.dataprotal.service.TranslateService;
import org.example.dataprotal.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.example.dataprotal.mapper.NotificationMapper.notificationToNotificationResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    private final TranslateService translateService;

    private final UserService userService;

    private final MessageSource messageSource;

    @Value("${email-address-of-the-admin-supervising-the-activity}")
    private String adminEmail;

    @Override
    public String sendNotificationForActivateProfile(String email) {
        User user = userService.getByEmail(email);
        User admin = userService.getByEmail(adminEmail);
        Locale locale = new Locale(admin.getLanguage().name().toLowerCase());
        Notification notification = Notification.builder()
                .language(admin.getLanguage())
                .title(messageSource.getMessage(
                        "activate-request-title",
                        null,
                        locale))
                .message(getMessageForActivateProfile(locale, user, admin))
                .receivedTime(LocalDateTime.now())
                .senderId(user.getId())
                .receiverId(admin.getId())
                .build();
        notificationRepository.save(notification);
        return "Request send successfully.";
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request, Language language) {
        User sender = userService.getById(request.senderId());
        return notificationToNotificationResponse(
                notificationRepository.save(
                        Notification.builder()
                                .language(language)
                                .title(request.title())
                                .message(request.message())
                                .senderId(request.senderId())
                                .receiverId(request.receiverId())
                                .build()), sender.getEmail());
    }

    @Override
    public List<NotificationResponseForOverView> getNotSeenNotifications(User currentUser) {
        return notificationRepository.getByReceiverIdAndNotSeen(currentUser.getId())
                .stream()
                .map(notification -> translateNotification(notification, currentUser.getLanguage()))
                .map(NotificationMapper::notificationToNotificationResponseForOverView)
                .toList();
    }

    @Override
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = getByID(id);
        return notificationToNotificationResponse(notification,
                userService.getById(notification.getSenderId()).getEmail());
    }

    private Notification getByID(Long id) {
        return notificationRepository.findById(id).orElseThrow(() -> {
            log.error("Notification not found with id : {}", id);
            return new NoSuchElementException("Notification not found with id : " + id);
        });
    }

    @Override
    public List<NotificationResponse> searchNotificationByTitle(String title) {
        return notificationRepository.searchNotificationByName(title)
                .stream()
                .map(notification -> notificationToNotificationResponse(notification,
                        userService.getById(notification.getSenderId()).getEmail()))
                .toList();
    }

    @Override
    public void deleteNotificationById() {

    }

    @Override
    public void deleteAllNotifications() {

    }

    @Override
    public void translateNotification() {

    }

    private Notification translateNotification(Notification notification, Language to) {
        if (!notification.getLanguage().equals(to)) {
            notification.setTitle(translateService.translate(
                    notification.getTitle(),
                    notification.getLanguage().name().toLowerCase(),
                    to.name().toLowerCase()));
            notification.setMessage(translateService.translate(
                    notification.getMessage(),
                    notification.getLanguage().name().toLowerCase(),
                    to.name().toLowerCase()
            ));
        }
        return notification;
    }

    private String getMessageForActivateProfile(Locale locale, User user, User admin) {
        return messageSource.getMessage(
                "activate-request-massage",
                null,
                locale) +
                " ( email : " + user.getEmail() +
                " , " +
                translateService.translate(
                        "deactivate reason",
                        "en",
                        admin.getLanguage().name().toLowerCase()) +
                " : " +
                translateService.translate(
                        user.getDeactivateReason(),
                        user.getLanguage().name().toLowerCase(),
                        admin.getLanguage().name().toLowerCase()) +
                " , " +
                translateService.translate(
                        "deactivate time",
                        "en",
                        admin.getLanguage().name().toLowerCase()) +
                " : " + user.getDeactivateTime() + " ) ";
    }
}
