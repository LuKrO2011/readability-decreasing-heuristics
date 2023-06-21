package de.uni_passau.fim.se2.rdh.util;

import spoon.SpoonAPI;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public final class DebugUtils {

    private DebugUtils() {
    }

    public static List<CtMethod<?>> getAllMethods(final SpoonAPI spoon) {
        return spoon.getModel().getElements(new TypeFilter<>(CtMethod.class));
    }
}
