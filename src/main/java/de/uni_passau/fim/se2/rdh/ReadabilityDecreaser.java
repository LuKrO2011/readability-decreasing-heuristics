package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.ModelConfig;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;

import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decreases the readability of Java code.
 * <p>
 * This class is the main entry point for the Readability Decreaser. It is responsible for setting up the spoon
 * environment and calling the refactorings.
 * </p>
 */
public class ReadabilityDecreaser {

    private static final String PROBABILITIES_CONFIG_NAME = "config.yaml";
    private static final String CONFIG_NAME = "modelConfig.yaml";
    private final ProcessingPath inputDir;
    private final ProcessingPath outputDir;
    private final RdcProbabilities probabilities;

    // TODO: Use this in preprocessing
    private final ModelConfig modelConfig;
    private static final Logger LOG = LoggerFactory.getLogger(ReadabilityDecreaser.class);

    /**
     * Creates a new ReadabilityDecreaser with default config.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath) {
        this(inputDirPath, outputDirPath, PROBABILITIES_CONFIG_NAME, CONFIG_NAME);
    }

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath          the path to the input directory
     * @param outputDirPath         the path to the output directory
     * @param probabilitiesFilePath the path to the probabilities file
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                final String probabilitiesFilePath) {
        this(inputDirPath, outputDirPath, probabilitiesFilePath, CONFIG_NAME);
    }

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath          the path to the input directory
     * @param outputDirPath         the path to the output directory
     * @param probabilitiesFilePath the path to the probabilities file
     * @param configFilePath        the path to the config file
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                final String probabilitiesFilePath, final String configFilePath) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;

        // Load the configurations
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        modelConfig = yamlLoaderSaver.loadConfig(configFilePath);

        YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
        probabilities = yamlReaderWriter.loadRdcProbabilities(probabilitiesFilePath);
    }

    /**
     * Processes the given files or subdirectories.
     * <p>
     * Each file or subdirectory is treated as one unit (e.g. repository) and processed by a own
     * {@link RefactoringProcessor}.
     * </p>
     *
     * @param fileNames the files or subdirectories to process
     */
    public void process(final String... fileNames) {
        // preprocess();
        for (String fileName : fileNames) {
            RefactoringProcessor refactoringProcessor = new RefactoringProcessor(outputDir, probabilities);
            String fullyQualifiedClassName = inputDir.getAbsolutePath() + "/" + fileName;
            refactoringProcessor.process(fullyQualifiedClassName);
        }
    }

    /**
     * Preprocesses the input directory.
     * TODO: Execute code2vec using modelConfig here.
     */
    private void preprocess() {
        if (LOG.isErrorEnabled()) {
            LOG.error("Preprocessing not implemented yet. Code2vec was not executed.");
        }
    }

    /**
     * Processes the whole input directory by a single {@link RefactoringProcessor}.
     */
    public void process() {
        RefactoringProcessor refactoringProcessor = new RefactoringProcessor(outputDir, probabilities);
        String fullyQualifiedClassName = inputDir.getAbsolutePath();
        refactoringProcessor.process(fullyQualifiedClassName);
    }

}
