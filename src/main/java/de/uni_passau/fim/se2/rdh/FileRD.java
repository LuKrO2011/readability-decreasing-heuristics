package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Decreases the readability of Java code. The scope of the applied refactorings is a file.
 */
public class FileRD extends AbstractRD {

    private static final Logger LOG = LoggerFactory.getLogger(FileRD.class);

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities for the refactorings
     * @param config        the configuration for the tool
     */
    public FileRD(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                  final RdcProbabilities probabilities, final Config config) {
        super(inputDirPath, outputDirPath, probabilities, config);
    }

    /**
     * Processes each file in the input directory one by one. In other words, for each file an own
     * {@link RefactoringProcessor} is used.
     */
    public void decreaseReadability() {
        Stream<Path> javaFiles = getJavaFiles(getInputDir().getPath());
        javaFiles.forEach(file -> {
            LOG.info("Processing file {} to {}.", file, getOutputDir().getPath());
            RefactoringProcessor refactoringProcessor =
                    new RefactoringProcessor(ProcessingPath.directory(getOutputDir().getPath()), getProbabilities());
            refactoringProcessor.process(file.toString());
        });

        // Log success message
        if (LOG.isInfoEnabled()) {
            LOG.info("Successfully processed directory {} file by file.", getInputDir().getPath());
        }
    }

    /**
     * Gets all Java files in the input directory. Therefore, all subdirectories are searched recursively.
     *
     * @param inputDir the input directory
     * @return all Java files in the input directory
     */
    private Stream<Path> getJavaFiles(final Path inputDir) {
        Stream<Path> javaFileStream = null;
        try {
            javaFileStream = Files.walk(inputDir)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"));
        } catch (IOException e) {
            if (FileRD.LOG.isErrorEnabled()) {
                FileRD.LOG.error("Could not get Java files in input directory: {}", e.getMessage(),
                        e);
            }
        }
        return javaFileStream;
    }

}

