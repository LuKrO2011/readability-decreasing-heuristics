package de.uni_passau.fim.se2.rdh.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

class YamlLoaderSaverTest {

    private YamlLoaderSaver yaml;

    @BeforeEach
    void setUp() {
        yaml = new YamlLoaderSaver();
    }

    @Test
    void testLoad() {
        Object result = yaml.load();
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result instanceof RdcProbabilities));
    }

    @Test
    void testLoadDefaultConfig() {
        Object result = yaml.load("config.yaml");
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result instanceof RdcProbabilities));
    }

    @Test
    void testSave(@TempDir Path outputDir) {
        yaml.setConfigFilePath(outputDir.toString());
        yaml.save(new RdcProbabilities());
        File file = new File(outputDir + "/config.yaml");
        assertTrue(file.exists());
    }

    @Test
    void testGetDefaultConfigFilePath() {
        String path = yaml.getConfigFilePath();
        assertEquals("src/main/resources", path);
    }

    @Test
    void testInvalidConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> yaml.load("config-invalid.yaml"));
    }


}