// src/main/java/com/ECommerce/validation/UsernameValidator.java
package com.ECommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_]{4,20}$";

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return username.matches(USERNAME_PATTERN);
    }
}