package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.model.enums.UserRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

//esas classim budur
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

    @Column(nullable = false)
    String phoneNumber;

    @Column(nullable = false)
    String password;

    @Column(nullable = false)
    String workplace;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    UserRole role=UserRole.USER; // ADMIN, GUEST_USER, USER

    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime updatedAt;

    @Column(nullable = false)//hesabin tesdiqlenib tesdiqlenmediyini bildirir (gmailden)
    boolean isVerified;

    String googleId;

    String imgUrl;

    boolean acceptTermsOfUse;//gizllik siyaseti qebul edirsen ya yox

}
