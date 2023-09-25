package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.RenameMode;
import de.uni_passau.fim.se2.rdh.models.PythonRunner;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

    private final RefactoringProcessorBuilder rpBuilder;

    /**
     * Creates a new AbstractReadabilityDecreaser with default config.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities for the refactorings
     * @param config        the configuration for the tool
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The probabilities can be changed by the user at runtime
    public AbstractRD(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                      final RdcProbabilities probabilities, final Config config) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;
        this.probabilities = probabilities;
        this.config = new Config(config);
        this.rpBuilder = new RefactoringProcessorBuilder(config);
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
        if (config.getRenameMethodMode() == RenameMode.REALISTIC) {
            LOG.info("Pre-processing started.");
            PythonRunner pythonRunner = new PythonRunner(config);
            pythonRunner.createMethodNamePredictions(inputDir.getPath());
            LOG.info("Pre-processing finished.");
        }
        LOG.info("No Pre-processing needed.");
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

    /**
     * Gets the {@link RefactoringProcessorBuilder} that is used to create a {@link RefactoringProcessor}.
     *
     * @return the {@link RefactoringProcessorBuilder}
     */
    public RefactoringProcessorBuilder getRpBuilder() {
        return rpBuilder;
    }

    /**
     * Creates a new {@link RefactoringProcessor} with the given parameters.
     *
     * @return the created {@link RefactoringProcessor}
     */
    protected RefactoringProcessor createRefactoringProcessor() {
        return rpBuilder.create(probabilities);
    }
}
