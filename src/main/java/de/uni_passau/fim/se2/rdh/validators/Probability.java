package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validates a probability value.
 */
@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DecimalMin("0.0")
@DecimalMax("1.0")
public @interface Probability {

    /**
     * Returns the error message.
     *
     * @return the error message
     */
    String message() default "Invalid probability value: must be between 0.0 and 1.0";

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
