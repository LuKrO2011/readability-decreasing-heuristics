// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.FileManager;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.Randomness;
import picocli.CommandLine;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;
import java.util.concurrent.Callable;

import static de.uni_passau.fim.se2.rdh.ReadabilityDecreaser.DEFAULT_OUTPUT_DIR;

/**
 * The main class of the tool.
 * <p>
 * This class is the entry point of the tool. It is responsible for parsing the command line arguments and
 * executing the tool.
 * </p>
 */
@CommandLine.Command(
        name = "readability-decreasing-heuristics",
        mixinStandardHelpOptions = true,
        showDefaultValues = true,
        description = "Heuristics for decreasing the readability of Java source code.",
        versionProvider = Main.VersionProvider.class
)
public final class Main implements Callable<Integer> {

    @CommandLine.Parameters(
            index = "0",
            description = "The path to the input. Can be a file or a directory.")
    private String inputPath;

    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "The path to the output directory. If not specified, the output is written into the directory of the input (file)."
    )
    private String outputPath;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
            names = {"--seed"},
            description = "A number that is used as seed to initialize the random instance to allow for reproducible runs."
    )
    @SuppressWarnings("unused")
    void setSeed(int seed) {
        Randomness.setSeed(seed);
    }

    /**
     * The entry point of the tool.
     *
     * @param args The command line arguments. See the help for more information.
     */
    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Executes the tool. This method is called by the picocli library.
     *
     * @return The exit code.
     */
    @Override
    public Integer call() {
        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(getInputPath(), getOutputPath());
        readabilityDecreaser.process();
        // spec.commandLine().usage(System.out);
        return 0;
    }

    private ProcessingPath getInputPath() {
        if (inputPath == null) {
            return ProcessingPath.console();
        }

        if (inputPath.endsWith(".java")) {
            return ProcessingPath.file(Path.of(inputPath));
        } else {
            File file = new File(inputPath);
            if (!file.isDirectory()) {
                throw new CommandLine.ParameterException(spec.commandLine(),
                        "The input path must be a Java file (.java) or a directory.");
            } else { // The input path is a directory
                return ProcessingPath.directory(Path.of(inputPath));
            }
        }
    }

    private ProcessingPath getOutputPath() {
        if (outputPath == null) {
            return ProcessingPath.directory(Path.of(DEFAULT_OUTPUT_DIR));
        }

        File outputDir = FileManager.createFolder(outputPath);
        return ProcessingPath.directory(outputDir.toPath());
    }

    static class VersionProvider implements CommandLine.IVersionProvider {

        @Override
        public String[] getVersion() throws Exception {
            final URL resource = getClass().getResource("/version.txt");
            if (resource == null) {
                return new String[]{"0.0.0"};
            }

            final Properties properties = new Properties();
            try (var input = resource.openStream()) {
                properties.load(input);
                return new String[]{
                        properties.get("ApplicationName") + " " + properties.getProperty("Version")
                };
            }
        }
    }
}
