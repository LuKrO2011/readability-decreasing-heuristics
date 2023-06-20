// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.IOTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest extends IOTest {

    private static final String INPUT_PATH = "src/test/resources/code/";
    private static final String INPUT_FILENAME = "HelloWorld.java";
    private static final String INPUT = INPUT_PATH + INPUT_FILENAME;

    private static final String HELP_TEXT = """
            Usage: readability-decreasing-heuristics [-hV] [-o=<outputPath>]
                   [--seed=<seed>] <inputPath>
            Heuristics for decreasing the readability of Java source code.
                  <inputPath>     The path to the input. Can be a file or a directory.
              -h, --help          Show this help message and exit.
              -o, --output=<outputPath>
                                  The path to the output directory. If not specified, the
                                    output is written into the directory of the input
                                    (file).
                  --seed=<seed>   A number that is used as seed to initialize the random
                                    instance to allow for reproducible runs.
              -V, --version       Print version information and exit.
            """;


    @Test
    void testRunWithoutArguments() {
        execute(2);
        assertErrorContains("Missing required parameter");
    }

    @Test
    void testRunHelp() {
        execute(0, "-h");
        assertOutput(HELP_TEXT);
    }

    @Test
    void testRunInputFile(@TempDir Path tmpDir) {
        String seed = "1234";
        execute(0, INPUT, "--seed", seed, "-o", tmpDir.toString());

        // Assert that file exists
        Path outputFile = tmpDir.resolve(INPUT_FILENAME);
        assertThat(outputFile).exists();
    }

    @Test
    void testRunInputDir(@TempDir Path tmpDir) {
        execute(0, INPUT_PATH, "-o", tmpDir.toString());

        // Assert that the directory is not empty
        assertThat(tmpDir).isNotEmptyDirectory();
    }

    @Test
    void testRunWrongInputFileType() {
        execute(2, INPUT_PATH + "HelloWorld.class");
        assertErrorContains("The input path must be a Java file (.java) or a directory.");
    }

    @Test
    void testGetVersion() {
        execute(0, "-V");
        assertOutput("0.0.0\n");
    }

    private void execute(int expectedExitCode, String... args) {
        int exitCode = commandLine.execute(args);
        assertThat(exitCode).isEqualTo(expectedExitCode);
    }
}
