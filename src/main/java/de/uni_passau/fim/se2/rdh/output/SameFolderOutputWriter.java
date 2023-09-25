package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.RefactoringProcessor;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.RdcJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SameFolderOutputWriter extends AbstractOutputWriter {

    private static final Logger LOG = LoggerFactory.getLogger(RefactoringProcessor.class);
    private final SpoonAPI spoon;

    private final RdcProbabilities probabilities;

    private static final String RDH_FILE_EXTENSION = "_rdh.java";

    private String[] input;

    /**
     * Creates a new SameFolderOutputWriter.
     *
     * @param spoon         the spoon instance to use
     * @param probabilities the probabilities to use
     */
    public SameFolderOutputWriter(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer. The output is written to the given
     * directory with the given file name.
     *
     * @param outputDirForFile the directory to write the output to
     * @param fileName         the name of the file
     * @param clazz            the class to write
     */
    private void writeOutput(final Path outputDirForFile, final String fileName, final CtClass<?> clazz) {
        boolean validPath = validatePath(outputDirForFile);
        boolean validFileName = validateFileName(outputDirForFile, fileName);
        boolean validClass = validateClass(clazz);

        // If the path, file name or class is invalid, return
        if (!validPath || !validFileName || !validClass) {
            return;
        }

        // Get the printer
        Environment env = spoon.getEnvironment();
        RdcJavaPrettyPrinter printer = new RdcJavaPrettyPrinter(env, probabilities);

        // Convert the model to java code
        String javaCode = printer.prettyprint(clazz);

        // Write the java code to the output file
        Path path = Paths.get(outputDirForFile.toString(), fileName);
        try (FileWriter writer = new FileWriter(path.toString())) {
            writer.write(javaCode);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not write output file: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Validate the given path. The path must exist and must be a directory.
     *
     * @param path the path to validate
     * @return true if the path is valid, false otherwise
     */
    private static boolean validatePath(final Path path) {
        if (!path.toFile().exists() || !path.toFile().isDirectory()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Output directory does not exist: {}", path);
            }
            return false;
        }
        return true;
    }

    /**
     * Validate the given file name. The file name must not be empty, must not be null, must not be a directory and must
     * not already exist.
     *
     * @param path     the path to the file
     * @param fileName the file name
     * @return true if the file name is valid, false otherwise
     */
    private static boolean validateFileName(final Path path, final String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File name is invalid: {}", fileName);
            }
            return false;
        }
        if (Paths.get(fileName).toFile().isDirectory()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File name is a directory: {}", fileName);
            }
            return false;
        }
        if (Paths.get(path.toString(), fileName).toFile().exists()) {
            if (LOG.isErrorEnabled()) {
                LOG.error("File already exists: {}", fileName);
            }
            return false;
        }
        return true;
    }

    /**
     * Validate the given class. The class must not be null.
     *
     * @param clazz the class to validate
     * @return true if the class is valid, false otherwise
     */
    private static boolean validateClass(final CtClass<?> clazz) {
        if (clazz == null) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Class is null.");
            }
            return false;
        }
        return true;
    }

    /**
     * Add the input file names.
     *
     * @param inputs the input file names
     */
    public void addInputs(final String... inputs) {
        this.input = inputs;
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer.
     */
    @Override
    public void writeOutput() {
        // Get all files of the processed model
        List<CtClass<?>> classes = spoon.getModel().getElements(new TypeFilter<>(CtClass.class));

        // Match the input files to spoon classes
        HashMap<String, CtClass<?>> classDictionary = matchInputs(classes, List.of(input));

        // Write the output to the directory of the input file
        for (String fileName : input) {

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

    /**
     * Match the input file names to the spoon model files and store the matches in a dictionary.
     *
     * @param spoonClasses the spoon classes
     * @param fileNames    the file names
     * @return the dictionary
     */
    private HashMap<String, CtClass<?>> matchInputs(final List<CtClass<?>> spoonClasses, final List<String> fileNames) {
        HashMap<String, CtClass<?>> classDictionary = new HashMap<>();
        for (CtClass<?> clazz : spoonClasses) {
            String clazzName = clazz.getQualifiedName();

            // Get the class name without the package
            clazzName = clazzName.substring(clazzName.lastIndexOf('.') + 1);
            for (String filePath : fileNames) {

                // Get the file name without the extension
                String fileName = Paths.get(filePath).getFileName().toString().replace(".java", "");
                if (clazzName.equals(fileName)) {
                    classDictionary.put(filePath, clazz);
                }
            }
        }
        return classDictionary;
    }


    /**
     * Gets the common path of the given files. The common path is the path that is shared by all files. This is never a
     * file but always a directory.
     *
     * @param files the files
     * @return the common path
     */
    @Deprecated
    private static Path getCommonPath(final String[] files) {
        // Convert the array of file paths to a list of Path objects
        List<Path> paths = Arrays.stream(files)
                .map(Paths::get)
                .toList();

        // If there's only one path, return its parent directory
        if (paths.size() == 1) {
            return paths.get(0).getParent();
        }

        // Find the shortest path as a starting point for the common path
        Path commonPath = paths.get(0);

        // Iterate through each path to find the common prefix
        for (Path path : paths) {
            commonPath = commonPath.relativize(path);
        }

        // Build the common path by joining the common prefix with the root directory
        commonPath = commonPath.isAbsolute() ? commonPath : Paths.get("/").resolve(commonPath);

        return commonPath;
    }

}
