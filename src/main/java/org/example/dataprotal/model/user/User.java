package org.example.dataprotal.model.user;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.dataprotal.model.enums.Roles;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @Column()
    String phoneNumber;

    @Column(nullable = false)
    String password;

    @Column()
    String workplace;

@JdbcTypeCode(SqlTypes.JSON)
@Column(nullable = false)
Set<Roles> roles=new HashSet<>();
    @CreationTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDateTime updatedAt;

    @Column(nullable = false)//hesabin tesdiqlenib tesdiqlenmediyini bildirir (gmailden)
    boolean isVerified;

    String googleId;

    String profileImage;

    boolean acceptTermsOfUse;//gizllik siyaseti qebul edirsen ya yox

}
