package de.uni_passau.fim.se2.rdm.old;

import spoon.SpoonAPI;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class VariableRenamer {

    private final SpoonAPI spoon;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public VariableRenamer(SpoonAPI spoon) {
        this.spoon = spoon;
    }

    public void rename() {
        CtRenameLocalVariableRefactoring refactoring = new CtRenameLocalVariableRefactoring();

        // Get local variable named ... using spoon from spoonUniverse
        List<CtLocalVariable<Integer>> variablesNamesProcessId = spoon.getModel().getRootPackage()
                .getElements(new TypeFilter<CtLocalVariable<Integer>>(CtLocalVariable.class) {
                    @Override
                    public boolean matches(CtLocalVariable<Integer> element) {
                        return super.matches(element) &&
                                element.getSimpleName().equals("processId");
                    }
                });

        if (variablesNamesProcessId.size() != 1) {
            // log....
            return;
        }

        CtLocalVariable<Integer> i = variablesNamesProcessId.iterator().next();

        // Rename i to j
        refactoring.setTarget(i);
        refactoring.setNewName("UPDATED_VARIABLE_NAME");
        try {
            refactoring.refactor();
        } catch (RefactoringException e) {
            e.printStackTrace();
        }
    }


}
