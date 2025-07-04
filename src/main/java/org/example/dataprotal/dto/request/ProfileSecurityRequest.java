package org.example.dataprotal.dto.request;

public record ProfileSecurityRequest(String oldPassword, String newPassword, String recoveryEmail, String recoveryPhoneNumber) {
}
