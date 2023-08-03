package de.uni_passau.fim.se2.rdh.refactorings.rename;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.ModificationTest;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;


class SimpleMethodRenamerTest extends ModificationTest {

    @BeforeEach
    void setUp() {
        attachAppender(SimpleMethodRenamer.class);
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
    void testRenameMethod2(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, heapUtils);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(11),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testRenameMethodEmpty() {
        // Set up spoon
        SpoonAPI spoon = new Launcher();
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        AbstractModification refactoring = new SimpleMethodRenamer(spoon, rdcProbabilities);

        // Perform method renaming
        refactoring.apply();

        // Assert that the logger logged an error
        assertLogContainsExactly("No methods found");
    }


    @Test
    void testRenameMethodNameAlreadyExists(@TempDir Path outputDir) {
        // Setup spoon
        SpoonAPI spoon = setupSpoon(nameConflicts, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);
        AbstractModification renamer = new SimpleMethodRenamer(spoon, rdcProbabilities);

        // Perform method renaming
        renamer.apply();

        // Assert that the logger logged an error
        assertLogContainsExactly("Could not rename method m0");
    }

    @Override
    protected AbstractModification createModification(SpoonAPI spoon) {
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);
        return new SimpleMethodRenamer(spoon, rdcProbabilities);
    }
}
