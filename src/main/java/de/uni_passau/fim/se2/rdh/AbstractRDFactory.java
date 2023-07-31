package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.util.ProcessingPath;

import java.nio.file.Path;

/**
 * Superclass for ReadabilityDecreaserFactory-classes. Each factory class can build a specific ReadabilityDecreaser.
 */
public abstract class AbstractRDFactory {

    /**
     * The default resources path.
     */
    private static final Path DEFAULT_RESOURCES_PATH = Path.of("src/main/resources/");
    /**
     * The default config file.
     */
    public static final Path DEFAULT_CONFIG_FILE = DEFAULT_RESOURCES_PATH.resolve("config.yaml");
    /**
     * The default probabilities file.
     */

    public static final Path DEFAULT_PROBABILITIES_FILE = DEFAULT_RESOURCES_PATH.resolve("probabilities.yaml");

    /**
     * Creates a new specific ReadabilityDecreaser.
     *
     * @param inputPath         the path to the input directory
     * @param outputPath        the path to the output directory
     * @param probabilitiesPath the path to the probabilities file
     * @param configPath        the path to the config file
     * @return the new ReadabilityDecreaser
     */
    public abstract DirectoryRD create(ProcessingPath inputPath, ProcessingPath outputPath,
                                       Path probabilitiesPath, Path configPath);
}
