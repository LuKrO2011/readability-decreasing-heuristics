package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.CtInlineMethodRefactoring;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class MethodInliner {

    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodInliner(SpoonAPI spoon, RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
    }

    public void inline() {
        CtInlineMethodRefactoring refactoring = new CtInlineMethodRefactoring();

        // Get all method invocations
        List<CtMethod<?>> methods = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() <= 0) {
            // log....
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
                // log...
            }
        }
    }
}
