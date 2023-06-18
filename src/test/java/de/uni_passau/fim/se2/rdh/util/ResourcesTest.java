package de.uni_passau.fim.se2.rdh.util;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import gumtree.spoon.diff.operations.UpdateOperation;
import org.junit.jupiter.api.BeforeEach;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

        List<Operation> diffOperations = getDiffOperations(original, modification);

        // Assert that the files have the same functionality
        assertSameFunctionality(diffOperations);

        // Assert that the files have different content
        assertDifferentContent(diffOperations);
    }

    /**
     * Get the diff operations between the two files.
     *
     * @param original     The original file.
     * @param modification The modified file.
     * @return The diff operations between the two files.
     */
    private static List<Operation> getDiffOperations(File original, File modification) {
        Diff diff = null;
        try {
            diff = new AstComparator().compare(original, modification);
        } catch (Exception e) {
            fail("Could not compare files.");
        }
        return diff.getAllOperations();
    }

    /**
     * Assert that the two files have the same functionality. Files have the same functionality if only rename
     * operations are present.
     *
     * @param diffOperations The diff operations between the two files.
     */
    private static void assertSameFunctionality(List<Operation> diffOperations) {
        assertThat(diffOperations).allMatch(ResourcesTest::isRename);
    }

    private static boolean isRenameOfType(Operation<?> operation, Class<? extends CtElement> ctClass) {
        return operation instanceof UpdateOperation && ctClass.isInstance(operation.getSrcNode());
    }

    private static boolean isRenameMethod(Operation<?> operation) {
        return isRenameOfType(operation, CtMethod.class) || isRenameOfType(operation, CtInvocation.class);
    }

    private static boolean isRenameVariable(Operation<?> operation) {
        return isRenameOfType(operation, CtVariable.class) || isRenameOfType(operation, CtVariableRead.class) || isRenameOfType(operation, CtVariableWrite.class);
    }

    private static boolean isRenameField(Operation<?> operation) {
        return isRenameOfType(operation, CtField.class) || isRenameOfType(operation, CtFieldRead.class) || isRenameOfType(operation, CtFieldWrite.class);
    }

    private static boolean isInlineMethod(Operation<?> operation) {
        // TODO
        return true;
    }

    private static boolean isRename(Operation<?> operation) {
        return isRenameMethod(operation) || isRenameVariable(operation) || isRenameField(operation);
    }


    /**
     * Assert that the two files have different content. Files have different content if at least one diff is present.
     *
     * @param diffOperations The diff operations between the two files.
     */
    private static void assertDifferentContent(List<Operation> diffOperations) {
        assertThat(diffOperations.size()).isGreaterThan(0);
    }
}
