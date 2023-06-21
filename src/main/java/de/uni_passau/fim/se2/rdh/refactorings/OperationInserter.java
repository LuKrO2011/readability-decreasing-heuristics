package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class OperationInserter extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OperationInserter.class);


    public OperationInserter(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        insertAdd0();
    }


    private void insertAdd0() {
        CtAdd0 refactoring = new CtAdd0();

        // Get expression nodes
        List<CtBinaryOperator<?>> binaryOperations = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtBinaryOperator.class));

        if (binaryOperations.size() == 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No binary operations found");
            }
            return;
        }

        for (CtBinaryOperator<?> ctBinaryOperator : binaryOperations) {
            if (!probabilities.shouldInsertOperation()) {
                continue;
            }

            try {
                refactoring.setTarget(ctBinaryOperator);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not inline method", e);
            }
        }
    }
}
