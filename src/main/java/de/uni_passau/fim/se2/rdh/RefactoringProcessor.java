package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.RdcTokenWriter;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.RdcJavaPrettyPrinter;
import spoon.reflect.visitor.JavaPrettyPrinterC;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.gui.SpoonModelTree;

import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * The RefactoringProcessor is responsible for applying the refactorings to the AST.
 */
public class RefactoringProcessor {

    private ProcessingPath outputDir = null; // Null if output should be written to input directory
    private static final Logger LOG = LoggerFactory.getLogger(RefactoringProcessor.class);
    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;
    private final List<AbstractModification> modifications;

    private boolean used = false;
    private static final String RDH_FILE_EXTENSION = "_rdh.java";

    /**
     * Creates a new RefactoringProcessor.
     *
     * @param spoon         the spoon instance to use
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities to use
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The probabilities can be changed by the user at runtime
    public RefactoringProcessor(final SpoonAPI spoon, final ProcessingPath outputDirPath,
                                final RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.outputDir = outputDirPath;
        this.probabilities = probabilities;
        this.modifications = new ArrayList<>();

        setupSpoon();
    }

    /**
     * Creates a new RefactoringProcessor that writes the output to the input directory.
     * TODO: Refactor with method before.
     *
     * @param spoon         the spoon instance to use
     * @param probabilities the probabilities to use
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2") // The probabilities can be changed by the user at runtime
    public RefactoringProcessor(final SpoonAPI spoon, final RdcProbabilities probabilities) {
        this.spoon = spoon;
        this.probabilities = probabilities;
        this.modifications = new ArrayList<>();

        setupSpoon();
    }

    /**
     * Sets up the spoon environment.
     */
    private void setupSpoon() {
        Environment env = spoon.getEnvironment();

        // Imports and comment settings
        env.setAutoImports(true);

        // Disable copying of resources
        env.setCopyResources(false);

        // Writing comments is done probabilistic in RdcTokenWriter
        // env.setCommentEnabled(true);

        // Add a change listener that is needed for spoon.reflect.visitor.RdcJavaPrettyPrinter
        // new ChangeCollector().attachTo(env)

        // Set output type
        // env.setOutputType(OutputType.CLASSES);

        // Adjust the pretty printer with own token writer (RdcTokenWriter)
        JavaPrettyPrinterC prettyPrinter = new RdcJavaPrettyPrinter(env, probabilities);
        PrinterHelper printerHelper = new PrinterHelper(env);
        prettyPrinter.setPrinterTokenWriter(new RdcTokenWriter(printerHelper, probabilities));
        prettyPrinter.setIgnoreImplicit(false);
        env.setPrettyPrinterCreator(() -> prettyPrinter);

        // Set the output directory
        if (outputDir != null) {
            spoon.setSourceOutputDirectory(outputDir.getAbsolutePath());
        }
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer.
     */
    public void writeOutput() {
        spoon.prettyprint();
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer. The output is written to the given
     * directory with the given file name.
     *
     * @param outputDirForFile the directory to write the output to
     * @param fileName         the name of the file
     * @param clazz            the class to write
     */
    public void writeOutput(final ProcessingPath outputDirForFile, final String fileName, final CtClass<?> clazz) {
        Path outputDirForFilePath = Paths.get(outputDirForFile.getAbsolutePath());
        boolean validPath = validatePath(outputDirForFilePath);
        boolean validFileName = validateFileName(outputDirForFilePath, fileName);
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
        Path path = Paths.get(outputDirForFile.getAbsolutePath(), fileName);
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
     * Process the given input files using the registered refactorings.
     *
     * @param input The input files. Can be directories or a files.
     */
    public void process(final String... input) {
        if (used) {
            if (LOG.isErrorEnabled()) {
                LOG.error("RefactoringProcessor can only be used once!");
            }
            return;
        }

        for (String fileName : input) {
            spoon.addInputResource(fileName);
        }

        spoon.buildModel();
        modifications.forEach(AbstractModification::apply);

        // Get all files of the processed model
        List<CtClass<?>> classes = spoon.getModel().getElements(new TypeFilter<>(CtClass.class));

        // TODO: Refactor extract method
        // Match the input file names and the classes using a dictionary
        HashMap<String, CtClass<?>> classDictionary = new HashMap<>();
        for (CtClass<?> clazz : classes) {
            String clazzName = clazz.getQualifiedName();

            // Get the class name without the package
            clazzName = clazzName.substring(clazzName.lastIndexOf('.') + 1);
            for (String filePath : input) {

                // Get the file name without the extension
                String fileName = Paths.get(filePath).getFileName().toString().replace(".java", "");
                if (clazzName.equals(fileName)) {
                    classDictionary.put(filePath, clazz);
                }
            }
        }

        // Write the output to the directory of the input file
        for (String fileName : input) {

            // Get the input directory and the file names
            ProcessingPath inputDir =
                    ProcessingPath.directory(getCommonPath(input)); // TODO: Remove function getCommonPath
            String inputFileName = Paths.get(fileName).getFileName().toString();
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + RDH_FILE_EXTENSION;

            // Get the class for the file
            CtClass<?> clazz = classDictionary.get(fileName);

            // Write the output
            writeOutput(inputDir, outputFileName, clazz);
        }

        used = true;
    }

    /**
     * Gets the common path of the given files. The common path is the path that is shared by all files. This is never a
     * file but always a directory.
     * TODO: Necessary or always a single file?
     *
     * @param files the files
     * @return the common path
     */
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

    /**
     * Display a graphical overview of the spoon model.
     */
    public void display() {
        // Get a graphical overview, constructing is enough
        new SpoonModelTree(spoon.getFactory());
    }

    /**
     * Adds a modification to the list of modifications.
     *
     * @param abstractModification the modification to add
     */
    public void addModification(final AbstractModification abstractModification) {
        modifications.add(abstractModification);
    }
}
