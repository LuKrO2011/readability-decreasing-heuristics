package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates a list of probabilities.
 */
@Documented
@Constraint(validatedBy = {ProbabilityListValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProbabilityList {
    /**
     * Returns the error message.
     *
     * @return the error message
     */
    String message() default "Invalid probability list: must not be null, empty or contain values outside the range "
            + "[0.0, 1.0]";

    /**
     * Returns the groups.
     *
     * @return the groups
     */
    Class<?>[] groups() default {};

    /**
     * Returns the payload.
     *
     * @return the payload
     */
    Class<? extends Payload>[] payload() default {};
}
