package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class ReadabilityDecreaserTest extends ResourcesTest {

    @ParameterizedTest
    @ValueSource(strings = {"HelloWorld.java", "HeapUtils.java"})
    void testProcess(String fileName, @TempDir Path outputDir) {
        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesPath, outputDir.toString());
        readabilityDecreaser.process(fileName);

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertFileModified(new File(resourcesPath, fileName), new File(outputDir.toString(), fileName));
    }

    // This test can be used to generate the output without deleting it after the test.
    @Disabled
    @Test
    void processHeapUtils() {
        String fileName = "HeapUtils.java";
        String outputPath = "output";

        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(resourcesPath, outputPath);
        readabilityDecreaser.process(fileName);

        DirectoryFlattener.flatten(new File(outputPath));
    }

    /**
     * Assert that the two files have the same functionality and different content.
     *
     * @param original     The original file.
     * @param modification The modified file.
     */
    private void assertFileModified(File original, File modification) {
        assertTrue(original.exists());
        assertTrue(modification.exists());

        // Assert that the files have the same functionality
        assertSameFunctionality(original, modification);

        // Assert that the files have different content
        assertDifferentContent(original, modification);
    }

    /**
     * Assert that the two files have the same functionality.
     *
     * @param file1 The first file.
     * @param file2 The second file.
     */
    private void assertSameFunctionality(File file1, File file2) {
        Diff diff = new AstComparator().compare(file1.getPath(), file2.getPath());
        assertThat(diff.getAllOperations()).isEmpty();
    }

    /**
     * Assert that the two files have different content.
     *
     * @param file1 The first file.
     * @param file2 The second file.
     */
    private void assertDifferentContent(File file1, File file2) {
        String file1Content = null;
        String file2Content = null;
        try {
            file1Content = Files.readString(Path.of(file1.getPath()));
            file2Content = Files.readString(Path.of(file2.getPath()));
        } catch (IOException e) {
            fail("Could not read file content.");
        }

        // Assert that the content is not the same
        assertNotEquals(file1Content, file2Content);
    }


}