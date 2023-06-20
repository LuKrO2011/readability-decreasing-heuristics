package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.SpoonAPI;

/**
 * Represents a refactoring that can be applied to the ast.
 * <p>
 * The probability for a refactoring to be performed on a method is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public abstract class AbstractModification {

    /**
     * The {@link SpoonAPI} to use.
     * <p>
     * The attribute is protected to allow access from subclasses without need of super.getter().
     * </p>
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final SpoonAPI spoon;

    /**
     * The {@link RdcProbabilities} to use.
     * <p>
     * The attribute is protected to allow access from subclasses without need of super.getter().
     * </p>
     */
    @SuppressWarnings("checkstyle:VisibilityModifier")
    protected final RdcProbabilities probabilities;

    /**
     * Creates a new {@link AbstractModification} with the given {@link SpoonAPI} and {@link RdcProbabilities}.
     *
     * @param spoon         The {@link SpoonAPI}
     * @param probabilities The {@link RdcProbabilities}
     */
    public AbstractModification(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = new RdcProbabilities(probabilities);
    }

    /**
     * Applies a modification to the AST.
     */
    public abstract void apply();
}
