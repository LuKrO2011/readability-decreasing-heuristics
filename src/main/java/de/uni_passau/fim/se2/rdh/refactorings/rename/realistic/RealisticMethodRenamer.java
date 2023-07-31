package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.rename.CtRenameMethodRefactoring;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import de.uni_passau.fim.se2.rdh.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

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

    // TODO: Get this from config file
    private static final String NEW_NAMES_PATH = "src/test/resources/predictions";

    // TODO: Get this from config file
    private final NameSelectionMode nameSelectionMode = NameSelectionMode.LONGEST;

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     * @param backup        the backup method renamer
     */
    public RealisticMethodRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities,
                                  final MethodRenamer backup) {
        super(spoon, probabilities);
        this.backup = backup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        // Get all class files
        List<CtClass<?>> classes =
                spoon.getModel().getRootPackage().getElements(new TypeFilter<>(CtClass.class));

        // Rename all methods for each class
        for (CtClass<?> ctClass : classes) {
            List<CtMethod<?>> methodsOfClass = ctClass.getElements(new TypeFilter<>(CtMethod.class));
            rename(ctClass, methodsOfClass);
        }
    }

    /**
     * Renames the given methods with a certain probability. The new names are loaded from a json file containing the
     * predictions of code2vec.
     *
     * @param clazz   the class containing the methods
     * @param methods the methods to be renamed
     */
    private void rename(final CtClass<?> clazz, final List<CtMethod<?>> methods) {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        if (methods.size() == 0) {
            LOG.info("No methods found");
            return;
        }

        List<MethodRenamingData> newNames = loadNewNames(clazz);
        if (methods.size() != newNames.size()) {
            if (LOG.isWarnEnabled()) {
                LOG.warn("Number of methods and new names does not match. Using backup method renamer.");
            }
            backup.apply();
            return;
        }

        // Rename all local methods according to the predictions of code2vec
        for (int i = 0; i < methods.size(); i++) {
            if (!probabilities.shouldRenameMethod()) {
                continue;
            }

            // Get the class name to find the corresponding new names
            // String className = methods.get(i).getParent(CtMethod.class).getSimpleName();

            CtMethod<?> method = methods.get(i);
            MethodRenamingData renamingData = newNames.get(i);

            if (!method.getSimpleName().equals(renamingData.getOriginalName())) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Method name of method to rename did not match. Expected: "
                            + renamingData.getOriginalName() + " but was " + method.getSimpleName() + ".");
                }
            }

            try {
                refactoring.setTarget(method);
                String newName = getNewName(renamingData);
                refactoring.setNewName(newName);
                refactoring.refactor();
            } catch (SpoonException e) {
                Logging.logRefactoringFailed(LOG, "Could not rename method " + method.getSimpleName(), e);
            }
        }
    }

    /**
     * Returns the new name for the given renaming data depending on the name selection mode.
     *
     * @param renamingData the renaming data
     * @return the new name
     */
    private String getNewName(final MethodRenamingData renamingData) {
        return switch (nameSelectionMode) {
            case QUALITY -> renamingData.getPredictions().get(PREDICTION_QUALITY_INDEX).getName();
            case LONGEST -> renamingData.getLongestPrediction().getName();
        };
    }

    /**
     * Loads the new method names for the given class.
     *
     * @param clazz the class
     * @return the new names
     */
    private List<MethodRenamingData> loadNewNames(final CtClass<?> clazz) {
        // Get the class name to find the corresponding new names
        String className = clazz.getSimpleName();

        // Get the new names for the methods
        JsonLoader jsonLoader = new JsonLoader();
        return jsonLoader.loadMethodRenamingData(NEW_NAMES_PATH + "/" + className + ".json");
    }

}
