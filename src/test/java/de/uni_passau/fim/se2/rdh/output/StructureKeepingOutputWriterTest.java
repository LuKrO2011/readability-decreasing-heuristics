package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.AbstractRD;
import de.uni_passau.fim.se2.rdh.FileRD;
import de.uni_passau.fim.se2.rdh.FileRDFactory;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StructureKeepingOutputWriterTest extends ResourcesTest {

    private FileRDFactory rdf;

    @BeforeEach
    void setUp() {
        attachAppender(FileRD.class);
        rdf = new FileRDFactory();
    }

    @Test
    void testWriteOutput(@TempDir Path outputDir) {
        File file11Modified = new File(outputDir.toString(), "subproject1/HelloWorld.java");
        File file12Modified = new File(outputDir.toString(), "subproject1/HeapUtils.java");
        File file21Modified = new File(outputDir.toString(), "subproject2/HelloWorld.java");
        File file22Modified = new File(outputDir.toString(), "subproject2/HeapUtils.java");

        AbstractRD fileRD = rdf.create(
                ProcessingPath.directory(resourcesProcessingPath.getPath().resolve("project2")),
                ProcessingPath.directory(outputDir),
                PROBABILITIES,
                STRUCTURED_CONFIG
        );
        fileRD.decreaseReadability();

        assertAll(
                () -> assertTrue(file11Modified.exists()),
                () -> assertTrue(file12Modified.exists()),
                () -> assertTrue(file21Modified.exists()),
                () -> assertTrue(file22Modified.exists()),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testInterface(@TempDir Path outputDir) {
        File interfaceFile = new File(outputDir.toString(), "Interface.java");

        AbstractRD fileRD = rdf.create(
                ProcessingPath.directory(resourcesProcessingPath.getPath().resolve("Interface.java")),
                ProcessingPath.directory(outputDir),
                PROBABILITIES,
                STRUCTURED_CONFIG
        );
        fileRD.decreaseReadability();

        assertAll(
                () -> assertTrue(interfaceFile.exists()),
                this::assertLogIsEmpty
        );
    }
}