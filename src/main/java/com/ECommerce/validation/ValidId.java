package com.ECommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {

    String message() default "ID must be a positive number greater than zero";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}