package de.uni_passau.fim.se2.rdh.refactorings.experimental.imports;

import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.ExecutableReferenceFilter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.declaration.CtImportImpl;

import java.util.ArrayList;
import java.util.List;


public class CtImportRefactoring implements CtRefactoring {

    /**
     * The target is a method invocation that should be inlined.
     */
    private CtImport target;

    @Override
    public void refactor() {
        replaceWithStarImport();
    }

    /**
     * Replaces the target import with a star import.
     */
    private void replaceWithStarImport() {
        // Clone the target
        CtImport newImport = target.clone();

        // Get the type reference from the original import
        CtTypeReference<?> typeReference = (CtTypeReference<?>) target.getReference();

        // Remove the last part of the import and replace it with .*
        String packageName = typeReference.getPackage().getQualifiedName();
        packageName += ".*";

        // Create a new type reference with the modified package name
        CtTypeReference<?> starTypeReference = target.getFactory().Type().createReference(packageName);

        // Set the new type reference in the cloned import
        newImport.setReference(starTypeReference);

        // Replace the original import with the new import
        target.replace(newImport);
    }

    /**
     * This method returns the target of the refactoring.
     *
     * @return The target of the refactoring.
     */
    public CtImport getTarget() {
        return target;
    }

    /**
     * This method sets the target of the refactoring.
     *
     * @param target The target of the refactoring.
     */
    public void setTarget(final CtImport target) {
        this.target = target;
    }
}
