package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * Validates a list of probabilities. The list is valid if it is not null, not empty and all probabilities are
 * between 0.0 and 1.0.
 */
public class ProbabilityListValidator implements ConstraintValidator<ProbabilityList, List<Double>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(List<Double> probabilities, ConstraintValidatorContext context) {
        if (probabilities == null || probabilities.isEmpty()) {
            return false;
        }

        for (Double probability : probabilities) {
            if (probability == null || probability < 0.0 || probability > 1.0) {
                return false; // Invalid probability value
            }
        }

        return true; // All probabilities are valid
    }
}
