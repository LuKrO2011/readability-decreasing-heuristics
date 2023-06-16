// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.IOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest extends IOTest {

    private static final String ECHO_COMMAND_NAME = "echo";

    private static final String HELP_TEXT = "" +
            "Usage: readability-decreasing-heuristics [-hV] [-o, --output=<outputPath>]\n" +
            "       [--seed=<seed>] <inputPath>\n" +
            "Heuristics for decreasing the readability of Java source code.\n" +
            "      <inputPath>     The path to the input. Can be a file or a directory.\n" +
            "  -h, --help          Show this help message and exit.\n" +
            "      -o, --output=<outputPath>\n" +
            "                      The path to the output directory. If not specified, the\n" +
            "                        output is written into the directory of the input\n" +
            "                        (file).\n" +
            "      --seed=<seed>   A number that is used as seed to initialize the random\n" +
            "                        instance to allow for reproducible runs.\n" +
            "  -V, --version       Print version information and exit.";


    @Test
    void testRunWithoutArguments() {
        execute(2);
    }

    @Test
    void testRunHelp() {
        execute(0, "-h");

        // Assert that the help text was printed
        // assertOutputContains(HELP_TEXT);
    }

    @Test
    void testRunInputFile(@TempDir Path tmpDir) {
        String inputPath = "src/test/resources/code/";
        String fileName = "HelloWorld.java";

        execute(0, inputPath + fileName, "-o", tmpDir.toString());

        // Assert that file exists
        Path outputFile = tmpDir.resolve(fileName);
        assertThat(outputFile).exists();
    }

    @Test
    void testRunInputDir(@TempDir Path tmpDir) {
        String inputPath = "src/test/resources/";
        String dirName = "code";

        execute(0, inputPath + dirName, "-o", tmpDir.toString());

        // Assert that the directory is not empty
        assertThat(tmpDir).isNotEmptyDirectory();
    }

    private void execute(int expectedExitCode, String... args) {
        int exitCode = commandLine.execute(args);
        assertThat(exitCode).isEqualTo(expectedExitCode);
    }
}
