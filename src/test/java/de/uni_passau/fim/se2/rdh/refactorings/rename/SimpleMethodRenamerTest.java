package de.uni_passau.fim.se2.rdh.refactorings.rename;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


class SimpleMethodRenamerTest extends SpoonTest {

    @BeforeEach
    void setUp() {
        log = attachAppender(SimpleMethodRenamer.class);
    }

    @Test
    void testRenameMethod(@TempDir Path outputDir) {
        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);
        SpoonAPI spoon = setupSpoon(helloWorld, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);
        AbstractModification renamer = new SimpleMethodRenamer(spoon, rdcProbabilities);

        // Perform method renaming
        renamer.apply();

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(1),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod),
                () -> assertThat(log.list).isEmpty()
        );
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
        assertThat(log.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Could not rename method m0");
    }

}
