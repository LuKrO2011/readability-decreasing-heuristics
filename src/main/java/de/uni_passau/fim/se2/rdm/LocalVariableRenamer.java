package de.uni_passau.fim.se2.rdm;

import de.uni_passau.fim.se2.rdm.config.RdcProbabilities;
import spoon.SpoonAPI;
import spoon.refactoring.CtRenameLocalVariableRefactoring;
import spoon.refactoring.RefactoringException;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class LocalVariableRenamer {

    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;

    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public LocalVariableRenamer(SpoonAPI spoon, RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
    }

    public void rename() {
        CtRenameLocalVariableRefactoring refactoring = new CtRenameLocalVariableRefactoring();

        // Get all local variables
        List<CtLocalVariable<Integer>> localVariables = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtLocalVariable.class));

        if (localVariables.size() <= 0) {
            // log....
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < localVariables.size(); i++) {
            if (probabilities.shouldRenameVariable()) {
                CtLocalVariable<Integer> localVariable = localVariables.get(i);
                try {
                    refactoring.setTarget(localVariable);
                    refactoring.setNewName("v" + i);
                    refactoring.refactor();
                } catch (RefactoringException e) {
                    // log...
                }
            }
        }

    }


}
