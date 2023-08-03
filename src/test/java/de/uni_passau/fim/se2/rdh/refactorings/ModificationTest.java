package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.refactorings.rename.FieldRenamer;
import gumtree.spoon.diff.operations.Operation;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public abstract class ModificationTest extends SpoonTest {

    /**
     * Apply the {@link FieldRenamer} to the given input file and returns the {@link Operation}s that were performed.
     *
     * @param outputDir     The directory to write the modified file to.
     * @param inputFilename The name of the input file.
     * @return The {@link Operation}s that were performed.
     */
    protected List<Operation> applyModifications(Path outputDir, String inputFilename) {
        File original = new File(resources, inputFilename);
        File modified = new File(outputDir.toString(), inputFilename);
        SpoonAPI spoon = setupSpoon(inputFilename, outputDir);

        // Set up the refactoring
        AbstractModification renamer = createModification(spoon);

        // Perform method renaming
        renamer.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        return getDiffOperations(original, modified);
    }

    /**
     * Create an implementation of {@link AbstractModification} that should be tested.
     * @param spoon The {@link SpoonAPI} to use.
     * @return The {@link AbstractModification} to test.
     */
    protected abstract AbstractModification createModification(SpoonAPI spoon);
}
