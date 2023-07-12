package de.uni_passau.fim.se2.rdh.refactorings.experimental.imports;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.StarImporter;
import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
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

class StarImporterTest extends SpoonTest {

    @Test
    void testReplaceWithStarImport(@TempDir Path outputDir) {
        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);
        SpoonAPI spoon = setupSpoon(helloWorld, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setStarImport(1.0);
        AbstractModification starImporter = new StarImporter(spoon, rdcProbabilities);

        // Perform method renaming
        starImporter.apply();

        // Create modified code file
        spoon.prettyprint();

        // Flatten the output directory
        DirectoryFlattener.flatten(new File(outputDir.toString()));

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        assertAll(
            () -> assertThat(diffOperations).hasSize(0)
            // TODO: Check that the correct operations were performed
        );
    }

    @Disabled
    @Test
    void testReplaceWithStarImport() {
        Path outputDir = Path.of("output");
        String sampleClass = "HeapUtils.java";

        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setStarImport(1.0);
        AbstractModification starImporter = new StarImporter(spoon, rdcProbabilities);

        // Perform method renaming
        starImporter.apply();

        // Create modified code file
        spoon.prettyprint();

        // Flatten the output directory
        DirectoryFlattener.flatten(new File(outputDir.toString()));

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        /*
        TODO: Have a look at the following changes:
            - import static Global.currentTimeMillis;
            - import org.apache.cassandra.config.DatabaseDescriptor (is processed, probably not correctly replaced
            or reintroduced by printer)
            - import org.apache.cassandra.io.util.File;
            - import org.apache.cassandra.io.util.PathUtils;
            - Double imports
            - How far can one further remove before .*
            - make sure no references are lost (see diff, e.g. java.io.File/org.apache.cassandra.io.util.File)
         */

        assertAll(
            () -> assertThat(diffOperations).hasSize(0)
            // TODO: Fix me
            // TODO: Check that the correct operations were performed
        );
    }

}