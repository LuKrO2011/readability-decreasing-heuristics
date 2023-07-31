package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for ReadabilityDecreaser-classes.
 */
public abstract class AbstractRD {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRD.class);

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
    public AbstractRD(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                      final RdcProbabilities probabilities, final Config config) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;
        this.probabilities = probabilities;
        this.config = config;
    }

    /**
     * Processes the input by performing the refactorings. Additionally, it performs pre- and post-processing.
     */
    public void process() {
        preProcess();
        decreaseReadability();
        postProcess();
    }

    /**
     * Performs pre-processing.
     */
    protected void preProcess() {
        // TODO: Execute code2vec using modelConfig here.
        LOG.info("Pre-processing not implemented.");
    }

    /**
     * Performs post-processing.
     */
    protected void postProcess() {
        LOG.info("Post-processing not implemented.");
    }

    /**
     * Processes the input.
     */
    public abstract void decreaseReadability();

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
