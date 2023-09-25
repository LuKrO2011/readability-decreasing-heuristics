package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Writes the output to the output directory and also reproduces the original directory structure in the output
 * directory.
 */
public class StructureKeepingOutputWriter extends StructuredOutputWriter implements OutputWriter {

    private static final Logger LOG = LoggerFactory.getLogger(StructuredOutputWriter.class);
    private final Path inputBasePath;
    private final Path outputBaseDir;

    /**
     * Creates a new StructureKeepingOutputWriter.
     *
     * @param spoon         the spoon instance to use
     * @param probabilities the probabilities to use
     * @param inputBasePath the base path of all input files of the used refactoring processor
     * @param outputBaseDir the base directory of the output
     */
    public StructureKeepingOutputWriter(final SpoonAPI spoon, final RdcProbabilities probabilities,
                                        final Path inputBasePath, final Path outputBaseDir) {
        super(spoon, probabilities);
        this.inputBasePath = inputBasePath;
        this.outputBaseDir = outputBaseDir;
    }

    /**
     * Creates all subdirectories in the output directory, that are needed to reproduce the original directory
     * structure. The original directory structure is reproduced by using the input directory as base directory.
     *
     * @param inputDir      the input directory
     * @param inputBaseDir  the base directory of all input files of the used refactoring processor
     * @param outputBaseDir the base directory of the output
     * @return the output directory or null if the output directory could not be created
     */
    private static Path createOutputDir(final Path inputDir, final Path inputBaseDir, final Path outputBaseDir) {
        // Get the relative path of the input directory
        Path relativePath = inputBaseDir.relativize(inputDir);

        // Calculate the output directory
        Path outputDir = Paths.get(outputBaseDir.toString(), relativePath.toString());

        // Check if the output directory already exists
        if (outputDir.toFile().exists()) {
            return outputDir;
        }

        // If the output directory does not exist, create it
        boolean success = outputDir.toFile().mkdirs();
        if (!success) {
            LOG.error("Could not create output directory: {}", outputDir);
            return null;
        }

        return outputDir;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This output writer writes the class into subfolders of the output folder to reproduce the original directory
     * structure.
     */
    protected void writeClass(final String filePath, final HashMap<String, CtClass<?>> classDictionary) {
        // Get the input directory and the file names
        Path path = Paths.get(filePath);
        Path inputDir = path.getParent();
        String inputFileName = path.getFileName().toString();

        // Get the class for the file
        CtClass<?> clazz = classDictionary.get(filePath);

        // Create the output subdirectory
        Path outputDir = createOutputDir(inputDir, inputBasePath, outputBaseDir);
        if (outputDir == null) {
            return;
        }

        // Write the output
        writeOutput(outputDir, inputFileName, clazz);
    }
}
