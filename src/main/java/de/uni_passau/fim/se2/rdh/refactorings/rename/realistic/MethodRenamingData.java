package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import lombok.Data;

import java.util.List;

/**
 * Represents the data of a method renaming.
 */
@Data
public class MethodRenamingData {
    private String originalName;
    private List<PredictionData> predictions;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "MethodRenamingData{" + "originalName='" + originalName + '\'' + ", highestPrediction ='"
            + predictions.get(0).getName() + '\'' + '}';
    }
}
