package de.uni_passau.fim.se2.rdh.util;

import org.junit.jupiter.api.BeforeEach;

import java.util.Objects;

public class ResourcesTest {

    protected String resourcesPath;

    @BeforeEach
    void setupResourcesPath() {
        resourcesPath = Objects.requireNonNull(getClass().getClassLoader().getResource("code")).getPath();
    }
}
