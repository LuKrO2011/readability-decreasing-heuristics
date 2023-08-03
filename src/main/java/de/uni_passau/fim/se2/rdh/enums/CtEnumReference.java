package de.uni_passau.fim.se2.rdh.enums;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DerivedProperty;

import java.lang.reflect.Member;


/**
 * This interface defines a reference to a CtEnum. The interface is similar to
 * {@link spoon.reflect.reference.CtFieldReference}.
 *
 * @param <T> the type of the enum
 */
public interface CtEnumReference<T> extends CtReference {

    /**
     * Gets the runtime member that corresponds to an enum reference if any.
     *
     * @return the member (null if not found)
     */
    Member getActualEnum();

    @Override
    @DerivedProperty
    CtEnum<?> getDeclaration();

    /**
     * Returns the {@link CtEnum} that corresponds to the reference even if its declaring type isn't in the Spoon source
     * path  (in this case, the Spoon elements are built with runtime reflection)
     *
     * @return the field declaration that corresponds to the reference.
     */
    @DerivedProperty
    CtEnum<?> getEnumDeclaration();

    /**
     * Gets the type in which the enum is declared.
     */
    @PropertyGetter(role = CtRole.DECLARING_TYPE)
    CtTypeReference<?> getDeclaringType();

    /**
     * Gets the qualified name of the enum.
     */
    String getQualifiedName();

    /**
     * Tells if the referenced enum is final.
     */
    @PropertyGetter(role = CtRole.IS_FINAL)
    boolean isFinal();

    /**
     * Tells if the referenced enum is static.
     */
    @PropertyGetter(role = CtRole.IS_STATIC)
    boolean isStatic();

    /**
     * Sets the type in which the enum is declared.
     */
    @PropertySetter(role = CtRole.DECLARING_TYPE)
    <C extends CtEnumReference<T>> C setDeclaringType(CtTypeReference<?> declaringType);

    /**
     * Forces a reference to a final element.
     */
    @PropertySetter(role = CtRole.IS_FINAL)
    <C extends CtEnumReference<T>> C setFinal(boolean b);

    /**
     * Forces a reference to a static element.
     */
    @PropertySetter(role = CtRole.IS_STATIC)
    <C extends CtEnumReference<T>> C setStatic(boolean b);

    @Override
    CtEnumReference<T> clone();
}
