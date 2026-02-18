package com.bbsoft.spring_backend_starter.constant;

public enum Role {

    AUTHENTICATED_USER,
    ADMIN,
    SIMPLE_USER;

    public String getName() {
        return this.name();
    }
}
