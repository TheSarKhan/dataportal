package org.example.dataprotal.dto.response;

import java.time.LocalDateTime;

public record NotificationResponseForOverView(String title,
                                              String message,
                                              LocalDateTime receivedDate) {
}
