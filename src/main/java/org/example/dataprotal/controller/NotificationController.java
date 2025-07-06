package org.example.dataprotal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.example.dataprotal.dto.request.NotificationRequest;
import org.example.dataprotal.dto.response.NotificationResponse;
import org.example.dataprotal.dto.response.NotificationResponseForOverView;
import org.example.dataprotal.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notification Controller",
        description = "APIs for managing notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/activate/{email}")
    @Operation(summary = "Send activation notification",
            description = "Sends activation email to the given user email")
    public ResponseEntity<String> sendNotificationForActivateProfile(@PathVariable String email) {
        return ResponseEntity.ok(notificationService.sendNotificationForActivateProfile(email));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send notification",
            description = "Sends a notification (Admin only)")
    public ResponseEntity<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.sendNotification(request));
    }

    @GetMapping("/not-seen")
    @Operation(summary = "Get unseen notifications",
            description = "Fetches all unseen notifications for the user")
    public ResponseEntity<List<NotificationResponseForOverView>> getNotSeenNotifications() throws AuthException {
        return ResponseEntity.ok(notificationService.getNotSeenNotifications());
    }

    @GetMapping
    @Operation(summary = "Get all notifications",
            description = "Fetches all notifications for the user")
    public ResponseEntity<List<NotificationResponse>> getAllNotification() throws AuthException {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get sent notifications",
            description = "Fetches all notifications sent by admin")
    public ResponseEntity<List<NotificationResponse>> getAllSendNotification() throws AuthException {
        return ResponseEntity.ok(notificationService.getAllSendNotification());
    }

    @GetMapping("/search/{title}")
    @Operation(summary = "Search notification by title",
            description = "Searches notifications by their title")
    public ResponseEntity<List<NotificationResponse>> searchNotification(@PathVariable String title) {
        return ResponseEntity.ok(notificationService.searchNotificationByTitle(title));
    }

    @GetMapping("/id/{id}")
    @Operation(summary = "Get notification by ID",
            description = "Fetches a notification by its ID")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }

    @GetMapping("/translate/id/{id}/language/{language}")
    @Operation(summary = "Translate notification",
            description = "Translates a notification to the specified language")
    public ResponseEntity<NotificationResponse> translateNotification(@PathVariable Long id,
                                                                      @PathVariable String language) throws AuthException {
        return ResponseEntity.ok(notificationService.translateNotification(id, language));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update notification",
            description = "Updates an existing notification (Admin only)")
    public ResponseEntity<NotificationResponse> updateNotification(@RequestBody Long notificationId,
                                                                   @RequestBody NotificationRequest request) throws AuthException {
        return ResponseEntity.ok(notificationService.updateNotification(notificationId, request));
    }

    @DeleteMapping("/id/{id}")
    @Operation(summary = "Delete notification by ID",
            description = "Deletes a specific notification by its ID")
    public ResponseEntity<List<NotificationResponse>> deleteNotificationById(@PathVariable Long id) throws AuthException {
        return ResponseEntity.ok(notificationService.deleteNotificationById(id));
    }

    @DeleteMapping("/all")
    @Operation(summary = "Delete all notifications",
            description = "Deletes all notifications for the user")
    public ResponseEntity<String> deleteAllNotifications() throws AuthException {
        return ResponseEntity.ok(notificationService.deleteAllNotifications());
    }
}
