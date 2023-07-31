package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.ModelConfig;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;

import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Decreases the readability of Java code.
 * <p>
 * This class is the main entry point for the Readability Decreaser. It is responsible for setting up the spoon
 * environment and calling the refactorings.
 * </p>
 */
public class ReadabilityDecreaser {

    /**
     * The default probabilities file.
     * TODO: Rename files
     */
    public static final Path DEFAULT_PROBABILITIES_FILE = Path.of("config.yaml");

    /**
     * The default config file.
     */
    public static final Path DEFAULT_CONFIG_FILE = Path.of("modelConfig.yaml");
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
        this(inputDirPath, outputDirPath, DEFAULT_PROBABILITIES_FILE, DEFAULT_CONFIG_FILE);
    }

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath          the path to the input directory
     * @param outputDirPath         the path to the output directory
     * @param probabilitiesFilePath the path to the probabilities file
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                final Path probabilitiesFilePath) {
        this(inputDirPath, outputDirPath, probabilitiesFilePath, DEFAULT_CONFIG_FILE);
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
                                final Path probabilitiesFilePath, final Path configFilePath) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;

        // Load the configuration
        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        ModelConfig loadedConfig;
        try {
            loadedConfig = yamlLoaderSaver.loadConfig(configFilePath);
        } catch (IOException e) {
            LOG.error("Could not load config file.", e);
            loadedConfig = new ModelConfig();
        }
        modelConfig = loadedConfig;

        // Load the probabilities
        RdcProbabilities loadedProbabilities;
        try {
            loadedProbabilities = yamlLoaderSaver.loadRdcProbabilities(probabilitiesFilePath);
        } catch (IOException e) {
            LOG.error("Could not load probabilities file.", e);
            loadedProbabilities = new RdcProbabilities();
        }
        probabilities = loadedProbabilities;
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

        // Log success message
        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully processed {} files.", fileNames.length);
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

        // Log success message
        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully processed directory {}.", fullyQualifiedClassName);
        }
    }

}
