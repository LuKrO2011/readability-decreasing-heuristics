package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
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
public class DirectoryReadabilityDecreaser extends AbstractReadabilityDecreaser {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryReadabilityDecreaser.class);

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities for the refactorings
     * @param config        the configuration for the tool
     */
    public DirectoryReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                         final RdcProbabilities probabilities, final Config config) {
        super(inputDirPath, outputDirPath, probabilities, config);
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
    @Deprecated
    public void process(final String... fileNames) {
        // preprocess();
        for (String fileName : fileNames) {
            RefactoringProcessor refactoringProcessor = new RefactoringProcessor(getOutputDir(), getProbabilities());
            String fullyQualifiedClassName = getInputDir().getAbsolutePath() + "/" + fileName;
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
        if (DirectoryReadabilityDecreaser.LOG.isErrorEnabled()) {
            DirectoryReadabilityDecreaser.LOG.error("Preprocessing not implemented yet. Code2vec was not executed.");
        }
    }

    /**
     * Processes the whole input directory by a single {@link RefactoringProcessor}.
     */
    public void process() {
        // preprocess();
        RefactoringProcessor refactoringProcessor = new RefactoringProcessor(getOutputDir(), getProbabilities());
        String fullyQualifiedClassName = getInputDir().getAbsolutePath();
        refactoringProcessor.process(fullyQualifiedClassName);

        // Log success message
        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully processed directory {}.", fullyQualifiedClassName);
        }
    }

}
