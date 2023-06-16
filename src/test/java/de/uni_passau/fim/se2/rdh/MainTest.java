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
            "Usage: readability-decreasing-heuristics [-hV] [--seed=<seed>]\n" +
            "Heuristics for decreasing the readability of Java source code.\n" +
            "  -h, --help          Show this help message and exit.\n" +
            "      --seed=<seed>   A number that is used as seed to initialize the random\n" +
            "                        instance to allow for reproducible runs.\n" +
            "  -V, --version       Print version information and exit.";

    @BeforeEach
    void setup() {

    }

    @Disabled
    @Test
    void testRunAppNoSubcommand() {
        assertThat(commandLine.execute()).isZero();
        assertOutput(HELP_TEXT);
    }

    private void execute(int expectedExitCode, String... args) {
        int exitCode = commandLine.execute(args);

        assertThat(exitCode).isEqualTo(expectedExitCode);
    }
}
