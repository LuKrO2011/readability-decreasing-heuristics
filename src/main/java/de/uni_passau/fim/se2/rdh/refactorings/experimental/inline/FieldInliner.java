package de.uni_passau.fim.se2.rdh.refactorings.experimental.inline;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.rename.SimpleMethodRenamer;
import de.uni_passau.fim.se2.rdh.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

public class FieldInliner extends AbstractModification {


    private static final Logger LOG = LoggerFactory.getLogger(SimpleMethodRenamer.class);

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
            LOG.info("No global variables found");
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
            } catch (SpoonException e) {
                Logging.logRefactoringFailed(LOG, "Could not inline field " + variable.getSimpleName(), e);
            }
        }

    }


}
