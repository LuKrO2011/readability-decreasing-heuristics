package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.Logging;
import de.uni_passau.fim.se2.rdh.util.NumberIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.SpoonException;
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
     * The number iterator provides a way to get a unique number for each method.
     */
    private final NumberIterator numberIterator = new NumberIterator();

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

        // Get all local variables
        List<CtMethod<Integer>> methods =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() == 0) {
            LOG.info("No methods found");
            return;
        }

        // Rename all methods to m0 ... mN
        for (CtMethod<Integer> method : methods) {
            if (!probabilities.shouldRenameMethod()) {
                continue;
            }

            rename(method);
        }
    }

    /**
     * Renames the given method to the next unique name given by the number iterator.
     *
     * @param method the method to rename
     */
    public void rename(final CtMethod<?> method) {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        try {
            refactoring.setTarget(method);
            String newName = "m" + numberIterator.getNext();
            refactoring.setNewName(newName);
            refactoring.refactor();
        } catch (SpoonException e) {
            Logging.logRefactoringFailed(LOG, "Could not rename method " + method.getSimpleName(), e);
        }
    }

}
