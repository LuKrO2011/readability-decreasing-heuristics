package de.uni_passau.fim.se2.rdh.refactorings.rename;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.*;
import spoon.support.reflect.declaration.CtExecutableImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// TODO: Add checks for valid method names
// TODO: Use AllMethodsSameSignatureFunction?

/**
 * This class implements the refactoring for renaming a method.
 */
public class CtRenameMethodRefactoring extends AbstractRenameRefactoring<CtMethod<?>> {

    /**
     * This regular expression matches all valid variable names.
     */
    private static final Pattern VALID_VARIABLE_NAME_REGEX = javaIdentifierRE;

    /**
     * This constructor sets the regular expression for valid variable names.
     */
    public CtRenameMethodRefactoring() {
        super(VALID_VARIABLE_NAME_REGEX);
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

        // TODO: Use filter instead for all methods with same signature? @see CtRenameLocalVariableRefactoring
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

        // Change name of method
        target.setSimpleName(newName);

        // Change name of all invocations
        for (CtInvocation<?> invocation : invocations) {
            invocation.getExecutable().setSimpleName(newName);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkNewNameIsValid() {
        // TODO: Implement using a own regex for javaIdentifierRE
    }

    /**
     * {@inheritDoc}
     * There is a name conflict if there is a method with the same name and signature
     * TODO: Check for lambdas, anonymous classes, overriding methods etc.
     */
    @Override
    protected void detectNameConflicts() {
        // Create a method with the new name as the target
        CtMethod<?> newMethod = target.clone(); // TODO: Try copy method
        newMethod.setSimpleName(newName);

        // Find all executables with same name and signature
        final List<CtMethod<?>> methods = new ArrayList<>();
        NamedElementFilter<?> namedElementFilter = new NamedElementFilter<>(CtMethod.class, newName);
        getTarget().getParent().filterChildren(namedElementFilter).forEach(
            (CtConsumer<CtMethod<?>>) t -> {
                if (t.getSignature().equals(newMethod.getSignature())) {
                    methods.add(t);
                }
            });


        // If the list is not empty, there is a name conflict
        if (!methods.isEmpty()) {
            createNameConflictIssue(methods.get(0));
        }
    }

    /**
     * This method creates a name conflict issue for the given method.
     * TODO: Catch the exception?
     *
     * @param conflictMethod The method with the same name and signature
     */
    private void createNameConflictIssue(CtMethod<?> conflictMethod) throws RefactoringException {
        throw new RefactoringException("There is already a method with the name " + newName + " and the same signature.");
    }
}
