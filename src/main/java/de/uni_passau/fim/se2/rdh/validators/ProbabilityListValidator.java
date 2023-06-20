package de.uni_passau.fim.se2.rdh.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.util.List;

public class ProbabilityListValidator implements ConstraintValidator<ProbabilityList, List<Double>> {
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
