package de.uni_passau.fim.se2.rdh.refactorings.experimental.inline;

import de.uni_passau.fim.se2.rdh.refactorings.RefactoringsUtils;
import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
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

        final List<CtInvocation<?>> invocations = RefactoringsUtils.getMethodInvocations(target);

        // Clone statements
        List<CtStatement> statements = target.getBody().getStatements();
        List<CtStatement> clonedStatements = new ArrayList<>();
        for (CtStatement s : statements) {
            clonedStatements.add(s.clone());
        }

        // Make the inlining
        for (CtStatement invocation : invocations) {
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
                    if (statement instanceof CtReturnImpl<?> returnStatement) {
                        CtExpression<?> returnedExpression = returnStatement.getReturnedExpression();
                        if (returnedExpression != null) {
                            if (invocation instanceof CtLocalVariable localVariable) {
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
