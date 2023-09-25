package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Writes the output to the same folder as the input files with a different file name.
 */
public class SameFolderOutputWriter extends StructuredOutputWriter implements OutputWriter {

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
    protected void writeClass(final String fileName, final HashMap<String, CtClass<?>> classDictionary) {
        // Get the input directory and the file names
        Path path = Paths.get(fileName);
        Path inputDir = path.getParent();
        String inputFileName = path.getFileName().toString();

        String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + RDH_FILE_EXTENSION;

        // Get the class for the file
        CtClass<?> clazz = classDictionary.get(fileName);

        // Write the output
        writeOutput(inputDir, outputFileName, clazz);
    }
}
