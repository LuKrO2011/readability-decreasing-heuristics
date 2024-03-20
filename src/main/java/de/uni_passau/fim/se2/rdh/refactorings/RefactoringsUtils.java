package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;

import java.util.ArrayList;
import java.util.List;

public final class RefactoringsUtils {

    private RefactoringsUtils() {
    }

    /**
     * This method returns all invocations of the given method (within the whole model?).
     *
     * @param target The method whose invocations should be returned.
     * @return A list of all invocations of the given method.
     */
    public static List<CtInvocation<?>> getMethodInvocations(final CtMethod<?> target) {
        ExecutableReferenceFilter execRefFilter = new ExecutableReferenceFilter();
        final List<CtInvocation<?>> invocations = new ArrayList<>();

        /*
         * Use filter instead for all methods with same signature? @see CtRenameLocalVariableRefactoring,
         *  @see VariableReferenceFunction. Consider super/subclasses?
         */
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
        return invocations;
    }
}
