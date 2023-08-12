package de.uni_passau.fim.se2.rdh.refactorings;

import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

public class SpoonTest extends ResourcesTest {

    protected SpoonAPI setupSpoon(String className, Path outputDir) {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource(resources + "/" + className);
        spoon.setSourceOutputDirectory(outputDir.toString());
        spoon.buildModel();

        Environment env = spoon.getEnvironment();
        env.setAutoImports(true);
        env.setCopyResources(false);

        return spoon;
    }

    @Test
    void testNoRefactoring(@TempDir Path outputDir) {
        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);
        SpoonAPI spoon = setupSpoon(helloWorld, outputDir);

        // Create modified code file
        spoon.prettyprint();

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that no refactoring was performed
        assertThat(diffOperations).isEmpty();
    }

    @Disabled("Spoon error: Spoon changes the Vector import to com.sk89q.worldedit.Vector.")
    @Test
    void testNoRefactoringWrongImport(@TempDir Path outputDir) {
        String fileName = "WrongImport.java";

        File original = new File(resources, fileName);
        File modified = new File(outputDir.toString(), fileName);
        SpoonAPI spoon = setupSpoon(fileName, outputDir);

        // Create modified code file
        spoon.prettyprint();

        // Use the directory flattener to flatten the output dir
        DirectoryFlattener.flatten(outputDir.toFile());

        // Perform the refactoring
        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert that no refactoring was performed
        assertThat(diffOperations).isEmpty();
    }

}