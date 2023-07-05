package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RefactoringProcessorTest extends ResourcesTest {

    @Test
    void testDisplay() {
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        RefactoringProcessor readabilityDecreaser = new RefactoringProcessor(resourcesProcessingPath, rdcProbabilities);

        readabilityDecreaser.display();
    }

}