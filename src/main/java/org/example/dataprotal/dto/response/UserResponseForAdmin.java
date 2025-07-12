package org.example.dataprotal.dto.response;

import org.example.dataprotal.enums.Role;
import org.example.dataprotal.enums.Subscription;

import java.time.LocalDateTime;

public record UserResponseForAdmin(Long id,
                                   String firstName,
                                   String lastName,
                                   String email,
                                   String phoneNumber,
                                   String workplace,
                                   String position,
                                   String profileImage,
                                   Role role,
                                   boolean isActive,
                                   String deactivationReason,
                                   LocalDateTime deactivationDate,
                                   boolean isVerified,
                                   String googleId,
                                   String recoveryEmail,
                                   String recoveryPhoneNumber,
                                   Subscription subscription,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
}
