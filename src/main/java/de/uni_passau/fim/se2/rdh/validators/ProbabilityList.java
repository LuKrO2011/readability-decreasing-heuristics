package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validates a list of probabilities.
 */
@Documented
@Constraint(validatedBy = {ProbabilityListValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProbabilityList {
    String message() default "Invalid probability list: must not be null, empty or contain values outside the range [0.0, 1.0]";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
