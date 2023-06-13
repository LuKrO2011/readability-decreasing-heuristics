package de.uni_passau.fim.se2.rdm.refactorings;

import spoon.refactoring.AbstractRenameRefactoring;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

import java.util.regex.Pattern;

// TODO: Add checks for valid field names

public class CtRenameFieldRefactoring extends AbstractRenameRefactoring<CtField<?>> {

    public static final Pattern validVariableNameRE = javaIdentifierRE;

    public CtRenameFieldRefactoring() {
        super(validVariableNameRE);
    }

    @Override
    protected void refactorNoCheck() {
        getTarget().map(new VariableReferenceFunction()).forEach(new CtConsumer<CtReference>() {
            @Override
            public void accept(CtReference t) {
                t.setSimpleName(newName);
            }
        });
        target.setSimpleName(newName);
    }
}
