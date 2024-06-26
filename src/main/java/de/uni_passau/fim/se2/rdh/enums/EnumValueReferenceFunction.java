package de.uni_passau.fim.se2.rdh.enums;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.DirectReferenceFilter;

/**
 * This Query expects a CtEnumValue as input and returns all CtFieldReferences, which refers this input. This class is
 * similar to {@link spoon.reflect.visitor.filter.FieldReferenceFunction} but for {@link CtEnumValue}.
 * <p>
 * Usage: CtEnumValue param = ...; param.map(new EnumValueReferenceFunction()).forEach((CtFieldReference
 * ref)->...process references...);
 * </p>
 */
public class EnumValueReferenceFunction implements CtConsumableFunction<CtElement> {
    private final CtEnumValue<?> field;

    /**
     * Creates a new instance of FieldReferenceFunction.
     */
    public EnumValueReferenceFunction() {
        this.field = null;
    }

    /**
     * Creates a new instance of FieldReferenceFunction.
     *
     * @param field The field to search for.
     */

    @SuppressFBWarnings("EI_EXPOSE_REP2") // The field can be changed by the user at runtime
    public EnumValueReferenceFunction(final CtEnumValue<?> field) {
        this.field = field;
    }

    /**
     * Applies the function to the given input element.
     *
     * @param enumOrScope    the input of the function
     * @param outputConsumer the consumer which accepts the results of this function.
     */
    @Override
    public void apply(final CtElement enumOrScope, final CtConsumer<Object> outputConsumer) {
        CtElement scope;
        CtEnumValue<?> ctEnum = this.field;
        if (ctEnum == null) {
            if (enumOrScope instanceof CtEnumValue<?>) {
                ctEnum = (CtEnumValue<?>) enumOrScope;
            } else {
                throw new SpoonException(
                        "The input of FieldReferenceFunction must be a CtEnumValue but is " + enumOrScope.getClass()
                                .getSimpleName());
            }
            scope = ctEnum.getFactory().getModel().getUnnamedModule();
        } else {
            scope = enumOrScope;
        }
        scope
                .filterChildren(new DirectReferenceFilter<CtFieldReference<?>>(ctEnum.getReference()))
                .forEach(outputConsumer);
    }
}
