package de.uni_passau.fim.se2.rdh.config;

import de.uni_passau.fim.se2.rdh.AbstractReadabilityDecreaserFactory;
import de.uni_passau.fim.se2.rdh.util.LoggerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class YamlLoaderSaverTest extends LoggerTest {

    private YamlLoaderSaver yaml;

    @BeforeEach
    void setUp() {
        attachAppender(YamlLoaderSaver.class);
        yaml = new YamlLoaderSaver();
    }

    @Test
    void testLoadRdcProbabilities() {
        Object result = null;
        try {
            result = yaml.loadRdcProbabilities(AbstractReadabilityDecreaserFactory.DEFAULT_PROBABILITIES_FILE);
        } catch (IOException e) {
            fail(e);
        }

        Object finalResult = result;
        assertAll(
                () -> assertNotNull(finalResult),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testLoadModelConfig() {
        Object result = null;
        try {
            result = yaml.loadConfig(AbstractReadabilityDecreaserFactory.DEFAULT_CONFIG_FILE);
        } catch (IOException e) {
            fail(e);
        }

        Object finalResult = result;
        assertAll(
                () -> assertNotNull(finalResult),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testSave(@TempDir Path outputDir) {
        try {
            yaml.save(outputDir.resolve("probabilities.yaml"), new RdcProbabilities());
        } catch (IOException e) {
            fail(e);
        }

        File file = new File(outputDir + "/probabilities.yaml");
        assertAll(
                () -> assertTrue(file.exists()),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testInvalidConfiguration() {
        assertThrows(IOException.class, () -> yaml.loadRdcProbabilities(Path.of("probabilities-invalid.yaml")));
    }

}