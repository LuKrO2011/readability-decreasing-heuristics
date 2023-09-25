package de.uni_passau.fim.se2.rdh.output;

import de.uni_passau.fim.se2.rdh.RefactoringProcessor;
import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
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
import java.util.HashMap;
import java.util.List;

/**
 * Writes the output to the output directory and also reproduces the original directory structure in the output
 * directory.
 */
public class StructureKeepingOutputWriter extends AbstractOutputWriter {

    private static final Logger LOG = LoggerFactory.getLogger(RefactoringProcessor.class);
    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;
    private final Path inputBasePath;
    private final Path outputBaseDir;
    private String[] input;

    /**
     * Creates a new SameFolderOutputWriter.
     *
     * @param spoon         the spoon instance to use
     * @param probabilities the probabilities to use
     * @param inputBasePath the base path of all input files of the used refactoring processor
     * @param outputBaseDir the base directory of the output
     */
    public StructureKeepingOutputWriter(final SpoonAPI spoon, final RdcProbabilities probabilities,
                                        final Path inputBasePath, final Path outputBaseDir) {
        this.spoon = spoon;
        this.probabilities = probabilities;
        this.inputBasePath = inputBasePath;
        this.outputBaseDir = outputBaseDir;
    }


    private void writeOutput(final Path inputDir, final String fileName, final CtClass<?> clazz) {
        // TODO: Adjust validation
        /* boolean validPath = validatePath(inputDir);
        boolean validFileName = validateFileName(inputDir, fileName);
        boolean validClass = validateClass(clazz);

        // If the path, file name or class is invalid, return
        if (!validPath || !validFileName || !validClass) {
            return;
        }*/

        // Create the output subdirectory
        Path outputDir = createOutputDir(inputDir, inputBasePath, outputBaseDir);

        // Get the printer
        Environment env = spoon.getEnvironment();
        RdcJavaPrettyPrinter printer = new RdcJavaPrettyPrinter(env, probabilities);

        // Convert the model to java code
        String javaCode = printer.prettyprint(clazz);

        // Write the java code to the output directory with the same file name
        Path path = Paths.get(outputDir.toString(), fileName);
        try (FileWriter writer = new FileWriter(path.toString())) {
            writer.write(javaCode);
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not write output file: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Creates all subdirectories in the output directory, that are needed to reproduce the original directory
     * structure. The original directory structure is reproduced by using the input directory as base directory.
     *
     * @param inputDir      the input directory
     * @param inputBaseDir  the base directory of all input files of the used refactoring processor
     * @param outputBaseDir the base directory of the output
     * @return the output directory
     */
    private static Path createOutputDir(final Path inputDir, final Path inputBaseDir, final Path outputBaseDir) {
        // Get the relative path of the input directory
        Path relativePath = inputBaseDir.relativize(inputDir);

        // Create the output directory
        Path outputDir = Paths.get(outputBaseDir.toString(), relativePath.toString());
        outputDir.toFile().mkdirs();
        return outputDir;
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

            // Get the class for the file
            CtClass<?> clazz = classDictionary.get(fileName);

            // Write the output
            writeOutput(inputDir, inputFileName, clazz);
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

}
