package com.bbsoft.spring_backend_starter;

import com.bbsoft.spring_backend_starter.service.InitializationService;
import com.bbsoft.spring_backend_starter.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "prod"})
@RequiredArgsConstructor
@Slf4j
public class SpringBackendInitializer implements ApplicationRunner {

    private final TranslationService translationService;
    private final InitializationService initializationService;
    @Value("${server.port}")
    private String port;

    @Override
    public void run(ApplicationArguments args) {
        translationService.loadTranslations();
        initializationService.createDefaultUserIfNotExists();
        var applicationStartedText = """
            \n
            --------------------------------------------------------------------------
            |     SpringBackend has been initialized and running on port: {port}     |
            --------------------------------------------------------------------------
            """;
        applicationStartedText = applicationStartedText.replace("{port}", port);
        log.info(applicationStartedText);
    }
}

