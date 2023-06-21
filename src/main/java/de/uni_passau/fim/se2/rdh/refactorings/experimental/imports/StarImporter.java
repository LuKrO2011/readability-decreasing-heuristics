package de.uni_passau.fim.se2.rdh.refactorings.experimental;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.imports.CtImportRefactoring;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;


public class StarImporter extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MethodRenamer.class);

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     */
    public StarImporter(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        replaceWithStarImport();
    }

    private void replaceWithStarImport() {
        CtImportRefactoring refactoring = new CtImportRefactoring();

        // Get all imports
        List<CtImport> imports = spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtImport.class));

        if (imports.size() == 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No imports found");
            }
            return;
        }

        // Replace imports with star imports
        for (CtImport ctMethod : imports) {
            if (!probabilities.shouldReplaceWithStarImport()) {
                continue;
            }

            try {
                refactoring.setTarget(ctMethod);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not inline method", e);
            }
        }
    }
}
