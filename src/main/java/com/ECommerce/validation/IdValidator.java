package com.ECommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<ValidId, Long> {

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext context) {
        return id != null && id > 0;
    }
}