package de.uni_passau.fim.se2.rdh.refactorings.rename;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.refactoring.RefactoringException;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class SimpleMethodRenamerTest extends SpoonTest {

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
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameMethod)
        );
    }

    // TODO: Enable this test
    @Disabled
    @Test
    void testRenameMethodNameAlreadyExists(@TempDir Path outputDir) {
        /*Logger logger = (Logger) LoggerFactory.getLogger(SimpleMethodRenamer.class);

        // create and start a ListAppender
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();

        // add the appender to the logger
        logger.addAppender(listAppender);*/

        // Setup spoon
        SpoonAPI spoon = setupSpoon(nameConflicts, outputDir);

        // Set up the refactoring
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameMethod(1.0);
        AbstractModification renamer = new SimpleMethodRenamer(spoon, rdcProbabilities);

        // Perform method renaming
        assertThrows(RefactoringException.class, renamer::apply);
    }

}
