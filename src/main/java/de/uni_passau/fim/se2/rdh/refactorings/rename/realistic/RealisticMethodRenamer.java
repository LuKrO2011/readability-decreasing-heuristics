package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.models.PythonRunner;
import de.uni_passau.fim.se2.rdh.refactorings.rename.CtRenameMethodRefactoring;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Set;

/**
 * Renames methods to m0 ... mN.
 * <p>
 * This class is used to rename methods to m0 ... mN. The probability for this refactoring to be performed on a method
 * is defined in the {@link RdcProbabilities} class.
 * </p>
 */
public class RealisticMethodRenamer extends MethodRenamer {

    /**
     * The (logger) of this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(RealisticMethodRenamer.class);

    // TODO: Put this into config file?
    /**
     * The index of the prediction quality in the prediction data. The lower the index, the higher the quality.
     */
    public static final int PREDICTION_QUALITY_INDEX = 0;

    private final MethodRenamer backup;

    private final Set<String> inputResources;

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon          the spoon instance
     * @param probabilities  the probabilities
     * @param backup         the backup method renamer
     * @param inputResources the input resources
     */
    public RealisticMethodRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities,
                                  final MethodRenamer backup, final Set<String> inputResources) {
        super(spoon, probabilities);
        this.backup = backup;
        this.inputResources = inputResources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        rename();
    }


    /**
     * Renames methods to m0 ... mN.
     */
    private void rename() {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        // Get all local variables
        List<CtMethod<Integer>> methods =
            spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtMethod.class));

        if (methods.size() == 0) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("No methods found");
            }
            return;
        }

        // Get the new names for the methods
        JsonLoader jsonLoader = new JsonLoader();

        // TODO: How to handle those input files? (use inputResources)
        List<MethodRenamingData> newNames =
            jsonLoader.loadMethodRenamingData("src/test/resources/predictions/HeapUtils.json");

        if (methods.size() != newNames.size()) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Number of methods and new names does not match. Using backup method renamer.");
            }
            backup.apply();
            return;
        }

        // Rename all local variables according to the predictions of code2vec
        for (int i = 0; i < methods.size(); i++) {
            if (!probabilities.shouldRenameMethod()) {
                continue;
            }

            CtMethod<Integer> method = methods.get(i);
            MethodRenamingData renamingData = newNames.get(i);

            if (!method.getSimpleName().equals(renamingData.getOriginalName())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Method name of method to rename did not match. Expected: "
                        + renamingData.getOriginalName() + " but was " + method.getSimpleName() + ".");
                }
            }

            try {
                refactoring.setTarget(method);
                String newName = renamingData.getPredictions().get(PREDICTION_QUALITY_INDEX).getName();
                refactoring.setNewName(newName);
                refactoring.refactor();
            } catch (RefactoringException e) {
                LOG.error("Could not rename method", e);
            }
        }
    }

}
