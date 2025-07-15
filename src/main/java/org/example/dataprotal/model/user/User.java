package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.enums.Language;
import org.example.dataprotal.enums.Role;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String firstName;

    @Column(nullable = false)
    String lastName;

    @Column(nullable = false, unique = true)
    String email;

    String phoneNumber;

    @Column(nullable = false)
    String password;

    String workplace;

    String position;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    Role role;

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime updatedAt;

    boolean isVerified;

    String googleId;

    String profileImage;

    boolean acceptTermsOfUse;

    boolean isActive;

    String deactivateReason;

    LocalDateTime deactivateTime;

    String recoveryPhoneNumber;

    String recoveryEmail;

    @Enumerated(EnumType.STRING)
    Language language;

    Long subscriptionId;

    boolean isSubscriptionMonthly;

    LocalDateTime nextPaymentTime;

    @PrePersist
    public void prePersist() {
        role = Role.USER;
        subscriptionId = 1L;
        language = Language.EN;
        isActive = true;
    }
}
