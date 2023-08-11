package de.uni_passau.fim.se2.rdh.util;

import spoon.SpoonAPI;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

/**
 * This class contains utility methods for debugging.
 */
public final class DebugUtils {

    private DebugUtils() {
    }

    /**
     * Gets all methods in the model.
     *
     * @param spoon The SpoonAPI instance.
     * @return A list of all methods in the model.
     */
    public static List<CtMethod<?>> getAllMethods(final SpoonAPI spoon) {
        return spoon.getModel().getElements(new TypeFilter<>(CtMethod.class));
    }

    /**
     * Converts the given arguments of a command line call to a string.
     *
     * @param args The arguments.
     * @return The string representation of the arguments.
     */
    public static String argsToString(final String... args) {
        return String.join(" ", List.of(args));
    }
}
