package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;

/**
 * Superclass for ReadabilityDecreaser-classes.
 */
public abstract class AbstractReadabilityDecreaser {

    private final ProcessingPath inputDir;

    private final ProcessingPath outputDir;

    private final RdcProbabilities probabilities;

    private final Config config;

    /**
     * Creates a new AbstractReadabilityDecreaser with default config.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities for the refactorings
     * @param config        the configuration for the tool
     */
    public AbstractReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                        final RdcProbabilities probabilities, final Config config) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;
        this.probabilities = probabilities;
        this.config = config;
    }

    /**
     * Processes the input.
     */
    public abstract void process();

    /**
     * Gets input directory that contains the files to process.
     *
     * @return the input directory
     */
    protected ProcessingPath getInputDir() {
        return inputDir;
    }

    /**
     * Gets the output directory that will contain the processed files.
     *
     * @return the output directory
     */
    protected ProcessingPath getOutputDir() {
        return outputDir;
    }

    /**
     * Gets the probabilities for the refactorings.
     *
     * @return the probabilities
     */
    protected RdcProbabilities getProbabilities() {
        return probabilities;
    }

    /**
     * Gets the configuration of the tool.
     *
     * @return the configuration
     */
    protected Config getConfig() {
        return config;
    }
}
