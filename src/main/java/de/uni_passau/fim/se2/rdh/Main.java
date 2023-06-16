// SPDX-FileCopyrightText: 2023 Preprocessing Toolbox Contributors
//
// SPDX-License-Identifier: EUPL-1.2

package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.Randomness;
import picocli.CommandLine;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Callable;

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
        names = { "-o", "--output" },
        description = "The path to the output directory. If not specified, the output is written into the directory of the input (file)."
    )
    private String outputPath;

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @CommandLine.Option(
        names = { "--seed" },
        description = "A number that is used as seed to initialize the random instance to allow for reproducible runs."
    )
    @SuppressWarnings("unused")
    void setSeed(int seed) {
        Randomness.setSeed(seed);
    }

    public static void main(final String[] args) {
        final int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        ReadabilityDecreaser readabilityDecreaser = new ReadabilityDecreaser(inputPath, outputPath);
        readabilityDecreaser.process();
        // spec.commandLine().usage(System.out);
        return 0;
    }

    static class VersionProvider implements CommandLine.IVersionProvider {

        @Override
        public String[] getVersion() throws Exception {
            final URL resource = getClass().getResource("/version.txt");
            if (resource == null) {
                return new String[] { "0.0.0" };
            }

            final Properties properties = new Properties();
            try (var input = resource.openStream()) {
                properties.load(input);
                return new String[] {
                    properties.get("ApplicationName") + " " + properties.getProperty("Version")
                };
            }
        }
    }
}
