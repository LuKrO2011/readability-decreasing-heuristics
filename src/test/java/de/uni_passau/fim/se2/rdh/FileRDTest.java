package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.Randomness;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileRDTest extends ResourcesTest {

    private FileRDFactory rdf;

    @BeforeEach
    void setUp() {
        attachAppender(FileRD.class);
        rdf = new FileRDFactory();
    }

    @Test
    void testProcess(@TempDir Path outputDir) {
        File file1Modified = new File(outputDir.toString(), helloWorld);
        File file2Modified = new File(outputDir.toString(), heapUtils);

        AbstractRD fileRD = rdf.create(
                ProcessingPath.directory(resourcesPath.resolve("project1")),
                ProcessingPath.directory(outputDir)
        );
        fileRD.decreaseReadability();

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertTrue(file1Modified.exists()),
                () -> assertTrue(file2Modified.exists()),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testRunAreaShop(@TempDir Path outputDir) {

        String filename = "AreaShop.java";
        File file = new File(outputDir.toString(), filename);

        AbstractRD fileRD = rdf.create(
                ProcessingPath.file(resourcesPath.resolve(filename)),
                ProcessingPath.directory(outputDir),
                Path.of("src/main/resources/probabilities-newlines.yaml")
        );
        Randomness.setSeed(1234);

        fileRD.decreaseReadability();

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertFalse(getContent(file).contains("// Load WorldEdittry")),
                this::assertLogIsEmpty
        );
    }

    @Disabled("Can be used to generate a actual output file. Requires the output file to not exist yet.")
    @Test
    void testRunAnalytics(){
        Path outputDir = Path.of("output");

        String filename = "Analytics.java";
        File file = new File(outputDir.toString(), filename);

        AbstractRD fileRD = rdf.create(
                ProcessingPath.file(resourcesPath.resolve(filename)),
                ProcessingPath.directory(outputDir),
                Path.of("src/main/resources/probabilities.yaml"),
                Path.of("src/main/resources/config-structured.yaml")
        );
        Randomness.setSeed(1234);

        fileRD.decreaseReadability();

        DirectoryFlattener.flatten(new File(outputDir.toString()));
    }

    @Test
    void testEmptyCatch(@TempDir Path outputDir) {
        String filename = "EmptyCatch.java";
        File file = new File(outputDir.toString(), filename);

        AbstractRD fileRD = rdf.create(
                ProcessingPath.file(resourcesPath.resolve(filename)),
                ProcessingPath.directory(outputDir),
                Path.of("src/main/resources/probabilities-newlines.yaml")
        );
        Randomness.setSeed(1234);

        fileRD.decreaseReadability();

        DirectoryFlattener.flatten(new File(outputDir.toString()));

        assertAll(
                () -> assertTrue(file.exists()),
                () -> assertFalse(getContent(file).contains("// Empty catch }")),
                this::assertLogIsEmpty
        );
    }

}