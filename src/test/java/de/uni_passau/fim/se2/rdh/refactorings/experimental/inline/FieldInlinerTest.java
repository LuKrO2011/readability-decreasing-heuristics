package de.uni_passau.fim.se2.rdh.refactorings.experimental.inline;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.StarImporter;
import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FieldInlinerTest extends SpoonTest {

    @Test
    void testInlineField(@TempDir Path outputDir) {
        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setInlineField(1.0);
        AbstractModification fieldInliner = new FieldInliner(spoon, rdcProbabilities);

        // Perform method renaming
        fieldInliner.apply();

        // Create modified code file
        spoon.prettyprint();

        // Flatten the output directory
        DirectoryFlattener.flatten(new File(outputDir.toString()));

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        assertAll(
                () -> assertThat(diffOperations).hasSize(2)
                // TODO: Check that the correct operations were performed
        );
    }

    @Disabled
    @Test
    void testInlineField() {
        Path outputDir = Path.of("output");

        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setInlineField(1.0);
        AbstractModification fieldInliner = new FieldInliner(spoon, rdcProbabilities);

        // Perform method renaming
        fieldInliner.apply();

        // Create modified code file
        spoon.prettyprint();

        // Flatten the output directory
        DirectoryFlattener.flatten(new File(outputDir.toString()));

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);
    }

}