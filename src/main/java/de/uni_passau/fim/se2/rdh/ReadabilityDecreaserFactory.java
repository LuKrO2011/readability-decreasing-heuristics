package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Builds a concrete ReadabilityDecreaser.
 */
public class ReadabilityDecreaserFactory extends AbstractReadabilityDecreaserFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ReadabilityDecreaserFactory.class);

    /**
     * Creates a new concrete ReadabilityDecreaser with default config and probabilities.
     *
     * @param inputPath  the path to the input directory
     * @param outputPath the path to the output directory
     * @return the new ReadabilityDecreaser
     */
    public ReadabilityDecreaser createReadabilityDecreaser(final ProcessingPath inputPath,
                                                           final ProcessingPath outputPath) {
        return createReadabilityDecreaser(inputPath, outputPath, DEFAULT_PROBABILITIES_FILE, DEFAULT_CONFIG_FILE);
    }

    /**
     * Creates a new concrete ReadabilityDecreaser with default config.
     *
     * @param inputPath         the path to the input directory
     * @param outputPath        the path to the output directory
     * @param probabilitiesPath the path to the probabilities file
     * @return the new ReadabilityDecreaser
     */
    public ReadabilityDecreaser createReadabilityDecreaser(final ProcessingPath inputPath,
                                                           final ProcessingPath outputPath,
                                                           final Path probabilitiesPath) {
        return createReadabilityDecreaser(inputPath, outputPath, probabilitiesPath, DEFAULT_CONFIG_FILE);
    }

    /**
     * Creates a new concrete ReadabilityDecreaser.
     *
     * @param inputPath         the path to the input directory
     * @param outputPath        the path to the output directory
     * @param probabilitiesPath the path to the probabilities file
     * @param configPath        the path to the config file
     * @return the new ReadabilityDecreaser
     */
    public ReadabilityDecreaser createReadabilityDecreaser(final ProcessingPath inputPath,
                                                           final ProcessingPath outputPath,
                                                           final Path probabilitiesPath,
                                                           final Path configPath) {
        Config config = loadConfig(configPath);
        RdcProbabilities probabilities = loadProbabilities(probabilitiesPath);
        return new ReadabilityDecreaser(inputPath, outputPath, probabilities, config);
    }

    /**
     * Load the configuration for the tool.
     *
     * @param configFilePath the path to the config file
     * @return the configuration
     */
    private Config loadConfig(final Path configFilePath) {
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        Config config;
        try {
            config = yamlLoaderSaver.loadConfig(configFilePath);
        } catch (IOException e) {
            LOG.error("Could not load config file.", e);
            LOG.warn("Using default config.");
            config = new Config();
        }
        return config;
    }

    /**
     * Load the probabilities for the refactorings.
     *
     * @param probabilitiesFilePath the path to the probabilities file
     * @return the probabilities
     */
    private RdcProbabilities loadProbabilities(final Path probabilitiesFilePath) {
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        RdcProbabilities probabilities;
        try {
            probabilities = yamlLoaderSaver.loadRdcProbabilities(probabilitiesFilePath);
        } catch (IOException e) {
            LOG.error("Could not load probabilities file.", e);
            LOG.warn("Using default probabilities.");
            probabilities = new RdcProbabilities();
        }
        return probabilities;
    }
}
