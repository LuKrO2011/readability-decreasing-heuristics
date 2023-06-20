package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

import java.util.regex.Pattern;

// TODO: Add checks for valid field names

/**
 * This class implements the refactoring for renaming a field/global variable.
 * It does not check for valid field names.
 */
public class CtRenameFieldRefactoring extends AbstractRenameRefactoring<CtField<?>> {

    /**
     * This regular expression matches all valid variable names.
     */
    public static final Pattern VALID_VARIABLE_NAME_REGEX = javaIdentifierRE;

    /**
     * This constructor sets the regular expression for valid variable names.
     */
    public CtRenameFieldRefactoring() {
        super(VALID_VARIABLE_NAME_REGEX);
    }

    /**
     * This method refactors the field name and all references to the field.
     * It does not check for valid field names.
     *
     * @see AbstractRenameRefactoring#refactorNoCheck()
     */
    @Override
    protected void refactorNoCheck() {
        getTarget().map(new VariableReferenceFunction()).forEach(
                (CtConsumer<CtReference>) t -> t.setSimpleName(newName));
        target.setSimpleName(newName);
    }
}
