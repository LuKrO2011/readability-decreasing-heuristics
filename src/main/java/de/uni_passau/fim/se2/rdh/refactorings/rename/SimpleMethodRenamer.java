package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * Renames methods to m0 ... mN.
 * <p>
 * This class is used to rename methods to m0 ... mN. The probability for this refactoring to be performed on a method
 * is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class SimpleMethodRenamer extends MethodRenamer {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SimpleMethodRenamer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public SimpleMethodRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities) {
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
     * Renames methods to m0 ... mN.
     */
    private void rename() {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        // Get all local variables
        List<CtMethod<Integer>> methods =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() == 0) {
            LOG.warn("No methods found");
            return;
        }

        // Rename all methods to m0 ... mN
        for (int i = 0; i < methods.size(); i++) {
            if (!probabilities.shouldRenameMethod()) {
                continue;
            }

            CtMethod<Integer> method = methods.get(i);
            try {
                refactoring.setTarget(method);
                refactoring.setNewName("m" + i);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not rename method", e);
            }
        }
    }

}
