package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.rename.CtRenameMethodRefactoring;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.SimpleMethodRenamer;
import de.uni_passau.fim.se2.rdh.util.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.SpoonException;
import spoon.reflect.code.CtComment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final SimpleMethodRenamer backup;

    private final NameSelectionMode nameSelectionMode = NameSelectionMode.LONGEST;

    /**
     * This constructor sets the spoon instance and the probabilities to be used.
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities
     * @param backup        the backup method renamer
     */
    public RealisticMethodRenamer(final SpoonAPI spoon, final RdcProbabilities probabilities,
                                  final SimpleMethodRenamer backup) {
        super(spoon, probabilities);
        this.backup = backup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        Map<File, CtType<?>> files = getAllFiles();

        // Rename methods of each file
        for (Map.Entry<File, CtType<?>> entry : files.entrySet()) {
            File file = entry.getKey();
            CtType<?> ctClass = entry.getValue();

            // Split methods into methods with and without implementation
            SplitMethods splitMethods = splitMethods(ctClass);

            // Rename methods without implementation using the backup method renamer
            for (CtMethod<?> ctMethod : splitMethods.methodsWithoutImpl()) {
                backup.rename(ctMethod);
            }

            // Rename methods with implementation with realistic names
            rename(ctClass, splitMethods.methodsWithBody());
        }
    }

    /**
     * Splits the methods of the given class into methods with and without implementation.
     * <p>
     * A method is considered to have an implementation if it has a body and the body contains at least one statement
     * that is not a comment.
     *
     * @param ctClass the class
     * @return the split methods
     */
    private static SplitMethods splitMethods(final CtType<?> ctClass) {
        // Get all methods of the class
        List<CtMethod<?>> methodsOfClass = ctClass.getElements(new TypeFilter<>(CtMethod.class));

        // Split methods into methods with and without implementation
        List<CtMethod<?>> methodsWithImpl = methodsOfClass.stream().filter(m -> !m.isAbstract()).toList();
        List<CtMethod<?>> methodsWithoutImpl =
                new ArrayList<>(methodsOfClass.stream().filter(CtMethod::isAbstract).toList());

        // Split methods with and without empty body
        List<CtMethod<?>> methodsWithoutBody = new ArrayList<>();
        List<CtMethod<?>> methodsWithBody = new ArrayList<>();
        for (CtMethod<?> ctMethod : methodsWithImpl) {
            boolean hasBody = ctMethod.getBody() != null && ctMethod.getBody().getStatements().size() > 0;
            boolean hasNotOnlyComments =
                    hasBody && ctMethod.getBody().getStatements().stream().anyMatch(s -> !(s instanceof CtComment));
            if (hasNotOnlyComments) {
                methodsWithBody.add(ctMethod);
            } else {
                methodsWithoutBody.add(ctMethod);
            }
        }

        // Add methods without body to methods without implementation
        methodsWithoutImpl.addAll(methodsWithoutBody);
        return new SplitMethods(methodsWithoutImpl, methodsWithBody);
    }

    private record SplitMethods(List<CtMethod<?>> methodsWithoutImpl, List<CtMethod<?>> methodsWithBody) {
    }

    /**
     * Gets all files used by the spoon model together with the corresponding {@link CtType}.
     *
     * @return A map containing the files and the corresponding {@link CtType}
     */
    private Map<File, CtType<?>> getAllFiles() {
        Map<File, CtType<?>> files = new HashMap<>();
        spoon.getModel().getAllTypes().forEach(
                t -> {
                    File filename = t.getPosition().getFile();
                    files.put(filename, t);
                }
        );
        return files;
    }

    /**
     * Renames the given methods with a certain probability. The new names are loaded from a json file containing the
     * predictions of code2vec.
     *
     * @param clazz   the class containing the methods
     * @param methods the methods to be renamed
     */
    private void rename(final CtType<?> clazz, final List<CtMethod<?>> methods) {
        CtRenameMethodRefactoring refactoring = new CtRenameMethodRefactoring();

        if (methods.size() == 0) {
            LOG.info("No methods found");
            return;
        }

        List<MethodRenamingData> newNames = new ArrayList<>();
        try {
            newNames = loadNewNames(clazz);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }

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
                String newName = getNotUsedName(renamingData, refactoring);

                if (newName == null) {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("Could not find a not used name for method " + method.getSimpleName() + ". Using "
                                + "backup method renamer.");
                    }
                    backup.rename(method);
                    continue;
                }

                refactoring.setNewName(newName);
                refactoring.refactor();
            } catch (SpoonException e) {
                Logging.logRefactoringFailed(LOG, "Could not rename method " + method.getSimpleName(), e);
            }
        }
    }

    /**
     * Returns a name that is not yet used in the given class. Returns null if no not used name could be found.
     *
     * @param renamingData the renaming data
     * @param refactoring  the refactoring
     * @return the new name. Null if no not used name could be found.
     */
    private String getNotUsedName(final MethodRenamingData renamingData, final CtRenameMethodRefactoring refactoring) {
        List<String> newNames = getNewNames(renamingData);
        for (String newName : newNames) {
            refactoring.setNewName(newName);
            if (!refactoring.isUsed(newName)) {
                return newName;
            }
        }

        return null;
    }

    /**
     * Returns the preferred new names for the given renaming data depending on the name selection mode in descending
     * order. The first name is the most preferred one.
     *
     * @param renamingData the renaming data
     * @return the new name
     */
    private List<String> getNewNames(final MethodRenamingData renamingData) {
        return renamingData.getPredictions(nameSelectionMode).stream()
                .map(PredictionData::getName)
                .toList();
    }

    /**
     * Loads the new method names for the given class.
     *
     * @param clazz the class
     * @return the new names
     * @throws IOException if the json file could not be found
     */
    private List<MethodRenamingData> loadNewNames(final CtType<?> clazz) throws IOException {
        String jsonPath = getPath(clazz);
        File jsonFile = new File(jsonPath);
        if (!jsonFile.exists()) {
            throw new IOException("Could not find json file " + jsonPath + " for class " + clazz.getSimpleName() + ".");
        }

        // Get the new names for the methods
        JsonLoader jsonLoader = new JsonLoader();
        return jsonLoader.loadMethodRenamingData(jsonPath);
    }


    /**
     * Returns the path to the json file with the new names. The file has the same name as the clazz file but with the
     * extension .json.
     *
     * @param clazz the class
     * @return the path to the json file
     */
    private static String getPath(final CtType<?> clazz) {
        SourcePosition position = clazz.getPosition();
        File file = position.getFile();
        String path = file.getAbsolutePath();
        return path.substring(0, path.lastIndexOf('.')) + ".json";
    }

}
