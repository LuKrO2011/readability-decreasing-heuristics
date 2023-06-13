package de.uni_passau.fim.se2.rdm.refactorings;

import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;

import java.util.ArrayList;
import java.util.List;

public class CtInlineMethodRefactoring implements CtRefactoring {

    // The target is a method invocation
    private CtInvocation<?> invocation;

    // TODO: Add target to constructor?

    public CtInvocation<?> getTarget() {
        return invocation;
    }

    public void setTarget(CtInvocation<?> invocation) {
        this.invocation = invocation;
    }

    @Override
    public void refactor() {

        // Get the declaration of the method
        CtExecutable<?> declaration = invocation.getExecutable().getDeclaration();

        // Method not found
        if (declaration == null) {
            return;
        }

        // Get the statements of the method
        // TODO: NOT WORKING: Declaration wrong!
        List<CtStatement> statements = declaration.getBody().getStatements();

        // Clone the statements
        List<CtStatement> clonedStatements = new ArrayList<>();
        for (CtStatement s : statements) {
            clonedStatements.add(s.clone());
        }

        // Make the inlining
        int size = clonedStatements.size();
        for (int i = 0; i < size - 1; i++) {
            ((CtStatement) invocation.getParent()).insertBefore(clonedStatements.get(i));
        }

        CtExpression<?> returnExpression = ((CtReturn<?>) clonedStatements.get(size - 1)).getReturnedExpression();
        invocation.replace(returnExpression);
    }
}
