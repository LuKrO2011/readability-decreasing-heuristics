package de.uni_passau.fim.se2.rdh.refactorings.experimental.inline;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class FieldInliner extends AbstractModification {


    private static final Logger LOG = LoggerFactory.getLogger(MethodRenamer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public FieldInliner(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        inline();
    }

    /**
     * Inlines fields.
     */
    private void inline() {
        CtInlineFieldRefactoring refactoring = new CtInlineFieldRefactoring();

        // Get all global variables = fields
        List<CtField<Integer>> globalVariables =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtField.class));

        if (globalVariables.size() == 0) {
            LOG.warn("No global variables found");
            return;
        }

        // Inline fields with a certain probability
        for (CtField<Integer> variable : globalVariables) {
            if (!probabilities.shouldInlineField()) {
                continue;
            }

            try {
                refactoring.setTarget(variable);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not inline global variable", e);
            }
        }

    }


}
