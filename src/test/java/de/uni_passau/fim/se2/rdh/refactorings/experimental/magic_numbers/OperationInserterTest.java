package de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OperationInserterTest extends SpoonTest {

    @Test
    void testAdd0(@TempDir Path outputDir) {

        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);
        SpoonAPI spoon = setupSpoon(helloWorld, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setAdd0(1.0);
        AbstractModification parenthesesInserter = new OperationInserter(spoon, rdcProbabilities);

        // Perform method renaming
        parenthesesInserter.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that three operations were performed (creation of new 0 literal, creation of new add operation, and
        // replacement of old add operation)
        assertAll(
            () -> assertThat(diffOperations).hasSize(3)
            // TODO: Check that the correct operations were performed
        );
    }
}