package org.example.dataprotal.dto.request;

public record NotificationRequest(String title,
                                  String message,
                                  Long senderId,
                                  Long receiverId) {
}
