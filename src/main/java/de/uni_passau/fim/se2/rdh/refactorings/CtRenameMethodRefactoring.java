package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// TODO: Add checks for valid method names

/**
 * This class implements the refactoring for renaming a method.
 */
public class CtRenameMethodRefactoring extends AbstractRenameRefactoring<CtMethod<?>> {

    private static final Pattern validVariableNameRE = javaIdentifierRE;

    public CtRenameMethodRefactoring() {
        super(validVariableNameRE);
    }

    /**
     * This method refactors the method name and all invocations of the method.
     * It does not check for valid method names.
     *
     * @see AbstractRenameRefactoring#refactorNoCheck()
     */
    @Override
    protected void refactorNoCheck() {
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

        // Change name of method
        target.setSimpleName(newName);

        // Change name of all invocations
        for (CtInvocation<?> invocation : invocations) {
            invocation.getExecutable().setSimpleName(newName);
        }

    }
}
