package com.bbsoft.spring_backend_starter.config.providers;

import org.springframework.stereotype.Component;

@Component
public class RandomProvider {

    public double random() {
        return Math.random();
    }
}
