package org.example.dataprotal.service.impl;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.dto.response.HeaderResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.service.HeaderService;
import org.example.dataprotal.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeaderServiceImpl implements HeaderService {
    private final UserService userService;
    private final NotificationServiceImpl notificationService;

    @Override
    public HeaderResponse getHeader() throws AuthException {
        log.info("Get profile data for header");
        User currentUser = userService.getCurrentUser();
        List<NotificationResponseForOverView> unseenNotifications =
                notificationService.getNotSeenNotifications();
        return new HeaderResponse(
                currentUser.getLanguage(),
                unseenNotifications.size(),
                unseenNotifications,
                currentUser.getProfileImage());
    }
}
