package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.ModificationTest;
import de.uni_passau.fim.se2.rdh.refactorings.rename.realistic.RealisticMethodRenamer;
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

class RealisticMethodRenamerTest extends ModificationTest {

    @BeforeEach
    void setUp() {
        attachAppender(RealisticMethodRenamer.class);
    }

    @Test
    void testRenameMethod(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, helloWorld);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(1),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testRenameAbstractClass(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, "AbstractClass.java");

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(3),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod)
                // this::assertLogIsEmpty TODO: Has warnings as the generation for the abstract class does not work yet
        );
    }

    @Test
    void testRenameInterface(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, "Interface.java");

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(3),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testRenameAnalytics(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, "Analytics.java");

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(18),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod),
                this::assertLogIsEmpty
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractModification createModification(SpoonAPI spoon) {
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);
        SimpleMethodRenamer backup = new SimpleMethodRenamer(spoon, rdcProbabilities);
        return new RealisticMethodRenamer(spoon, rdcProbabilities, backup);
    }
}
