package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.refactorings.FieldRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.LocalVariableRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.MethodInliner;
import de.uni_passau.fim.se2.rdh.util.FileManager;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import spoon.support.gui.SpoonModelTree;

import java.io.File;

import static de.uni_passau.fim.se2.rdh.util.FileManager.checkFile;

/**
 * Decreases the readability of Java code.
 * <p>
 * This class is the main entry point for the Readability Decreaser. It is responsible for setting up the
 * spoon environment and calling the refactorings.
 */
public class ReadabilityDecreaser {

    private static final String DEFAULT_OUTPUT_DIR = "output";
    // public static final String CONFIG_FILE_NAME = "config-no-modification.yaml";
    public static final String CONFIG_FILE_NAME = "config.yaml";

    private final File inputDir;
    private final File outputDir;
    private final LocalVariableRenamer localVariableRenamer;
    private final MethodRenamer methodRenamer;

    // private static final Logger log = LoggerFactory.getLogger(ReadabilityDecreaser.class);

    private final SpoonAPI spoon;
    private final FieldRenamer fieldRenamer;
    private final MethodInliner methodInliner;
    private final RdcProbabilities probabilities;

    /**
     * Creates a new ReadabilityDecreaser with default output directory and config.
     *
     * @param inputDirPath the path to the input directory
     */
    public ReadabilityDecreaser(String inputDirPath) {
        this(inputDirPath, DEFAULT_OUTPUT_DIR);
    }

    /**
     * Creates a new ReadabilityDecreaser with default config.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     */
    public ReadabilityDecreaser(String inputDirPath, String outputDirPath) {
        this(inputDirPath, outputDirPath, CONFIG_FILE_NAME);
    }

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath   the path to the input directory
     * @param outputDirPath  the path to the output directory
     * @param configFilePath the path to the config file
     */
    public ReadabilityDecreaser(String inputDirPath, String outputDirPath, String configFilePath) {
        // Check input and output directory
        this.inputDir = new File(inputDirPath);
        checkFile(inputDir);
        if (outputDirPath == null) {
            outputDirPath = DEFAULT_OUTPUT_DIR;
        }
        this.outputDir = FileManager.createFolder(outputDirPath);

        // Create spoon launcher
        this.spoon = new Launcher();

        // Load the configuration
        YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
        probabilities = (RdcProbabilities) yamlReaderWriter.load(configFilePath);

        // Create the refactorings
        this.localVariableRenamer = new LocalVariableRenamer(spoon, probabilities);
        this.methodRenamer = new MethodRenamer(spoon, probabilities);
        this.fieldRenamer = new FieldRenamer(spoon, probabilities);
        this.methodInliner = new MethodInliner(spoon, probabilities);

        // Setup spoon
        setupSpoon();
    }

    /**
     * Sets up the spoon environment.
     */
    private void setupSpoon() {
        Environment env = spoon.getEnvironment();

        // Imports and comment settings
        env.setAutoImports(true);

        // Writing comments is done probabilistic in RdcTokenWriter
        // env.setCommentEnabled(true);

        // Add a change listener that is needed for RdcJavaPrettyPrinter
        // new ChangeCollector().attachTo(env)

        // Set output type
        // env.setOutputType(OutputType.CLASSES);

        // Adjust the pretty printer with own token writer (RdcTokenWriter)
        DefaultJavaPrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(env);
        PrinterHelper printerHelper = new PrinterHelper(env);
        prettyPrinter.setPrinterTokenWriter(new RdcTokenWriter(printerHelper, probabilities));
        prettyPrinter.setIgnoreImplicit(false);
        env.setPrettyPrinterCreator(() -> prettyPrinter);

        // Set the output directory
        spoon.setSourceOutputDirectory(outputDir.getAbsolutePath());
    }

    /**
     * Add the given files to the input of spoon.
     * @param fileNames the names of the files
     */
    public void readInput(String... fileNames) {

        // Add all files with the given names to the input
        for (String fileName : fileNames) {
            spoon.addInputResource(inputDir.getAbsolutePath() + "/" + fileName);
        }

        spoon.buildModel();
    }

    /**
     * Add the whole input directory to the input of spoon.
     */
    public void readInput() {

        // Add the whole folder to the input
        spoon.addInputResource(inputDir.getAbsolutePath());

        spoon.buildModel();
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer.
     */
    public void writeOutput() {
        // check if the output directory exists
        FileManager.checkFile(outputDir);

        // default printing
        spoon.prettyprint();
    }

    /**
     * Process the input files using the registered refactorings.
     */
    public void process() {
        readInput();
        methodInliner.inline();
        fieldRenamer.rename();
        localVariableRenamer.rename();
        methodRenamer.rename();
        writeOutput();
    }

    /**
     * Process the given input files using the registered refactorings.
     * @param fileNames the names of the files
     */
    public void process(String... fileNames) {
        readInput(fileNames);
        methodInliner.inline();
        fieldRenamer.rename();
        localVariableRenamer.rename();
        methodRenamer.rename();
        writeOutput();
    }

    /**
     * Display a graphical overview of the spoon model.
     */
    public void display() {
        // Get a graphical overview, constructing is enough
        new SpoonModelTree(spoon.getFactory());
    }

    /**
     * Get the input directory.
     * @return the input directory
     */
    public File getInputDir() {
        return inputDir;
    }

    /**
     * Get the output directory.
     * @return the output directory
     */
    public File getOutputDir() {
        return outputDir;
    }
}
