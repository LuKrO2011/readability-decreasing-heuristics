package de.uni_passau.fim.se2.rdm;

import de.uni_passau.fim.se2.rdm.refactorings.CtRenameFieldRefactoring;
import de.uni_passau.fim.se2.rdm.refactorings.CtInlineMethodRefactoring;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class MethodInliner {

    private final SpoonAPI spoon;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodInliner(SpoonAPI spoon) {
        this.spoon = spoon;
    }

    public void inline() {
        CtInlineMethodRefactoring refactoring = new CtInlineMethodRefactoring();

        // Get all method invocations
        List<CtInvocation<?>> methodInvocations = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtInvocation.class));

        if (methodInvocations.size() <= 0) {
            // log....
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < methodInvocations.size(); i++) {
            CtInvocation<?> invocation = methodInvocations.get(i);
            try {
                refactoring.setTarget(invocation);
                refactoring.refactor();
            } catch (RefactoringException e) {
                // log...
            }
        }
    }
}
