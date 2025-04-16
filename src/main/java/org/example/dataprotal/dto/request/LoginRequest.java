package org.example.dataprotal.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

// Avtorizasiya (giriş) üçün istifadə olunur
//✅ İstifadəçinin email və parolunu saxlayır
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@(gmail\\.com|yahoo\\.com|email\\.ru|.*\\.edu)$",
            message = "Email must be a valid Gmail, Yahoo, email.ru, or educational domain"
    )
     String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d_!@#$%^&*()+=-]{8,}$",
            message = "Password must be at least 8 characters long, contain at least one letter and one digit"
    )
     String password;
     String recaptchaToken; // sadece gerektiğinde gönderilir

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AboutDto {

        @NotBlank(message = "tittle must not be empty")
        private String title;

        @Column(nullable = false)
        private String description;

        @Column(name="image_url")
        private String imageUrl;

    }
}