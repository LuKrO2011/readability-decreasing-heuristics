package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.util.Logging;
import de.uni_passau.fim.se2.rdh.util.NumberIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Renames global variables.
 * <p>
 * This class is used to rename global variables to f0 ... fN. The probability for this refactoring to be performed on a
 * method is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class FieldRenamer extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FieldRenamer.class);

    /**
     * The number iterator provides a way to get a unique number for each method.
     */
    private final NumberIterator numberIterator = new NumberIterator();

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public FieldRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        rename();
    }

    /**
     * Renames global variables.
     */
    private void rename() {
        CtRenameFieldRefactoring refactoring = new CtRenameFieldRefactoring();

        // Get all global variables
        List<CtField<?>> globalVariables =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class));

        if (globalVariables.size() == 0) {
            LOG.info("No global variables found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (CtField<?> variable : globalVariables) {
            if (!probabilities.shouldRenameField()) {
                continue;
            }

            try {
                refactoring.setTarget(variable);
                refactoring.setNewName("f" + numberIterator.getNext());
                refactoring.refactor();
            } catch (SpoonException e) {
                Logging.logRefactoringFailed(LOG, "Could not rename global variable "
                        + variable.getSimpleName(), e);
            }
        }

    }


}
