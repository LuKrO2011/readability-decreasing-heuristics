package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import lombok.Data;

import java.util.Comparator;
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

    /**
     * Returns the longest predicted name regardless of the prediction quality.
     *
     * @return the longest predicted name
     */
    public PredictionData getLongestPrediction() {
        return predictions.stream().max(Comparator.comparingInt(p -> p.getName().length()))
                .orElseThrow(() -> new RuntimeException("No prediction found"));
    }
}
