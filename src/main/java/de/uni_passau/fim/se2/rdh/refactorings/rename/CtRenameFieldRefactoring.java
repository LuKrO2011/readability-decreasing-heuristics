package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.util.NumberIterator;
import spoon.refactoring.AbstractRenameRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class implements the refactoring for renaming a field/global variable. It does not check for valid field names.
 */
public class CtRenameFieldRefactoring extends AbstractRenameRefactoring<CtField<?>> {

    /**
     * This regular expression matches all valid variable names.
     */
    private static final Pattern VALID_FIELD_NAME = Pattern.compile("[a-zA-Z_$][a-zA-Z0-9_$]*");

    /**
     * This constructor sets the regular expression for valid variable names.
     */
    public CtRenameFieldRefactoring() {
        super(VALID_FIELD_NAME);
    }

    /**
     * This method refactors the field name and all references to the field. It does not check for valid field names.
     *
     * @see AbstractRenameRefactoring#refactorNoCheck()
     */
    @Override
    protected void refactorNoCheck() {
        getTarget().map(new VariableReferenceFunction()).forEach(
                (CtConsumer<CtReference>) t -> t.setSimpleName(newName));
        target.setSimpleName(newName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkNewNameIsValid() {
        if (!VALID_FIELD_NAME.matcher(newName).matches()) {
            throw new RefactoringException("Invalid field name: " + newName);
        }
    }

    /**
     * {@inheritDoc} There is a name conflict if there is another field with the same name in the class scope.
     */
    @Override
    protected void detectNameConflicts() {
        // Create a field with the new name as the target
        CtField<?> newField = target.clone();
        newField.setSimpleName(newName);

        // Find all fields with the new name
        final List<CtField<?>> fields = new ArrayList<>();
        NamedElementFilter<?> sameNameFilter = new NamedElementFilter<>(CtField.class, newName);
        getTarget().getDeclaringType().filterChildren(sameNameFilter).forEach((CtConsumer<CtField<?>>) fields::add);

        // If the list is not empty, there is a name conflict
        if (!fields.isEmpty()) {
            createNameConflictIssue();
        }
    }

    /**
     * This method creates a name conflict issue for the given field.
     *
     */
    private void createNameConflictIssue() {
        throw new RefactoringException(
                "There is already a field with the name " + newName + " in the class " + target.getDeclaringType()
                        .getQualifiedName());
    }


}
