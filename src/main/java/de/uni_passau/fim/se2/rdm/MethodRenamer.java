package de.uni_passau.fim.se2.rdm;

import spoon.SpoonAPI;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MethodRenamer {

    private final SpoonAPI spoon;

    // TODO: DOES NOT RENAME ALL OCCURRENCES OF METHODS BUT ONLY THE DECLARATIONS

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
            return;
        }

        // Rename all methods to m0 ... mN
        Iterator<CtMethod<?>> iterator = allMethods.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            CtMethod<?> method = iterator.next();
            method.setSimpleName("m" + i);
        }
    }


}
