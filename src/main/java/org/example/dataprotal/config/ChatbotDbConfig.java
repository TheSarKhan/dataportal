package org.example.dataprotal.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "org.example.dataprotal.repository.chatbot",
        entityManagerFactoryRef = "fourthEntityManagerFactory",
        transactionManagerRef = "fourthTransactionManager"
)

public class ChatbotDbConfig {

    @Value("${spring.datasource.fourth.url}")
    private String fourthDbUrl;

    @Value("${spring.datasource.fourth.username}")
    private String fourthDbUsername;

    @Value("${spring.datasource.fourth.password}")
    private String fourthDbPassword;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String fourthDbDdlAuto;

    @Bean(name = "fourthDataSource")
    public DataSource fourthDataSource() {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        builder.url(fourthDbUrl);
        builder.username(fourthDbUsername);
        builder.password(fourthDbPassword);
        return builder.build();
    }

    @Bean(name = "fourthEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean fourthEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(fourthDataSource())
                .packages("org.example.dataprotal.model.chatbot")
                .persistenceUnit("fourth")
                .properties(hibernateProperties())
                .build();
    }
    @Bean(name = "fourthTransactionManager")
    public PlatformTransactionManager fourthTransactionManager(
            @Qualifier("fourthEntityManagerFactory") EntityManagerFactory fourthEntityManagerFactory) {
        return new JpaTransactionManager(fourthEntityManagerFactory);
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", fourthDbDdlAuto);
        return properties;
    }

}


