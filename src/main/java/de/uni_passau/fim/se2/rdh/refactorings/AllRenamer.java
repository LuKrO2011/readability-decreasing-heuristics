package de.uni_passau.fim.se2.rdh.refactorings;

import spoon.SpoonAPI;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AllRenamer {

    private final SpoonAPI spoon;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public AllRenamer(SpoonAPI spoon) {
        this.spoon = spoon;
    }

    public void rename() {
        Collection<CtType<?>> allTypes = spoon.getModel().getAllTypes();
        Set<CtTypeMember> allMethods = new HashSet<>();

        // Get all type members
        for (CtType<?> type : allTypes) {
            allMethods.addAll(type.getTypeMembers());
        }

        // Return if there are no type members
        if (allMethods.isEmpty()) {
            return;
        }

        // Rename all type members to tm0 ... tmN
        Iterator<CtTypeMember> iterator = allMethods.iterator();
        for (int i = 0; iterator.hasNext(); i++) {
            CtTypeMember method = iterator.next();
            method.setSimpleName("tm" + i);
        }
    }

}
