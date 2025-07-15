package org.example.dataprotal.mapper;

import org.example.dataprotal.dto.response.ProfileResponse;
import org.example.dataprotal.dto.response.UserResponseForAdmin;
import org.example.dataprotal.model.user.User;

public class UserMapper {
    public static ProfileResponse userToProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getProfileImage(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getWorkplace(),
                user.getPosition());
    }

    public static UserResponseForAdmin userToUserResponseForAdmin(User user) {
        return new UserResponseForAdmin(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getWorkplace(),
                user.getPosition(),
                user.getProfileImage(),
                user.getRole(),
                user.isActive(),
                user.getDeactivateReason(),
                user.getDeactivateTime(),
                user.isVerified(),
                user.getGoogleId(),
                user.getRecoveryEmail(),
                user.getRecoveryPhoneNumber(),
                user.getSubscriptionId(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
