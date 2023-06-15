package de.uni_passau.fim.se2.rdm.refactorings;

import spoon.SpoonAPI;
import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtInvocation;
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
    private CtMethod<?> target;

    // TODO: Does not consider variable name clashes

    public CtMethod<?> getTarget() {
        return target;
    }

    public void setTarget(CtMethod<?> target) {
        this.target = target;
    }

    @Override
    public void refactor() {

        ExecutableReferenceFilter execRefFilter = new ExecutableReferenceFilter();
        final List<CtInvocation<?>> invocations = new ArrayList<>();

        // Add own executable
        execRefFilter.addExecutable(target.getReference().getExecutableDeclaration());

        // Add all executables with same signature
        target.getFactory().getModel().filterChildren(execRefFilter).forEach((CtConsumer<CtExecutableReference<?>>) t -> {
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
            CtInvocation<?> invocation = invocations.get(i);
            CtStatement parent = (CtStatement) invocation.getParent();
            if(!(parent instanceof CtExecutable)){
                for (int j = 0; j < clonedStatements.size() - 1; j++){
                    invocation.insertBefore(clonedStatements.get(j));
                }
                invocation.replace(clonedStatements.get(clonedStatements.size() - 1));
            }
        }
    }
}
