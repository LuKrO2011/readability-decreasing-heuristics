package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import de.uni_passau.fim.se2.rdh.validators.Probability;
import lombok.Data;

/**
 * Represents the data of a new name prediction.
 */
@Data
public class PredictionData {
    private String name;

    @Probability
    private double probability;
}
