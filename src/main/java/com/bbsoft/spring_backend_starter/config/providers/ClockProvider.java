package com.bbsoft.spring_backend_starter.config.providers;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ClockProvider {

    @Bean
    public Clock getClock() {
        return Clock.systemUTC();
    }
}
