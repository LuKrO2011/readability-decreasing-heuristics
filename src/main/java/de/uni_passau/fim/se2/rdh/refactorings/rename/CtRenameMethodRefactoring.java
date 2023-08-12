package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.refactorings.RefactoringsUtils;
import spoon.refactoring.AbstractRenameRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.NamedElementFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class implements the refactoring for renaming a method.
 */
public class CtRenameMethodRefactoring extends AbstractRenameRefactoring<CtMethod<?>> {

    /**
     * This regular expression matches all valid variable names.
     */
    private static final Pattern VALID_METHOD_NAME = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*");

    /**
     * This constructor sets the regular expression for valid variable names.
     */
    public CtRenameMethodRefactoring() {
        super(VALID_METHOD_NAME);
    }

    /**
     * This method refactors the method name and all invocations of the method. It does not check for valid method
     * names.
     *
     * @see AbstractRenameRefactoring#refactorNoCheck()
     */
    @Override
    protected void refactorNoCheck() {
        final List<CtInvocation<?>> invocations = RefactoringsUtils.getMethodInvocations(target);

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
        if (!VALID_METHOD_NAME.matcher(newName).matches()) {
            throw new RefactoringException("Invalid method name: " + newName);
        }
    }

    /**
     * {@inheritDoc} There is a name conflict if there is a method with the same name and signature
     * TODO: Check for super/subclass conflicts
     * TODO: Create class for detecting conflicts and use it in RealisticMethodRenamer (and others?)
     */
    @Override
    protected void detectNameConflicts() {
        // TODO: Abstract this part into superclass?
        // Check if the target already has the new name
        if (target.getSimpleName().equals(newName)) {
            return;
        }

        // Find all executables with same name and signature
        final List<CtMethod<?>> methods = methodsWithSameName(newName);

        // If the list is not empty, there is a name conflict
        if (!methods.isEmpty()) {
            createNameConflictIssue(methods.get(0));
        }
    }

    /**
     * This method checks if the name is already used, i.e. if there is a name conflict for the given name.
     *
     * @param name The name to check for conflicts
     * @return True if there is a conflict (the name is already used), false otherwise
     */
    public boolean isUsed(final String name) {
        // Find all executables with same name and signature
        final List<CtMethod<?>> methods = methodsWithSameName(name);

        // If the list is not empty, there is a name conflict
        return !methods.isEmpty();
    }

    /**
     * This method returns all existing methods with the given name.
     *
     * @param name The name of the methods
     * @return A list of methods with the given name
     */
    private List<CtMethod<?>> methodsWithSameName(final String name) {
        // Create a method with the new name as the target
        CtMethod<?> newMethod = target.clone(); // TODO: Try copy method
        newMethod.setSimpleName(name);

        // Find all executables with same name and signature
        final List<CtMethod<?>> methods = new ArrayList<>();
        NamedElementFilter<?> namedElementFilter = new NamedElementFilter<>(CtMethod.class, name);
        getTarget().getParent().filterChildren(namedElementFilter).forEach(
                (CtConsumer<CtMethod<?>>) t -> {
                    if (t.getSignature().equals(newMethod.getSignature())) {
                        methods.add(t);
                    }
                });

        return methods;
    }

    /**
     * This method creates a name conflict issue for the given method.
     *
     * @param conflictMethod The method with the same name and signature
     */
    private void createNameConflictIssue(final CtMethod<?> conflictMethod) {
        throw new RefactoringException(
                "There is already a method with the name " + newName + " and the same signature.");
    }
}
