package de.uni_passau.fim.se2.rdh;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class ReadabilityDecreaserTest {

    @Test
    void testHelloWorld(@TempDir Path outputDir) {
        String fileName = "HelloWorld.java";
        String filePath = "src/test/resources/code";

        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(filePath, outputDir.toString());
        readabilityDecreaser.process(fileName);

        assertFileModified(new File(filePath, fileName), new File(outputDir.toString(), fileName));
    }

    /**
     * Assert that the two files have the same functionality and different content.
     * @param original The original file.
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