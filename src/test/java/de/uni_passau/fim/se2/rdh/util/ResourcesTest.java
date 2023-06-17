package de.uni_passau.fim.se2.rdh.util;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for tests that need to access resources.
 */
public class ResourcesTest {

    protected String resources;
    protected ProcessingPath resourcesProcessingPath;
    protected Path resourcesPath;
    protected URL resourcesURL;

    protected String sampleClass = "HelloWorld.java";

    @BeforeEach
    void setupResourcesPath() {
        resourcesURL = Objects.requireNonNull(getClass().getClassLoader().getResource("code"));
        try {
            resourcesPath = Paths.get(resourcesURL.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        resourcesProcessingPath = ProcessingPath.directory(resourcesPath);
        resources = resourcesPath.toString();
    }

    /**
     * Assert that the two files have the same functionality and different content.
     *
     * @param original     The original file.
     * @param modification The modified file.
     */
    protected static void assertFileModified(File original, File modification) {
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
    private static void assertSameFunctionality(File file1, File file2) {
        Diff diff = new AstComparator().compare(file1.getPath(), file2.getPath());
        assertThat(diff.getAllOperations()).isEmpty();
    }

    /**
     * Assert that the two files have different content.
     * TODO: The content is different even with no modification due to the different "default" formatting.
     *
     * @param file1 The first file.
     * @param file2 The second file.
     */
    private static void assertDifferentContent(File file1, File file2) {
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
