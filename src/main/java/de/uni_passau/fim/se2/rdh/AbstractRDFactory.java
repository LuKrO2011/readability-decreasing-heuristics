package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import de.uni_passau.fim.se2.rdh.util.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryRDFactory.class);

    /**
     * Creates a new specific ReadabilityDecreaser.
     *
     * @param inputPath         the path to the input directory
     * @param outputPath        the path to the output directory
     * @param probabilitiesPath the path to the probabilities file
     * @param configPath        the path to the config file
     * @return the new ReadabilityDecreaser
     */
    public abstract AbstractRD create(ProcessingPath inputPath, ProcessingPath outputPath,
                                      Path probabilitiesPath, Path configPath);

    /**
     * Load the configuration for the tool.
     *
     * @param configFilePath the path to the config file
     * @return the configuration
     */
    protected Config loadConfig(final Path configFilePath) {
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        Config config;
        try {
            config = yamlLoaderSaver.loadConfig(configFilePath);
        } catch (IOException e) {
            LOG.error("Could not load config file.", e);
            LOG.warn("Using default config.");
            config = new Config();
        }

        // Log the loaded config
        LOG.info("Loaded config: {}", config);

        return config;
    }

    /**
     * Load the probabilities for the refactorings.
     *
     * @param probabilitiesFilePath the path to the probabilities file
     * @return the probabilities
     */
    protected RdcProbabilities loadProbabilities(final Path probabilitiesFilePath) {
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        RdcProbabilities probabilities;
        try {
            probabilities = yamlLoaderSaver.loadRdcProbabilities(probabilitiesFilePath);
        } catch (IOException e) {
            LOG.error("Could not load probabilities file.", e);
            LOG.warn("Using default probabilities.");
            probabilities = new RdcProbabilities();
        }

        // Log the loaded probabilities
        LOG.info("Loaded probabilities: {}", probabilities);

        return probabilities;
    }

    /**
     * Logs the seed that is used for the random instance.
     */
    protected void logSeed() {
        LOG.info("Using seed: {}", Randomness.getSeed());
    }
}
