package de.uni_passau.fim.se2.rdh.refactorings.experimental.imports;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.rename.SimpleMethodRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtType;

import java.util.Collection;
import java.util.List;

/**
 * This class implements a refactoring that replaces all imports with star imports.
 */
public class StarImporter extends AbstractModification {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SimpleMethodRenamer.class);

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

    /**
     * Replaces all imports with star imports.
     * <p>
     * For getting the imports, a compilation unit is needed. If a reference can not be resolved in this compilation
     * unit, the unresolved reference (see spoon.experimental.CtUnresolvedImport) is used.
     */
    private void replaceWithStarImport() {
        CtImportRefactoring refactoring = new CtImportRefactoring();

        // Get all import statements from the compilation unit
        Collection<CtType<?>> types = spoon.getModel().getAllTypes();

        if (!types.iterator().hasNext()) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Compilation unit can not be created as there are no types");
            }
            return;
        }

        CtCompilationUnit compilationUnit = types.iterator().next().getPosition().getCompilationUnit();
        List<CtImport> imports = compilationUnit.getImports();

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
