package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ProbabilityListValidator.class})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProbabilityList {
    String message() default "Invalid probability list";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
