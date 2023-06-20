package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Renames local variables.
 * <p>
 * This class is used to rename local variables to v0 ... vN. The probability for this refactoring to be performed
 * on a method is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class LocalVariableRenamer extends Refactoring {

    /**
     * The logger of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodRenamer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public LocalVariableRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities) {
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
     * Renames local variables to v0 ... vN.
     */
    private void rename() {
        CtRenameLocalVariableRefactoring refactoring = new CtRenameLocalVariableRefactoring();

        // Get all local variables
        List<CtLocalVariable<Integer>> localVariables =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtLocalVariable.class));

        if (localVariables.size() == 0) {
            LOG.warn("No local variables found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < localVariables.size(); i++) {
            if (probabilities.shouldRenameVariable()) {
                CtLocalVariable<Integer> localVariable = localVariables.get(i);
                try {
                    refactoring.setTarget(localVariable);
                    refactoring.setNewName("v" + i);
                    refactoring.refactor();
                } catch (RefactoringException e) {
                    LOG.error("Could not rename local variable", e);
                }
            }
        }

    }


}
