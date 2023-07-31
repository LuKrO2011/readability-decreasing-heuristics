package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DirectoryReadabilityDecreaserTest extends ResourcesTest {

    private final static Path ALL_RENAME_REFACTORINGS_CONFIG = MAIN_RESOURCES.resolve("probabilities-rename.yaml");

    private ReadabilityDecreaserFactory rdf;

    @BeforeEach
    void setUp() {
        attachAppender(DirectoryReadabilityDecreaser.class);
        rdf = new ReadabilityDecreaserFactory();
    }

    @ParameterizedTest
    @ValueSource(strings = {helloWorld, heapUtils})
    void testProcess(String fileName, @TempDir Path outputDir) {
        DirectoryReadabilityDecreaser directoryReadabilityDecreaser = rdf.createReadabilityDecreaser(
                resourcesProcessingPath,
                ProcessingPath.directory(outputDir),
                ALL_RENAME_REFACTORINGS_CONFIG
        );
        directoryReadabilityDecreaser.process(fileName);

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertFileModified(new File(resources, fileName), new File(outputDir.toString(), fileName)),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testProcessDir(@TempDir Path outputDir) {
        String file1 = helloWorld;
        String file2 = heapUtils;
        File file1Modified = new File(outputDir.toString(), file1);
        File file2Modified = new File(outputDir.toString(), file2);

        DirectoryReadabilityDecreaser directoryReadabilityDecreaser = rdf.createReadabilityDecreaser(
                resourcesProcessingPath,
                ProcessingPath.directory(outputDir)
        );
        directoryReadabilityDecreaser.process(file1, file2);

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertTrue(file1Modified.exists()),
                () -> assertTrue(file2Modified.exists()),
                this::assertLogIsEmpty
        );
    }

}