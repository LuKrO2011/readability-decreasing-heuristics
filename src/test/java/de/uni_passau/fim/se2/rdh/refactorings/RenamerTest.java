package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

// TODO: The disabled test only work when executed alone.
// TODO: Conflict with other tests using a mock logger.
// TODO: Rename class as it is used in not rename refactorings as well
// @ExtendWith(MockitoExtension.class)
public class RenamerTest extends ResourcesTest {

    /*@Mock
    private static Logger mockLogger;

    @BeforeAll
    static void setUp() {
        mockStatic(LoggerFactory.class, invocation -> mockLogger);
    }

    @Disabled
    @Test
    void testRenameMethodEmpty() {
        SpoonAPI spoon = new Launcher();
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        Refactoring refactoring = new MethodRenamer(spoon, rdcProbabilities);

        refactoring.apply();
        verify(mockLogger).warn("No methods found");
    }*/

    protected SpoonAPI setupSpoon(String className, Path outputDir) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource(resources + "/" + className);
        spoon.setSourceOutputDirectory(outputDir.toString());
        spoon.buildModel();

        return spoon;
    }

    @Test
    void testNoRefactoring(@TempDir Path outputDir) {
        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that no refactoring was performed
        assertThat(diffOperations).isEmpty();
    }

    @Disabled
    @Test
    void testNoRefactoring2() {
        Path outputDir = Path.of("output");

        File original = new File(resources, sampleClass);
        File modified = new File(outputDir.toString(), sampleClass);
        SpoonAPI spoon = setupSpoon(sampleClass, outputDir);

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that no refactoring was performed
        assertThat(diffOperations).isEmpty();
    }


}