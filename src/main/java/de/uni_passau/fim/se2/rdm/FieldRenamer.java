package de.uni_passau.fim.se2.rdm;

import de.uni_passau.fim.se2.rdm.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdm.refactorings.CtRenameFieldRefactoring;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class FieldRenamer {

    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;


    // private static final Logger log = LoggerFactory.getLogger(MethodRenamer.class);

    public FieldRenamer(SpoonAPI spoon, RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
    }

    public void rename() {
        CtRenameFieldRefactoring refactoring = new CtRenameFieldRefactoring();

        // Get all global variables
        List<CtField<Integer>> globalVariables = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class));

        if (globalVariables.size() <= 0) {
            // log....
            return;
        }

        // Rename all local variables to vl0 ... vlN
        for (int i = 0; i < globalVariables.size(); i++) {
            if (!probabilities.shouldRenameField()) {
                continue;
            }

            CtField<Integer> globalVariable = globalVariables.get(i);
            try {
                refactoring.setTarget(globalVariable);
                refactoring.setNewName("f" + i);
                refactoring.refactor();
            } catch (RefactoringException e) {
                // log...
            }
        }

    }


}
