package de.uni_passau.fim.se2.rdm.old;

import spoon.SpoonAPI;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MethodRenamer {

    private final SpoonAPI spoon;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodRenamer(SpoonAPI spoon) {
        this.spoon = spoon;
    }

    public void rename() {
        Collection<CtType<?>> allTypes = spoon.getModel().getAllTypes();
        Set<CtMethod<?>> allMethods = new HashSet<>();

        // Get all method declarations from allTypes
        for (CtType<?> type : allTypes) {
            allMethods.addAll(type.getMethods());
        }

        // Return if there are no method declarations
        if (allMethods.isEmpty()) {
            // log...
            return;
        }

        // Get the first method declaration
        CtMethod<?> firstMethod = allMethods.iterator().next();

        // Set the name of the first method to "foo"
        firstMethod.setSimpleName("UPDATED_METHOD_NAME");
    }


}
