package org.example.dataprotal.service.impl;

import jakarta.security.auth.message.AuthException;
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
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
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

    private final ReloadableResourceBundleMessageSource messageSource;

    @Value("${email-address-of-the-admin-supervising-the.activity}")
    private String adminEmail;

    @Override
    public String sendNotificationForActivateProfile(String email) {
        log.info("Send notification for activate profile : {}", email);
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
        return messageSource.getMessage("notification.message.success", null,
                new Locale(user.getLanguage().name().toLowerCase()));
    }

    @Override
    public NotificationResponse sendNotification(NotificationRequest request) {
        log.info("Send notification : {}", request);
        User sender = userService.getById(request.senderId());
        return notificationToNotificationResponse(
                notificationRepository.save(
                        Notification.builder()
                                .language(sender.getLanguage())
                                .title(request.title())
                                .message(request.message())
                                .senderId(request.senderId())
                                .receiverId(request.receiverId())
                                .build()), sender.getEmail());
    }

    @Override
    public void sendContactForm(String title, String message, Language language, Long id) {
        notificationRepository.save(Notification
                .builder()
                .title(title)
                .message(message)
                .language(language)
                .receivedTime(LocalDateTime.now())
                .receiverId(id)
                .build());
    }

    @Override
    public List<NotificationResponseForOverView> getNotSeenNotifications() throws AuthException {
        User currentUser = userService.getCurrentUser();
        log.info("Get not seen notifications for user : {}", currentUser.getFirstName());
        return notificationRepository.getByReceiverIdAndNotSeen(currentUser.getId())
                .stream()
                .map(notification -> translateNotification(notification, currentUser.getLanguage()))
                .map(NotificationMapper::notificationToNotificationResponseForOverView)
                .toList();
    }

    @Override
    public List<NotificationResponse> getAllNotifications() throws AuthException {
        User currentUser = userService.getCurrentUser();
        log.info("Get all notifications for user : {}", currentUser.getFirstName());
        return mapNotificationsToResponses(
                notificationRepository.getByReceiverId(
                        currentUser.getId()));
    }

    @Override
    public List<NotificationResponse> getAllSendNotification() throws AuthException {
        User currentUser = userService.getCurrentUser();
        log.info("Get all send notifications for user : {}", currentUser.getFirstName());
        return mapNotificationsToResponses(
                notificationRepository.getBySenderId(
                        currentUser.getId()));
    }

    @Override
    public NotificationResponse getNotificationById(Long id) throws AuthException {
        log.info("Get notification by id : {}", id);
        Notification notification = getById(id);
        notification = checkIsSeen(notification);
        return notificationToNotificationResponse(notification,
                userService.getById(notification.getSenderId()).getEmail());
    }

    private Notification checkIsSeen(Notification notification) throws AuthException {
        if (!notification.isSeen() && notification.getReceiverId().equals(userService.getCurrentUser().getId())) {
            notification.setSeen(true);
            notification = notificationRepository.save(notification);
        }
        return notification;
    }

    @Override
    public List<NotificationResponse> searchNotificationByTitle(String title) {
        log.info("Search notification by title : {}", title);
        return mapNotificationsToResponses(notificationRepository.searchNotificationByName(title));
    }

    @Override
    public NotificationResponse updateNotification(Long notificationId, NotificationRequest request) {
        log.info("Update notification by id : {}", notificationId);
        Notification notification = getById(notificationId);
        notification.setTitle(request.title());
        notification.setMessage(request.message());
        notification.setReceivedTime(LocalDateTime.now());
        notification.setReceiverId(request.receiverId());
        return notificationToNotificationResponse(notificationRepository.save(notification), userService.getById(request.senderId()).getEmail());
    }

    @Override
    public List<NotificationResponse> deleteNotificationById(Long id) throws AuthException {
        log.info("Delete notification by id : {}", id);
        notificationRepository.deleteById(id);
        return mapNotificationsToResponses(notificationRepository.getByReceiverId(userService.getCurrentUser().getId()));
    }

    @Override
    public String deleteAllNotifications() throws AuthException {
        log.info("Delete all notifications");
        User currentUser = userService.getCurrentUser();
        notificationRepository.deleteAll(notificationRepository.getByReceiverId(currentUser.getId()));
        return "Deletion is successfully";
    }

    @Override
    public NotificationResponse translateNotification(Long id, String language) throws AuthException {
        log.info("Translate notification by id : {} to {}", id, language);
        Notification notification = getById(id);
        User sender = userService.getById(notification.getSenderId());
        notification = checkIsSeen(notification);
        return notificationToNotificationResponse(
                translateNotification(notification,
                        Language.valueOf(language.toUpperCase())),
                sender.getEmail());
    }

    private Notification translateNotification(Notification notification, Language to) {
        if (!notification.getLanguage().equals(to)) {
            notification.setTitle(translateService.translate(
                    notification.getLanguage().name().toLowerCase(),
                    to.name().toLowerCase(),
                    notification.getTitle()
            ));
            notification.setMessage(translateService.translate(
                    notification.getLanguage().name().toLowerCase(),
                    to.name().toLowerCase(),
                    notification.getMessage()
            ));
        }
        return notification;
    }

    private String getMessageForActivateProfile(Locale locale, User user, User admin) {
        String message = messageSource.getMessage(
                "activate-request-massage",
                null,
                locale) +
                " ( email : " + user.getEmail() +
                " , deactivate reason : " +
                translateService.translate(
                        user.getLanguage().name().toLowerCase(),
                        "en",
                        user.getDeactivateReason()) +
                " , deactivate time : " + user.getDeactivateTime() + " ) ";
        return translateService.translate(
                "en",
                admin.getLanguage().name().toLowerCase(),
                message);
    }

    private Notification getById(Long id) {
        return notificationRepository.findById(id).orElseThrow(() -> {
            log.error("Notification not found with id : {}", id);
            return new NoSuchElementException("Notification not found with id : " + id);
        });
    }

    private List<NotificationResponse> mapNotificationsToResponses(List<Notification> notifications) {
        return notifications
                .stream()
                .map(notification -> notificationToNotificationResponse(notification,
                        notification.getSenderId() == null ? "unknown user" : userService.getById(notification.getSenderId()).getEmail()))
                .toList();
    }
}
