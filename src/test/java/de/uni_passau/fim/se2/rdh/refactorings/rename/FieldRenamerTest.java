package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.ModificationTest;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class FieldRenamerTest extends ModificationTest {

    @BeforeEach
    void setUp() {
        attachAppender(FieldRenamer.class);
    }

    @Test
    void testRenameField(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, helloWorld);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(2),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameField),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testRenameFieldNameAlreadyExists() {
        // Setup spoon
        SpoonAPI spoon = setupSpoon(nameConflicts, outputDir);

        // Set up the refactoring
        AbstractModification renamer = createModification(spoon);

        // Perform method renaming
        renamer.apply();

        // Assert that the logger logged an error
        assertLogContainsExactly("Could not rename global variable f0");
    }

    @Test
    void testRenameSimpleEnum(@TempDir Path outputDir) {
        String filename = "SimpleEnum.java";

        List<Operation> diffOperations = applyModifications(outputDir, filename);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(2),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameField),
                this::assertLogIsEmpty
        );
    }


    @Test
    void testRenameComplexEnum(@TempDir Path outputDir) {
        String filename = "ComplexEnumClass.java";

        List<Operation> diffOperations = applyModifications(outputDir, filename);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(7),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameField),
                this::assertLogIsEmpty
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractModification createModification(SpoonAPI spoon) {
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameField(1.0);
        return new FieldRenamer(spoon, rdcProbabilities);
    }

}
