package de.uni_passau.fim.se2.rdh.refactorings.experimental.imports;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.RenamerTest;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.StarImporter;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers.OperationInserter;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StarImporterTest extends RenamerTest {

    @Test
    void testReplaceWithStarImport(@TempDir Path outputDirT) {
        Path outputDir = Path.of("output");

        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setStarImport(1.0);
        AbstractModification parenthesesInserter = new StarImporter(spoon, rdcProbabilities);

        // Perform method renaming
        parenthesesInserter.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that three operations were performed (creation of new 0 literal, creation of new add operation, and
        // replacement of old add operation)
        /*assertAll(
            () -> assertThat(diffOperations).hasSize(3)
            // TODO: Check that the correct operations were performed
        );*/
    }

}