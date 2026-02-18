package com.bbsoft.spring_backend_starter.controller.validation;

import com.bbsoft.spring_backend_starter.controller.validation.validators.UsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Username {

    String message() default "Invalid username.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
