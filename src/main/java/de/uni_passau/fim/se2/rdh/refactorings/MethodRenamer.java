package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.CtRenameMethodRefactoring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;

/**
 * Renames methods to m0 ... mN.
 * <p>
 * This class is used to rename methods to m0 ... mN. The probability for this refactoring to be performed on a method
 * is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class MethodRenamer extends AstModifier {
    private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodRenamer(SpoonAPI spoon, RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyModification() {
        rename();
    }

    /**
     * Renames methods to m0 ... mN.
     */
    public void rename() {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        // Get all local variables
        List<CtMethod<Integer>> methods = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() == 0) {
            log.warn("No methods found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
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
                log.error("Could not rename method", e);
            }
        }
    }

}
