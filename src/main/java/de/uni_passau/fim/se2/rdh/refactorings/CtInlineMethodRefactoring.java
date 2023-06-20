package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;

import java.util.ArrayList;
import java.util.List;
/*
 TODO: Handle static methods
 TODO: Handle methods with multiple return statements
 TODO: Handle variable naming conflicts
*/

/**
 * This class implements the refactoring for inlining a method.
 */
public class CtInlineMethodRefactoring implements CtRefactoring {

    /**
     * The target is a method invocation that should be inlined.
     */
    private CtMethod<?> target;


    /**
     * This method performs the inlining of the method.
     */
    @Override
    public void refactor() {

        ExecutableReferenceFilter execRefFilter = new ExecutableReferenceFilter();
        final List<CtInvocation<?>> invocations = new ArrayList<>();

        // Add own executable
        execRefFilter.addExecutable(target.getReference().getExecutableDeclaration());

        // Add all executables with same signature
        target.getFactory().getModel().filterChildren(execRefFilter).forEach(
                (CtConsumer<CtExecutableReference<?>>) t -> {
                    CtElement parent = t.getParent();
                    if (parent instanceof CtInvocation<?>) {
                        invocations.add((CtInvocation<?>) parent);
                    }
                });

        // Clone statements
        List<CtStatement> statements = target.getBody().getStatements();
        List<CtStatement> clonedStatements = new ArrayList<>();
        for (CtStatement s : statements) {
            clonedStatements.add(s.clone());
        }

        // Make the inlining
        for (int i = 0; i < invocations.size(); i++) {
            CtStatement invocation = invocations.get(i);
            CtStatement parent = (CtStatement) invocation.getParent();

            // Go upwards in tree until we find a block to which we can append statements
            while (!(parent instanceof CtBlockImpl)) {
                invocation = parent;
                parent = (CtStatement) parent.getParent();
            }

            // Append statements
            if (!(parent instanceof CtExecutable)) {
                for (CtStatement statement : clonedStatements) {

                    // Replace right side of assignment with first found return expression
                    if (statement instanceof CtReturnImpl<?>) {
                        CtReturnImpl<?> returnStatement = (CtReturnImpl<?>) statement;
                        CtExpression<?> returnedExpression = returnStatement.getReturnedExpression();
                        if (returnedExpression != null) {
                            if (invocation instanceof CtLocalVariable) {
                                CtLocalVariable localVariable = (CtLocalVariable) invocation;
                                localVariable.setAssignment(returnedExpression);
                                break;
                            }
                        }
                    }

                    // Otherwise, insert all statements before invocation
                    invocation.insertBefore(statement);
                }
            }
        }
    }

    /**
     * This method returns the target of the refactoring.
     *
     * @return The target of the refactoring.
     */
    public CtMethod<?> getTarget() {
        return target;
    }

    /**
     * This method sets the target of the refactoring.
     *
     * @param target The target of the refactoring.
     */
    public void setTarget(final CtMethod<?> target) {
        this.target = target;
    }
}
