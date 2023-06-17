package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.SpoonAPI;

/**
 * Represents a refactoring that can be applied to the ast.
 * <p>
 * The probability for a refactoring to be performed on a method is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public abstract class Refactoring {

    protected final SpoonAPI spoon;
    protected final RdcProbabilities probabilities;

    public Refactoring(SpoonAPI spoon, RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
    }

    /**
     * Applies a modification to the AST.
     */
    public abstract void apply();
}
