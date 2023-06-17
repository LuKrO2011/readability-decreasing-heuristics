// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.IOTest;
import de.uni_passau.fim.se2.rdh.util.Randomness;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest { // extends IOTest {

    private static final String ECHO_COMMAND_NAME = "echo";

    protected CommandLine commandLine;

    private PrintStream originalOutput;
    private PrintStream originalError;
    private InputStream originalInput;
    protected ByteArrayOutputStream newOutput;
    protected ByteArrayOutputStream newError;

    protected static final String NEWLINE = System.lineSeparator();

    @BeforeEach
    void setupStdinStdout() {
        Randomness.setSeed(0);
        originalOutput = System.out;
        newOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(newOutput, true, StandardCharsets.UTF_8));

        originalError = System.err;
        newError = new ByteArrayOutputStream();
        System.setErr(new PrintStream(newError, true, StandardCharsets.UTF_8));

        originalInput = System.in;
    }

    @BeforeEach
    void setupCommandLine() {
        commandLine = new CommandLine(new Main());
    }

    private static final String HELP_TEXT = "" +
            "Usage: readability-decreasing-heuristics [-hV] [-o=<outputPath>]\n" +
            "       [--seed=<seed>] <inputPath>\n" +
            "Heuristics for decreasing the readability of Java source code.\n" +
            "      <inputPath>     The path to the input. Can be a file or a directory.\n" +
            "  -h, --help          Show this help message and exit.\n" +
            "  -o, --output=<outputPath>\n" +
            "                      The path to the output directory. If not specified, the\n" +
            "                        output is written into the directory of the input\n" +
            "                        (file).\n" +
            "      --seed=<seed>   A number that is used as seed to initialize the random\n" +
            "                        instance to allow for reproducible runs.\n" +
            "  -V, --version       Print version information and exit.\n";


    @Test
    void testRunWithoutArguments() {
        execute(2);
    }

    @Test
    void testRunHelp() {
        execute(0, "-h");

        // Assert that the help text was printed
        assertThat(newOutput.toString(StandardCharsets.UTF_8)).isEqualTo(normalise(HELP_TEXT));
    }

    private static String normalise(final String s) {
        return s.replace("\n", NEWLINE);
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
        commandLine = new CommandLine(new Main());
        
        int exitCode = commandLine.execute(args);
        assertThat(exitCode).isEqualTo(expectedExitCode);
    }
}
