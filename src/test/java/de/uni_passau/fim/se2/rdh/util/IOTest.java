// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import de.uni_passau.fim.se2.rdh.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import picocli.CommandLine;

public abstract class IOTest {

    protected CommandLine commandLine;

    private PrintStream originalOutput;
    private PrintStream originalError;
    private InputStream originalInput;
    protected ByteArrayOutputStream newOutput;
    protected ByteArrayOutputStream newError;

    protected static final String NEWLINE = System.lineSeparator();

    @BeforeEach
    void setUp() {
        setupStdinStdout();
        setupCommandLine();
    }

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

    void setupCommandLine() {
        commandLine = new CommandLine(new Main());
    }

    @AfterEach
    void resetStdinStdout() {
        System.setOut(originalOutput);
        System.setIn(originalInput);
        System.setErr(originalError);
    }

    protected void setInput(final String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Asserts console output.
     * <p>
     * Adds the final newline to the output before checking.
     */
    protected void assertOutput(final String output) {
        assertThat(newOutput.toString(StandardCharsets.UTF_8)).isEqualTo(normalise(output) + NEWLINE);
    }

    protected void assertOutputContains(final CharSequence... values) {
        assertThat(newOutput.toString(StandardCharsets.UTF_8)).contains(values);
    }

    protected void assertErrorContains(final CharSequence... values) {
        assertThat(newError.toString(StandardCharsets.UTF_8)).contains(values);
    }

    protected void assertOutputLineCount(final int lineCount) {
        assertThat(newOutput.toString(StandardCharsets.UTF_8)).hasLineCount(lineCount);
    }

    protected void assertEmptyOutput() {
        assertThat(newOutput.toString(StandardCharsets.UTF_8)).isBlank();
    }

    private static String normalise(final String s) {
        return s.replace("\n", NEWLINE);
    }
}
