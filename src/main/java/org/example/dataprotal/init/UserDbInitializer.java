package org.example.dataprotal.init;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.dataprotal.enums.Role;
import org.example.dataprotal.model.user.Subscription;
import org.example.dataprotal.model.user.User;
import org.example.dataprotal.repository.user.SubscriptionRepository;
import org.example.dataprotal.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class UserDbInitializer {

    private final DataSource userDataSource;

    private final UserRepository userRepository;

    private final SubscriptionRepository subscriptionRepository;

    private final PasswordEncoder passwordEncoder;

    private final Environment environment;

    public UserDbInitializer(@Qualifier("firstDataSource") DataSource userDataSource,
                             UserRepository userRepository,
                             SubscriptionRepository subscriptionRepository,
                             PasswordEncoder passwordEncoder,
                             Environment environment) {
        this.userDataSource = userDataSource;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @PostConstruct
    public void init() {
        runInitFile();

        createAdmin();

        createDefaultSubscriptions();
    }

    private void createDefaultSubscriptions() {
        try {
            int defaultPackCount = Integer.parseInt(environment.getProperty("packs.default-count", "0"));
            for (int i = 1; i <= defaultPackCount; i++) {
                String baseKey = "packs." + i;

                String name = environment.getProperty(baseKey + ".name");
                String advantagesStr = environment.getProperty(baseKey + ".advantages");
                String priceStr = environment.getProperty(baseKey + ".currency-monthly-yearly-price");

                assert advantagesStr != null;
                assert priceStr != null;
                assert name != null;

                List<String> advantages = Arrays.stream(advantagesStr.split("\\."))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();

                Map<String, List<BigDecimal>> priceMap = new HashMap<>();
                String[] priceEntries = priceStr.split("-");
                for (String priceEntry : priceEntries) {
                    String[] parts = priceEntry.split("\\|");
                    priceMap.put(parts[0].toUpperCase(),
                            List.of(new BigDecimal(parts[1]),
                                    new BigDecimal(parts[2])));
                }

                Subscription subscription = Subscription.builder()
                        .name(name)
                        .advantages(advantages)
                        .cureencyMonthlyAndYearlyPriceMap(priceMap)
                        .build();

                log.info("Subscription created successfully : {}", subscriptionRepository.save(subscription));
            }
        } catch (Exception e) {
            log.error("Failed to create subscriptions : {}", e.getMessage(), e);
        }
    }

    private void createAdmin() {
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

    private void runInitFile() {
        try (Connection conn = userDataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("db/user/init.sql"));
            log.info("User DB init.sql applied successfully.");
        } catch (Exception e) {
            log.error("Failed to apply init.sql to User DB : {}", e.getMessage());
        }
    }
}
