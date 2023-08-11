package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import spoon.SpoonAPI;

/**
 * Abstract class for ways to rename methods.
 */
public abstract class MethodRenamer extends AbstractModification {

    /**
     * Creates a new MethodRenamer with the given {@link SpoonAPI} and {@link RdcProbabilities}.
     *
     * @param spoon         The {@link SpoonAPI}
     * @param probabilities The {@link RdcProbabilities}
     */
    public MethodRenamer(final SpoonAPI spoon,
                         final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }
}
