package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the data of a method renaming.
 */
public final class MethodRenamingData {
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
        return predictions.stream()
                .max(Comparator.comparingInt(p -> p.getName().length()))
                .orElseThrow(() -> new RuntimeException("No prediction found"));
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(final String originalName) {
        this.originalName = originalName;
    }

    public List<PredictionData> getPredictions() {
        return Collections.unmodifiableList(predictions);
    }

    public void setPredictions(final List<PredictionData> predictions) {
        this.predictions = new ArrayList<>(predictions);
    }
}
