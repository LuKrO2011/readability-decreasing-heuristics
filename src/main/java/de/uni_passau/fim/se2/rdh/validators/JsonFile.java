package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates a JSON file.
 */
@Documented
@Constraint(validatedBy = JsonFileValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFile {
    /**
     * Returns the error message.
     *
     * @return the error message
     */
    String message() default "Invalid file format. File must end with .json";

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
