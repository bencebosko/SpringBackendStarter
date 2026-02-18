package com.bbsoft.spring_backend_starter.config;

import com.bbsoft.spring_backend_starter.service.helper.ObjectMerger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringBackendConfig {

    @Bean
    public ObjectMerger objectMerger(ObjectMapper objectMapper) {
        return new ObjectMerger(objectMapper);
    }
}
