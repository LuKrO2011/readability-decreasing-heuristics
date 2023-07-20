package de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * This class implements the refactoring for inserting an operation. The operation should not change the semantics of
 * the program.
 */
public class OperationInserter extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(OperationInserter.class);


    /**
     * Creates a new instance of this class.
     *
     * @param spoon         The SpoonAPI instance to use.
     * @param probabilities The probabilities to use.
     */
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

    /**
     * This method inserts add0 on random nodes.
     */
    private void insertAdd0() {
        CtAdd0 refactoring = new CtAdd0();

        // Get expression nodes
        List<CtBinaryOperator<?>> binaryOperations =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtBinaryOperator.class));

        if (binaryOperations.size() == 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No binary operations found");
            }
            return;
        }

        // Insert add0 on random nodes
        for (CtBinaryOperator<?> ctBinaryOperator : binaryOperations) {
            if (!probabilities.shouldInsertOperation()) {
                continue;
            }

            try {
                refactoring.setTarget(ctBinaryOperator);
                refactoring.refactor();
            } catch (RefactoringException e) {
                Logging.logRefactoringFailed(LOG, "Could not insert +0", e);
            }
        }
    }
}
