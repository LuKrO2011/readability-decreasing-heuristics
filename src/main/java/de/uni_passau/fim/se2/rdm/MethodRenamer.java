package de.uni_passau.fim.se2.rdm;

import de.uni_passau.fim.se2.rdm.refactorings.CtRenameMethodRefactoring;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.*;

public class MethodRenamer {

    private final SpoonAPI spoon;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public MethodRenamer(SpoonAPI spoon) {
        this.spoon = spoon;
    }

    public void rename() {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        // Get all local variables
        List<CtMethod<Integer>> methods = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() <= 0) {
            // log....
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < methods.size(); i++) {
            CtMethod<Integer> method = methods.get(i);
            try {
                refactoring.setTarget(method);
                refactoring.setNewName("m" + i);
                refactoring.refactor();
            } catch (RefactoringException e) {
                // log...
            }
        }
    }

    public void renameOld() {
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
