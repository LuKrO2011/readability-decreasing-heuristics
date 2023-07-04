package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.refactorings.rename.realistic.RealisticMethodRenamer;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RealisticMethodRenamerTest extends SpoonTest {

    @Test
    void testRenameMethod(@TempDir Path outputDir) {
        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);

        MethodRenamer backup = new SimpleMethodRenamer(spoon, rdcProbabilities);
        MethodRenamer renamer = new RealisticMethodRenamer(spoon, rdcProbabilities, backup);

        // Perform method renaming
        renamer.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(1),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod)
        );
    }

}
