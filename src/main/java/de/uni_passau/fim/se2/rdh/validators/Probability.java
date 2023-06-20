package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.lang.annotation.*;

@Constraint(validatedBy = {})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DecimalMin("0.0")
@DecimalMax("1.0")
public @interface Probability {
    String message() default "Invalid probability value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}