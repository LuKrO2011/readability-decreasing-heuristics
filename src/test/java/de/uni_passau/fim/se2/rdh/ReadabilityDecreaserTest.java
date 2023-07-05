package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ReadabilityDecreaserTest extends ResourcesTest {

    private final static String ALL_RENAME_REFACTORINGS_CONFIG = "config-rename.yaml";

    @ParameterizedTest
    @ValueSource(strings = {"HelloWorld.java", "HeapUtils.java"})
    void testProcess(String fileName, @TempDir Path outputDir) {
        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesProcessingPath, ProcessingPath.directory(outputDir), ALL_RENAME_REFACTORINGS_CONFIG);
        readabilityDecreaser.process(fileName);

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertFileModified(new File(resources, fileName), new File(outputDir.toString(), fileName));
    }

    // This test can be used to generate the output without deleting it after the test.
    @Disabled
    @Test
    void processHeapUtils() {
        String fileName = "HeapUtils.java";
        String outputPath = "output";

        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesProcessingPath, ProcessingPath.directory(Path.of(outputPath)), ALL_RENAME_REFACTORINGS_CONFIG);
        readabilityDecreaser.process(fileName);

        DirectoryFlattener.flatten(new File(outputPath));

        assertFileModified(new File(resources, fileName), new File(outputPath, fileName));
    }

    @Test
    void testProcessDir(@TempDir Path outputDir) {
        String file1 = "HeapUtils.java";
        String file2 = "HelloWorld.java";
        File file1Modified = new File(outputDir.toString(), "HelloWorld.java");
        File file2Modified = new File(outputDir.toString(), "HeapUtils.java");

        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesProcessingPath, ProcessingPath.directory(outputDir));
        readabilityDecreaser.process(file1, file2);

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertTrue(file1Modified.exists()),
                () -> assertTrue(file2Modified.exists())
        );
    }

    @Disabled
    @Test
    void testProcessDir() {
        String outputPath = "output";
        String file1 = "HeapUtils.java";
        String file2 = "HelloWorld.java";

        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesProcessingPath, ProcessingPath.directory(Path.of(outputPath)));
        readabilityDecreaser.process(file1, file2);

        DirectoryFlattener.flatten(new File(outputPath));

    }


}