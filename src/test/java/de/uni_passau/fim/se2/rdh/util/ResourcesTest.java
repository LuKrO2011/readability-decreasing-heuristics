package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.BeforeEach;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Base class for tests that need to access resources.
 */
public class ResourcesTest {

    protected String resources;
    protected ProcessingPath resourcesProcessingPath;
    protected Path resourcesPath;
    protected URL resourcesURL;

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
}
