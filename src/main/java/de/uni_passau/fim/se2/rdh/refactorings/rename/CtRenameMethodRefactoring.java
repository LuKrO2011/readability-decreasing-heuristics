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
import spoon.reflect.visitor.filter.AllMethodsSameSignatureFunction;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;
import spoon.reflect.visitor.filter.VariableReferenceFunction;
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
     * TODO: Implement me and test me
     * TODO: AllMethodsSameSignatureFunction is probably not the right way
     */
    @Override
    protected void detectNameConflicts() {
        // Create an executable with the new name from the executable of the target
        /*CtExecutableImpl<?> executable = (CtExecutableImpl<?>) target.getReference().getExecutableDeclaration();
        CtExecutableImpl<?> executableWithNewName = (CtExecutableImpl<?>) executable.clone();
        executableWithNewName.setSimpleName(newName);

        // TODO: Include lambdas not working!
        // Check if there is a method with the same name and signature
        getTarget().map((new AllMethodsSameSignatureFunction()).includingSelf(false).includingLambdas(false))
            .forEach(conflict ->
                createNameConflictIssue((CtMethod<?>) conflict)
            );*/
    }

    /**
     * Override this method to get access to details about this refactoring issue
     *
     * @param conflictMethod The method with the same name and signature
     */
    protected void createNameConflictIssue(CtMethod<?> conflictMethod) {
        throw new RefactoringException(conflictMethod.getClass().getSimpleName()
            + " with name " + conflictMethod.getSimpleName() + " is in conflict.");
    }
}
