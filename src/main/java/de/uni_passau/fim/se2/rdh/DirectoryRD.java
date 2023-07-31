package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decreases the readability of Java code. The scope of the applied refactorings is a directory.
 */
public class DirectoryRD extends AbstractRD {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryRD.class);

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities for the refactorings
     * @param config        the configuration for the tool
     */
    public DirectoryRD(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
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
    public void decreaseReadability(final String... fileNames) {
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
     * Processes the whole input directory by a single {@link RefactoringProcessor}.
     */
    public void decreaseReadability() {
        RefactoringProcessor refactoringProcessor = new RefactoringProcessor(getOutputDir(), getProbabilities());
        String fullyQualifiedClassName = getInputDir().getAbsolutePath();
        refactoringProcessor.process(fullyQualifiedClassName);

        // Log success message
        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully processed directory {}.", fullyQualifiedClassName);
        }
    }

}
