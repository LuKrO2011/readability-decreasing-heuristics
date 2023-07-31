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

import static de.uni_passau.fim.se2.rdh.ReadabilityDecreaser.DEFAULT_CONFIG_FILE;
import static de.uni_passau.fim.se2.rdh.ReadabilityDecreaser.DEFAULT_PROBABILITIES_FILE;

/**
 * The main class of the tool.
 * <p>
 * This class is the entry point of the tool. It is responsible for parsing the command line arguments and executing the
 * tool.
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

    /**
     * The default output directory.
     */
    private static final String DEFAULT_OUTPUT_DIR = "output";

    /**
     * The path to the input. Can be a file or a directory.
     * TODO: Add support for console input.
     */
    @CommandLine.Parameters(
            index = "0",
            description = "The path to the input. Can be a file or a directory.")
    private String inputPath;

    /**
     * The path to the output directory. If not specified, the output is written into the default output directory
     * located in the input directory.
     */
    @CommandLine.Option(
            names = {"-o", "--output"},
            description = "The path to the output directory. If not specified, the output is written into the "
                    + "directory of the input (file)."
    )
    private String outputPath;

    /**
     * The path to the configuration file of the tool.
     */
    @CommandLine.Option(
            names = {"-c", "--config"},
            description = "The path to the configuration file of the tool. If not specified, the default "
                    + "configuration is used."
    )
    private String configPath;

    /**
     * The path to the configuration file for the probability distributions of the refactorings.
     */
    @CommandLine.Option(
            names = {"-p", "--probs", "--probabilities"},
            description = "The path to the configuration file for the probability distributions of the refactorings. "
                    + "If not specified, the default configuration is used."
    )
    private String probabilitiesPath;


    /**
     * The command line specification. This is used to print messages to the user.
     */
    @CommandLine.Spec
    private CommandLine.Model.CommandSpec spec;

    /**
     * Sets the seed for the random instance.
     * TODO: Use the provided seed in RdcProbabilities.
     *
     * @param seed The seed.
     */
    @CommandLine.Option(
            names = {"--seed"},
            description = "A number that is used as seed to initialize the random instance to allow for reproducible "
                    + "runs."
    )
    public void setSeed(final int seed) {
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
        ReadabilityDecreaser readabilityDecreaser =
                new ReadabilityDecreaser(getInputPath(), getOutputPath(), getProbabilitiesPath(), getConfigPath());
        readabilityDecreaser.process();
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

    /**
     * Returns the path to the configuration file for the probability distributions of the refactorings. If no path is
     * specified, the default configuration is used.
     *
     * @return The path to the configuration file for the probability distributions of the refactorings.
     */
    private Path getProbabilitiesPath() {
        if (probabilitiesPath == null) {
            return DEFAULT_PROBABILITIES_FILE;
        }

        // TODO: Check if the file exists...

        return Path.of(probabilitiesPath);
    }

    /**
     * Returns the path to the configuration file of the tool. If no path is specified, the default configuration is
     * used.
     *
     * @return The path to the configuration file of the tool.
     */
    private Path getConfigPath() {
        if (configPath == null) {
            return DEFAULT_CONFIG_FILE;
        }

        // TODO: Check if the file exists...

        return Path.of(configPath);
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
