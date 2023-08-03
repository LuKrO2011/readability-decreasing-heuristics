package de.uni_passau.fim.se2.rdh.refactorings.rename;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.ModificationTest;
import de.uni_passau.fim.se2.rdh.refactorings.SpoonTest;
import de.uni_passau.fim.se2.rdh.util.ResourcesTest;
import gumtree.spoon.diff.operations.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import spoon.SpoonAPI;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class LocalVariableRenamerTest extends ModificationTest {

    @BeforeEach
    void setUp() {
        attachAppender(LocalVariableRenamer.class);
    }

    @Test
    void testRenameVariable(@TempDir Path outputDir) {
        List<Operation> diffOperations = applyModifications(outputDir, helloWorld);

        // Assert that only existing method was renamed
        assertAll(
                () -> assertThat(diffOperations).hasSize(1),
                () -> assertThat(diffOperations).allMatch(ResourcesTest::isRenameVariable),
                this::assertLogIsEmpty
        );
    }

    @Override
    protected AbstractModification createModification(SpoonAPI spoon) {
        RdcProbabilities rdcProbabilities = new RdcProbabilities();
        rdcProbabilities.setRenameVariable(1.0);
        return new LocalVariableRenamer(spoon, rdcProbabilities);
    }
}
