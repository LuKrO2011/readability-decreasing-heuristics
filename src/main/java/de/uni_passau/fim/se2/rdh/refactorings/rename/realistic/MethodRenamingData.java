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

    /**
     * Returns the predictions sorted by the given name selection mode.
     *
     * @param mode the name selection mode
     * @return the sorted predictions
     */
    public List<PredictionData> getPredictions(final NameSelectionMode mode) {
        sortPredictions(mode);
        return Collections.unmodifiableList(predictions);
    }

    public void setPredictions(final List<PredictionData> predictions) {
        this.predictions = new ArrayList<>(predictions);
    }

    /**
     * Sorts the predictions by the given name selection mode.
     *
     * @param mode the name selection mode
     */
    private void sortPredictions(final NameSelectionMode mode) {
        switch (mode) {
            case LONGEST -> {
                predictions.sort(Comparator.comparingInt(p -> p.getName().length()));
                Collections.reverse(predictions);
            }
            case QUALITY -> predictions.sort(Comparator.comparingDouble(PredictionData::getProbability).reversed());
            default -> throw new IllegalArgumentException("Unknown name selection mode: " + mode);
        }
    }
}
