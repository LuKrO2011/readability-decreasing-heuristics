// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.IOTest;
import org.junit.jupiter.api.BeforeEach;
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

    private static final String HELP_TEST = "" +
        "Usage: readability-decreasing-heuristics [-hV] [--seed=<seed>]\n" +
        "Heuristics for decreasing the readability of Java source code.\n" +
        "  -h, --help          Show this help message and exit.\n" +
        "      --seed=<seed>   A number that is used as seed to initialize the random\n" +
        "                        instance to allow for reproducible runs.\n" +
        "  -V, --version       Print version information and exit.\n" +
        "";

    @BeforeEach
    void setup() {

    }

    @Test
    void testRunAppNoSubcommand() {
        assertThat(commandLine.execute()).isZero();
        assertOutputContains(HELP_TEST);
    }

    @Test
    void testRunConsoleInputConsoleOutput() {
        String input = joined("line1", "line2");
        setInput(input);

        execute(0, ECHO_COMMAND_NAME);

        assertThat(newOutput).hasToString(input);
    }

    @Test
    void testRunFileInputConsoleOutput(@TempDir Path tmpDir) throws Exception {
        Path file = tmpDir.resolve("testfile.txt");
        String fileContent = "file content";
        Files.writeString(file, fileContent, StandardCharsets.UTF_8);

        execute(0, ECHO_COMMAND_NAME, "-s", file.toString());

        assertOutput(fileContent);
    }

    @Test
    void testRunDirectoryInputConsoleOutput(@TempDir Path tmpDir) throws Exception {
        Files.createDirectories(tmpDir.resolve("someDir"));
        Path file1 = tmpDir.resolve("testfile.txt");
        Path file2 = tmpDir.resolve("someDir").resolve("testfile2.txt");

        String fileContent1 = "file content1";
        Files.writeString(file1, fileContent1, StandardCharsets.UTF_8);
        String fileContent2 = "file content2";
        Files.writeString(file2, fileContent2, StandardCharsets.UTF_8);

        execute(0, ECHO_COMMAND_NAME, "-s", tmpDir.toString());

        assertOutputContains(fileContent1, fileContent2);
    }

    private String joined(String... lines) {
        return Arrays.stream(lines)
            .collect(Collectors.joining(System.lineSeparator(), "", System.lineSeparator()));
    }

    private void execute(int expectedExitCode, String... args) {
        int exitCode = commandLine.execute(args);

        assertThat(exitCode).isEqualTo(expectedExitCode);
    }
}
