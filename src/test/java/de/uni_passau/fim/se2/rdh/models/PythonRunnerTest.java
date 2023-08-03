package de.uni_passau.fim.se2.rdh.models;

import de.uni_passau.fim.se2.rdh.AbstractRDFactory;
import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.util.FileManager;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PythonRunnerTest extends ResourcesTest {

    @BeforeEach
    void setUp() {
        attachAppender(PythonRunner.class);
    }

    @Disabled("Takes a lot of time because a model (code2vec) is loaded.")
    @Test
    void testCreateMethodNamePredictions(@TempDir Path outputDir) {
        Path inputPath = resourcesPath.resolve("project1");
        File outputFile1 = new File(outputDir.toString(), "HelloWorld.json");
        File outputFile2 = new File(outputDir.toString(), "HeapUtils.json");

        // Load config
        Config config = null;
        try {
            config = new YamlLoaderSaver().loadConfig(AbstractRDFactory.DEFAULT_CONFIG_FILE);
        } catch (IOException e) {
            fail(e);
        }

        // Create predictions
        PythonRunner pythonRunner = new PythonRunner(config);
        pythonRunner.createMethodNamePredictions(inputPath);

        // Move files to output directory
        try {
            FileManager.moveFiles(inputPath, outputDir, outputFile1, outputFile2);
        } catch (IOException e) {
            fail(e);
        }

        // Assert that files exist
        assertAll(
                () -> assertTrue(outputFile1.exists()),
                () -> assertTrue(outputFile2.exists()),
                this::assertLogIsEmpty
        );
    }

}