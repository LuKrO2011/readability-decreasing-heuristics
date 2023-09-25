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

class SameFolderOutputWriterTest extends ResourcesTest {

    private FileRDFactory rdf;

    @BeforeEach
    void setUp() {
        attachAppender(FileRD.class);
        rdf = new FileRDFactory();
    }

    @Test
    void testWriteOutput(@TempDir Path outputDir) {
        File file1Modified = new File(outputDir.toString(), "HelloWorld_rdh.java");
        File file2Modified = new File(outputDir.toString(), "HeapUtils_rdh.java");

        // Move the files into the output directory
        copyFileIntoDirectory(resourcesPath.resolve("project1").resolve(helloWorld), outputDir);
        copyFileIntoDirectory(resourcesPath.resolve("project1").resolve(heapUtils), outputDir);

        AbstractRD fileRD = rdf.create(
                ProcessingPath.directory(outputDir),
                ProcessingPath.directory(outputDir),
                PROBABILITIES,
                SAME_DIR_CONFIG
        );
        fileRD.decreaseReadability();

        assertAll(
                () -> assertTrue(file1Modified.exists()),
                () -> assertTrue(file2Modified.exists()),
                this::assertLogIsEmpty
        );
    }
}