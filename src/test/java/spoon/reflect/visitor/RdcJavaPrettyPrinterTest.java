package spoon.reflect.visitor;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RdcJavaPrettyPrinterTest extends ResourcesTest {
    private SpoonAPI spoon;
    private RdcProbabilities probabilities;

    @BeforeEach
    void setUp() {
        setUpLogger();

        spoon = new Launcher();

        Environment env = spoon.getEnvironment();
        env.setAutoImports(true);

        probabilities = new RdcProbabilities();

        DefaultJavaPrettyPrinter prettyPrinter = new RdcJavaPrettyPrinter(env, probabilities);
        PrinterHelper printerHelper = new PrinterHelper(env);
        prettyPrinter.setPrinterTokenWriter(new RdcTokenWriter(printerHelper, probabilities));
        prettyPrinter.setIgnoreImplicit(false);
        env.setPrettyPrinterCreator(() -> prettyPrinter);

        spoon.addInputResource(resources + "/" + helloWorld);
    }

    private void setUpLogger() {
        attachAppender(RdcJavaPrettyPrinter.class);
    }

    @Test
    void testWriteFile(@TempDir Path outputDir) {
        spoon.setSourceOutputDirectory(outputDir.toString());
        spoon.buildModel();

        spoon.prettyprint();

        assertAll(
                () -> assertThat(outputDir.resolve(helloWorld)).exists(),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testAdditionalBraces(@TempDir Path outputDir) {
        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);

        probabilities.setInsertBraces(1.0);

        spoon.setSourceOutputDirectory(outputDir.toString());
        spoon.buildModel();

        spoon.prettyprint();

        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert no change in the semantics of the code
        assertAll(
                () -> assertThat(diffOperations).isEmpty(),
                this::assertLogIsEmpty
        );
    }

    @Test
    void testNewlineAfterLineComment(@TempDir Path outputDir) {
        outputDir = Path.of("output");

        File original = new File(resources, helloWorld);
        File modified = new File(outputDir.toString(), helloWorld);

        probabilities.setSpaceInsteadOfNewline(1.0);

        spoon.setSourceOutputDirectory(outputDir.toString());
        spoon.buildModel();

        spoon.prettyprint();

        List<Operation> diffOperations = getDiffOperations(original, modified);

        // Assert no change in the semantics of the code
        assertAll(
                () -> assertThat(diffOperations).isEmpty(),
                this::assertLogIsEmpty
        );
    }

}