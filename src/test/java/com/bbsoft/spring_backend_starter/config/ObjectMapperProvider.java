package com.bbsoft.spring_backend_starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperProvider {

    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
