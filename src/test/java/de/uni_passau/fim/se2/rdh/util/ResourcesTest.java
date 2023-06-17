package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

/**
 * Base class for tests that need to access resources.
 */
public class ResourcesTest {

    protected String resourcesPath;

    @BeforeEach
    void setupResourcesPath() {
        resourcesPath = Objects.requireNonNull(getClass().getClassLoader().getResource("code")).getPath();
    }
}
