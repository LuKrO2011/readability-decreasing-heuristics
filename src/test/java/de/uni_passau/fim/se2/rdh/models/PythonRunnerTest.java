package de.uni_passau.fim.se2.rdh.models;

import de.uni_passau.fim.se2.rdh.config.Config;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class PythonRunnerTest {

    @Disabled
    @Test
    void testRun() {
        Config config = new Config();
        PythonRunner pythonRunner = new PythonRunner(config);
        pythonRunner.createMethodNamePredictions();
    }

}