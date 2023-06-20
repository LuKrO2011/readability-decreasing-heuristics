package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Inlines methods.
 * <p>
 * This class is used to inline methods. The probability for this refactoring to be performed on a method is defined in
 * the {@link RdcProbabilities} class.
 * </p>
 */
public class MethodInliner extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodRenamer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public MethodInliner(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        inline();
    }

    /**
     * Inlines methods.
     */
    private void inline() {
        CtInlineMethodRefactoring refactoring = new CtInlineMethodRefactoring();

        // Get all method invocations
        List<CtMethod<?>> methods = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() == 0) {
            LOG.warn("No methods found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (CtMethod<?> ctMethod : methods) {
            if (!probabilities.shouldInlineMethod()) {
                continue;
            }

            try {
                refactoring.setTarget(ctMethod);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not inline method", e);
            }
        }
    }
}
