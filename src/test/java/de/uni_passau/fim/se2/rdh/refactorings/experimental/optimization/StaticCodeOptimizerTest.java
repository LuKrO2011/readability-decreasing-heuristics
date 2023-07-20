package de.uni_passau.fim.se2.rdh.refactorings.experimental.optimization;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaticCodeOptimizerTest extends SpoonTest {

    @BeforeEach
    void setUp() {
        attachAppender(PartiallyEvaluator.class);
    }

    @Test
    void testPartiallyEvaluate(@TempDir Path outputDir) {
        File original = new File(resources, staticCodeOptimizable);
        File modified = new File(outputDir.toString(), staticCodeOptimizable);
        SpoonAPI spoon = setupSpoon(staticCodeOptimizable, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setPartiallyEvaluate(1.0);
        AbstractModification renamer = new PartiallyEvaluator(spoon, rdcProbabilities);

        // Perform method renaming
        renamer.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<?> diffOperations = getDiffOperations(original, modified);

        /*
         * Assert that the third field was reassigned to the evaluated expression.
         * The changes are one insertion (new value) and three deletions (binaryOperation(read, read)).
         */
        assertAll(
                () -> assertThat(diffOperations).hasSize(4),
                () -> assertThat(diffOperations.get(0).toString()).contains("Insert"),
                () -> assertThat(diffOperations.get(1).toString()).contains("Delete"),
                () -> assertThat(diffOperations.get(2).toString()).contains("Delete"),
                () -> assertThat(diffOperations.get(3).toString()).contains("Delete"),
                this::assertLogIsEmpty
        );
    }

}