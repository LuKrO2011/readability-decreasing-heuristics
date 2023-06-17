package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Renames global variables.
 * <p>
 * This class is used to rename global variables to f0 ... fN. The probability for this refactoring to be performed
 * on a method is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class FieldRenamer extends Refactoring {

    private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public FieldRenamer(SpoonAPI spoon, RdcProbabilities probabilities) {
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
        List<CtField<Integer>> globalVariables = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class));

        if (globalVariables.size() == 0) {
            log.warn("No global variables found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < globalVariables.size(); i++) {
            if (!probabilities.shouldRenameField()) {
                continue;
            }

            CtField<Integer> globalVariable = globalVariables.get(i);
            try {
                refactoring.setTarget(globalVariable);
                refactoring.setNewName("f" + i);
                refactoring.refactor();
            } catch (RefactoringException e) {
                log.error("Could not rename global variable", e);
            }
        }

    }


}
