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
 * Writes the output to the same folder as the input files with a different file name.
 */
public class SameFolderOutputWriter extends StructuredOutputWriter implements OutputWriter {

    private static final Logger LOG = LoggerFactory.getLogger(StructuredOutputWriter.class);

    private static final String RDH_FILE_EXTENSION = "_rdh.java";

    /**
     * Creates a new SameFolderOutputWriter.
     *
     * @param spoon         the spoon instance to use
     * @param probabilities the probabilities to use
     */
    public SameFolderOutputWriter(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        super(spoon, probabilities);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This output writer writes the class to the same folder as the input files with a different file name.
     */
    protected void writeClass(final String filePath, final HashMap<String, CtClass<?>> classDictionary) {
        // Get the input directory and the file names
        Path path = Paths.get(filePath);
        Path inputDir = path.getParent();
        Path inputFileNameP = path.getFileName();
        String inputFileName;
        if (inputFileNameP == null) {
            LOG.warn("Could not get file name for file {}. The file was not saved.", filePath);
            return;
        } else {
            inputFileName = inputFileNameP.toString();
        }

        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + RDH_FILE_EXTENSION;

        // Get the class for the file
        CtClass<?> clazz = classDictionary.get(filePath);

        if (clazz == null) {
            LOG.warn("Could not find class for file {}. The file was not saved.", filePath);
            return;
        }

        // Write the output
        writeOutput(inputDir, outputFileName, clazz);
    }
}
