/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package de.uni_passau.fim.se2.rdh.enums;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;

import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.DirectReferenceFilter;

// TODO: Similar to CtFieldReferenceFunction
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
                throw new SpoonException("The input of FieldReferenceFunction must be a CtEnumValue but is " + enumOrScope.getClass().getSimpleName());
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
