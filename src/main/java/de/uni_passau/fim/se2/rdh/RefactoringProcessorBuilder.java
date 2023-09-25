package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.RenameMode;
import de.uni_passau.fim.se2.rdh.output.NewFolderOutputWriter;
import de.uni_passau.fim.se2.rdh.output.SameFolderOutputWriter;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.imports.StarImporter;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.inline.MethodInliner;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers.OperationInserter;
import de.uni_passau.fim.se2.rdh.refactorings.rename.FieldRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.LocalVariableRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.SimpleMethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.realistic.RealisticMethodRenamer;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import spoon.Launcher;
import spoon.SpoonAPI;

import java.util.List;

/**
 * The RefactoringProcessorBuilder is responsible for creating a RefactoringProcessor.
 */
public class RefactoringProcessorBuilder {

    private final Config config;

    /**
     * Creates a new RefactoringProcessorFactory with default config.
     */
    public RefactoringProcessorBuilder() {
        this(new Config());
    }

    /**
     * Creates a new RefactoringProcessorFactory with the given config.
     *
     * @param config the config to use
     */
    public RefactoringProcessorBuilder(final Config config) {
        this.config = new Config(config);
    }

    /**
     * Creates a new RefactoringProcessor.
     *
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities to use
     * @return the RefactoringProcessor
     */
    public RefactoringProcessor create(final ProcessingPath outputDirPath, final RdcProbabilities probabilities) {
        // Create spoon launcher
        SpoonAPI spoon = new Launcher();

        // Create the refactorings
        List<AbstractModification> modifications = getRefactorings(spoon, probabilities);

        // Create the processor
        RefactoringProcessor refactoringProcessor = new RefactoringProcessor(spoon, outputDirPath, probabilities);

        // Add the refactorings
        modifications.forEach(refactoringProcessor::addModification);

        // Set the output writer
        switch (config.getOutputMode()) {
            case NEW_DIRECTORY -> refactoringProcessor.setOutputWriter(new NewFolderOutputWriter(spoon));
            case SAME_DIRECTORY ->
                    refactoringProcessor.setOutputWriter(new SameFolderOutputWriter(spoon, probabilities));
            default -> throw new IllegalStateException("Unexpected value: " + config.getOutputMode());
        }

        return refactoringProcessor;
    }

    /**
     * Initializes the refactorings.
     * TODO: Use a map?
     *
     * @param spoon         the spoon instance
     * @param probabilities the probabilities to use
     * @return the default refactorings
     */
    private List<AbstractModification> getRefactorings(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        SimpleMethodRenamer simpleMethodRenamer = new SimpleMethodRenamer(spoon, probabilities);
        MethodRenamer methodRenamer;

        RenameMode renameMethodMode = config.getRenameMethodMode();
        methodRenamer = switch (renameMethodMode) {
            case ITERATIVE -> simpleMethodRenamer;
            case REALISTIC -> new RealisticMethodRenamer(spoon, probabilities, simpleMethodRenamer);
        };

        return List.of(
                new LocalVariableRenamer(spoon, probabilities),
                new FieldRenamer(spoon, probabilities),
                methodRenamer,
                new MethodInliner(spoon, probabilities),
                new OperationInserter(spoon, probabilities),
                new StarImporter(spoon, probabilities));
    }
}
