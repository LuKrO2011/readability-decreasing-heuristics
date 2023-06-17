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
public class MethodInliner extends AstModifier {

    private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodInliner(SpoonAPI spoon, RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyModification() {
        inline();
    }

    /**
     * Inlines methods.
     */
    public void inline() {
        CtInlineMethodRefactoring refactoring = new CtInlineMethodRefactoring();

        // Get all method invocations
        List<CtMethod<?>> methods = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() <= 0) {
            log.warn("No methods found");
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < methods.size(); i++) {
            if (!probabilities.shouldInlineMethod()) {
                continue;
            }

            CtMethod<?> method = methods.get(i);
            try {
                refactoring.setTarget(method);
                refactoring.refactor();
            } catch (RefactoringException e) {
                log.error("Could not inline method", e);
            }
        }
    }
}
