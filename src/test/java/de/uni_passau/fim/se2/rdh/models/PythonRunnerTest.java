package de.uni_passau.fim.se2.rdh.models;

import de.uni_passau.fim.se2.rdh.config.ModelConfig;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PythonRunnerTest {

    @Disabled
    @Test
    void testRun() {
        ModelConfig modelConfig = new ModelConfig();
        PythonRunner pythonRunner = new PythonRunner(modelConfig);
        pythonRunner.createMethodNamePredictions();
    }

}