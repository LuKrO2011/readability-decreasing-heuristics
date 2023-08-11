package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * Builds a concrete ReadabilityDecreaser. The scope of the ReadabilityDecreaser is a file.
 */
public class FileRDFactory extends AbstractRDFactory {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRDFactory.class);

    /**
     * Creates a new concrete ReadabilityDecreaser with default config and probabilities.
     *
     * @param inputPath  the path to the input directory
     * @param outputPath the path to the output directory
     * @return the new ReadabilityDecreaser
     */
    public FileRD create(final ProcessingPath inputPath,
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
    public FileRD create(final ProcessingPath inputPath,
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
    public FileRD create(final ProcessingPath inputPath,
                         final ProcessingPath outputPath,
                         final Path probabilitiesPath,
                         final Path configPath) {
        Config config = loadConfig(configPath);
        RdcProbabilities probabilities = loadProbabilities(probabilitiesPath);
        return new FileRD(inputPath, outputPath, probabilities, config);
    }
}
