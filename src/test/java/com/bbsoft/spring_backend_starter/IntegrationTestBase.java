package com.bbsoft.spring_backend_starter;

import com.bbsoft.spring_backend_starter.repository.MailRepository;
import com.bbsoft.spring_backend_starter.repository.UserAuthRepository;
import com.bbsoft.spring_backend_starter.repository.UserRepository;
import com.bbsoft.spring_backend_starter.repository.UserRoleRepository;
import com.bbsoft.spring_backend_starter.repository.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = SpringBackendApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@RequiredArgsConstructor
public class IntegrationTestBase {

    private static final String POSTGRES_IMAGE = "postgres:17.4-alpine";
    private static final String MAILHOG_IMAGE = "mailhog/mailhog:v1.0.1";
    private static final int MAILHOG_EXPOSED_PORT = 1025;

    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE)).withReuse(true);
    private static final GenericContainer<?> MAILHOG_CONTAINER = new GenericContainer<>(DockerImageName.parse(MAILHOG_IMAGE))
        .withExposedPorts(MAILHOG_EXPOSED_PORT)
        .withReuse(true);

    protected final UserRepository userRepository;
    protected final UserRoleRepository userRoleRepository;
    protected final UserAuthRepository userAuthRepository;
    protected final UserSettingsRepository userSettingsRepository;
    protected final MailRepository mailRepository;

    @DynamicPropertySource
    static void setConfigProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.mail.host", MAILHOG_CONTAINER::getHost);
        registry.add("spring.mail.port", () -> MAILHOG_CONTAINER.getMappedPort(MAILHOG_EXPOSED_PORT));
        registry.add("spring.mail.protocol", () -> "smtp");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> false);
        registry.add("spring.mail.properties.mail.smtp.starttls.enable", () -> false);
    }

    @BeforeAll
    static void startContainers() {
        POSTGRES_CONTAINER.start();
        MAILHOG_CONTAINER.start();
    }

    @BeforeEach
    void cleanDatabase() {
        userRepository.deleteAll();
        userRoleRepository.deleteAll();
        userAuthRepository.deleteAll();
        userSettingsRepository.deleteAll();
        mailRepository.deleteAll();
    }

    @AfterAll
    static void stopContainers() {
        POSTGRES_CONTAINER.stop();
        POSTGRES_CONTAINER.close();
        MAILHOG_CONTAINER.stop();
        MAILHOG_CONTAINER.close();
    }
}
