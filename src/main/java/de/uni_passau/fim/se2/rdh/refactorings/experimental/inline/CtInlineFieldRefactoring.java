package de.uni_passau.fim.se2.rdh.refactorings.experimental.inline;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spoon.refactoring.CtRefactoring;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.VariableReferenceFunction;

import java.util.List;

/**
 * This class implements the refactoring for inlining a field.
 */
public class CtInlineFieldRefactoring implements CtRefactoring {

    /**
     * The target is a field that should be inlined.
     */
    private CtField<?> target;

    /**
     * This method performs the inlining of the field.
     */
    @Override
    public void refactor() {

        // Get all references to the field
        List<Object> fieldReferences = target.map(new VariableReferenceFunction()).list();

        // Replace all references with the literal
        for (Object o : fieldReferences) {
            if (o instanceof CtFieldReference<?> field) {
                CtExpression<?> replacingExpression;

                // Check if the field is uninitialized
                if (target.getDefaultExpression() == null) {

                    // Get the default value for the field's type
                    Object defaultValue = getDefaultValue(target.getType());
                    replacingExpression = target.getFactory().createLiteral(defaultValue);
                } else {

                    // Clone the literal of the field and replace the field with it
                    replacingExpression = target.getDefaultExpression().clone();
                }

                field.getParent().replace(replacingExpression);
            }
        }
    }

    /**
     * This method returns the default value for a type.
     *
     * @param typeRef The type.
     * @return The default value for the type.
     */
    private Object getDefaultValue(final CtTypeReference<?> typeRef) {
        if (typeRef.isPrimitive()) {
            return getPrimitiveDefaultValue(typeRef.getQualifiedName());
        } else {
            return null;
        }
    }

    /**
     * This method returns the default value for a primitive type.
     *
     * @param qualifiedName The qualified name of the primitive type.
     * @return The default value for the primitive type.
     */
    private Object getPrimitiveDefaultValue(final String qualifiedName) {
        return switch (qualifiedName) {
            case "boolean" -> false;
            case "byte" -> (byte) 0;
            case "char" -> (char) 0;
            case "double" -> 0.0d;
            case "float" -> 0.0f;
            case "int" -> 0;
            case "long" -> 0L;
            case "short" -> (short) 0;
            default -> null;
        };
    }


    /**
     * This method returns the target of the refactoring.
     *
     * @return The target of the refactoring.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP") // The target can be changed by the user at runtime
    public CtField<?> getTarget() {
        return target;
    }

    /**
     * This method sets the target of the refactoring.
     *
     * @param target The target of the refactoring.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The target can be changed by the user at runtime
    public void setTarget(final CtField<?> target) {
        this.target = target;
    }
}
