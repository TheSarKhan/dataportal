package org.example.dataprotal.init;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.enums.Role;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Slf4j
@Component
public class UserDbInitializer {

    private final DataSource userDataSource;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserDbInitializer(@Qualifier("firstDataSource")DataSource userDataSource, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userDataSource = userDataSource;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        try (Connection conn = userDataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/user/init.sql"));
            log.info("User DB init.sql applied successfully.");
        } catch (Exception e) {
            log.error("Failed to apply init.sql to User DB : {}", e.getMessage());
        }

        try {
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("Admin")
                    .email("admin1234@gmail.com")
                    .password(passwordEncoder.encode("Admin123"))
                    .role(Role.ADMIN)
                    .isVerified(true)
                    .phoneNumber("05555555555")
                    .build();

            log.info("User created successfully : {}", userRepository.save(admin));
        } catch (Exception e) {
            log.error("Failed to create user : {}", e.getMessage());
        }
    }

}
