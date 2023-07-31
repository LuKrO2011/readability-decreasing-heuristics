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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Base class for tests that need to access resources.
 */
public class ResourcesTest extends LoggerTest {

    protected String resources;
    protected ProcessingPath resourcesProcessingPath;
    protected Path resourcesPath;
    protected URL resourcesURL;

    protected final String helloWorld = "HelloWorld.java";

    protected final String heapUtils = "HeapUtils.java";

    protected final String nameConflicts = "NameConflicts.java";

    protected final String staticCodeOptimizable = "StaticCodeOptimizable.java";

    protected final String outputPath = "output";

    protected final Path outputDir = Path.of(outputPath);

    protected final static Path MAIN_RESOURCES = Path.of("src/main/resources");

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
    protected static List<Operation> getDiffOperations(File original, File modification) {
        try {
            Diff diff = new AstComparator().compare(original, modification);
            return diff.getAllOperations();
        } catch (Exception e) {
            fail("Could not compare files.");
        }
        return new ArrayList<>();
    }

    /**
     * Assert that the two files have the same functionality. Files have the same functionality if only rename
     * operations are present.
     *
     * @param diffOperations The diff operations between the two files.
     */
    protected static void assertSameFunctionality(List<Operation> diffOperations) {
        assertThat(diffOperations).allMatch(ResourcesTest::isRename);
    }

    /**
     * Check if the operation is a rename operation. It is a rename operation if it is an update operation and the
     * source node is of the specified type.
     *
     * @param operation The operation to check.
     * @param ctClass   The type of the source node.
     * @return True if the operation is a rename operation, false otherwise.
     */
    protected static boolean isRenameOfType(Operation<?> operation, Class<? extends CtElement> ctClass) {
        return operation instanceof UpdateOperation && ctClass.isInstance(operation.getSrcNode());
    }

    /**
     * Check if the operation is a rename method operation. It is a rename method operation if it is an update operation
     * and the source node is of type CtMethod or CtInvocation.
     *
     * @param operation The operation to check.
     * @return True if the operation is a rename method operation, false otherwise.
     */
    protected static boolean isRenameMethod(Operation<?> operation) {
        return isRenameOfType(operation, CtMethod.class) || isRenameOfType(operation, CtInvocation.class);
    }

    /**
     * Check if the operation is a rename variable operation. It is a rename variable operation if it is an update
     * operation and the source node is of type CtVariable, CtVariableRead or CtVariableWrite.
     *
     * @param operation The operation to check.
     * @return True if the operation is a rename variable operation, false otherwise.
     */
    protected static boolean isRenameVariable(Operation<?> operation) {
        return isRenameOfType(operation, CtVariable.class) || isRenameOfType(operation, CtVariableRead.class)
                || isRenameOfType(operation, CtVariableWrite.class);
    }

    /**
     * Check if the operation is a rename field operation. It is a rename field operation if it is an update operation
     * and the source node is of type CtField, CtFieldRead or CtFieldWrite.
     *
     * @param operation The operation to check.
     * @return True if the operation is a rename field operation, false otherwise.
     */
    protected static boolean isRenameField(Operation<?> operation) {
        return isRenameOfType(operation, CtField.class) || isRenameOfType(operation, CtFieldRead.class)
                || isRenameOfType(operation, CtFieldWrite.class);
    }

    /**
     * Check if the operation is a rename operation. It is a rename operation if it is a rename method, variable or
     * field operation.
     *
     * @param operation The operation to check.
     * @return True if the operation is a rename operation, false otherwise.
     */
    protected static boolean isRename(Operation<?> operation) {
        return isRenameMethod(operation) || isRenameVariable(operation) || isRenameField(operation);
    }

    /**
     * Check if the operation is an inline method operation.
     * TODO: Implement
     */
    protected static boolean isInlineMethod(Operation<?> operation) {
        throw new IllegalStateException("Not implemented yet.");
    }

    /**
     * Assert that the two files have different content. Files have different content if at least one diff is present.
     *
     * @param diffOperations The diff operations between the two files.
     */
    protected static void assertDifferentContent(List<Operation> diffOperations) {
        assertThat(diffOperations.size()).isGreaterThan(0);
    }
}
