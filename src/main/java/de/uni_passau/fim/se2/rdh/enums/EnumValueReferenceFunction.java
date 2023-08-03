package de.uni_passau.fim.se2.rdh.enums;

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

    public EnumValueReferenceFunction() {
        this.field = null;
    }

    public EnumValueReferenceFunction(CtEnumValue<?> field) {
        this.field = field;
    }

    @Override
    public void apply(CtElement enumOrScope, CtConsumer<Object> outputConsumer) {
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
