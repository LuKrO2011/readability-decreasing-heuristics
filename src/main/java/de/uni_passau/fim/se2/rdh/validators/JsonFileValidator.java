package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validates a JSON file.
 */
public class JsonFileValidator implements ConstraintValidator<JsonFile, String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return value.endsWith(".json");
    }
}
