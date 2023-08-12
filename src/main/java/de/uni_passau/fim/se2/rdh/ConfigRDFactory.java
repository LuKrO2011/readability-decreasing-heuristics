package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.Scope;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

import static de.uni_passau.fim.se2.rdh.config.Scope.DIRECTORY;
import static de.uni_passau.fim.se2.rdh.config.Scope.FILE;

/**
 * Builds a concrete ReadabilityDecreaser. The scope of the ReadabilityDecreaser depends on the config.
 * TODO: Delete other Factories?
 */
public class ConfigRDFactory extends AbstractRDFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigRDFactory.class);

    private static final Scope DEFAULT_SCOPE = FILE;

    /**
     * Creates a new concrete ReadabilityDecreaser with default config and probabilities.
     *
     * @param inputPath  the path to the input directory
     * @param outputPath the path to the output directory
     * @return the new ReadabilityDecreaser
     */
    public AbstractRD create(final ProcessingPath inputPath,
                             final ProcessingPath outputPath) {
        return create(inputPath, outputPath, DEFAULT_PROBABILITIES_FILE, DEFAULT_CONFIG_FILE);
    }

    /**
     * Creates a new concrete ReadabilityDecreaser with default config.
     *
     * @param inputPath         the path to the input directory
     * @param outputPath        the path to the output directory
     * @param probabilitiesPath the path to the probabilities file
     * @return the new ReadabilityDecreaser
     */
    public AbstractRD create(final ProcessingPath inputPath,
                             final ProcessingPath outputPath,
                             final Path probabilitiesPath) {
        return create(inputPath, outputPath, probabilitiesPath, DEFAULT_CONFIG_FILE);
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
    public AbstractRD create(final ProcessingPath inputPath,
                             final ProcessingPath outputPath,
                             final Path probabilitiesPath,
                             final Path configPath) {
        logSeed();
        Config config = loadConfig(configPath);
        RdcProbabilities probabilities = loadProbabilities(probabilitiesPath);

        // Check if scope is set in config
        if (config.getScope() == null) {
            LOG.warn("Scope is null. Using default scope: {}", DEFAULT_SCOPE);
            config.setScope(DEFAULT_SCOPE);
        }

        // Choose the correct RD depending on the scope in the config
        return switch (config.getScope()) {
            case FILE -> new FileRD(inputPath, outputPath, probabilities, config);
            case DIRECTORY -> new DirectoryRD(inputPath, outputPath, probabilities, config);
        };
    }
}
