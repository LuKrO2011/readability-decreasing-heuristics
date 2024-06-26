package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.AbstractRD;
import de.uni_passau.fim.se2.rdh.FileRD;
import de.uni_passau.fim.se2.rdh.FileRDFactory;
import de.uni_passau.fim.se2.rdh.util.DirectoryFlattener;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UnstructuredOutputWriterTest extends ResourcesTest {

    private FileRDFactory rdf;

    @BeforeEach
    void setUp() {
        attachAppender(FileRD.class);
        rdf = new FileRDFactory();
    }

    @Disabled("Already tested in FileRDTest")
    @Test
    void testWriteOutput(@TempDir Path outputDir) {
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

}