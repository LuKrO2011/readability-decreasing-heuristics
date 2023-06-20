package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;

import de.uni_passau.fim.se2.rdh.refactorings.FieldRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.LocalVariableRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.MethodInliner;
import de.uni_passau.fim.se2.rdh.refactorings.MethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import spoon.support.gui.SpoonModelTree;

import java.util.List;

/**
 * Decreases the readability of Java code.
 * <p>
 * This class is the main entry point for the Readability Decreaser. It is responsible for setting up the spoon
 * environment and calling the refactorings.
 * </p>
 */
public class ReadabilityDecreaser {

    private static final String CONFIG_FILE_NAME = "config.yaml";
    private final ProcessingPath inputDir;
    private final ProcessingPath outputDir;
    private static final Logger LOG = LoggerFactory.getLogger(ReadabilityDecreaser.class);

    private final SpoonAPI spoon;
    private final RdcProbabilities probabilities;
    private final List<AbstractModification> modifications;


    /**
     * Creates a new ReadabilityDecreaser with default config.
     *
     * @param inputDirPath  the path to the input directory
     * @param outputDirPath the path to the output directory
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath) {
        this(inputDirPath, outputDirPath, CONFIG_FILE_NAME);
    }

    /**
     * Creates a new ReadabilityDecreaser.
     *
     * @param inputDirPath   the path to the input directory
     * @param outputDirPath  the path to the output directory
     * @param configFilePath the path to the config file
     */
    public ReadabilityDecreaser(final ProcessingPath inputDirPath, final ProcessingPath outputDirPath,
                                final String configFilePath) {
        this.inputDir = inputDirPath;
        this.outputDir = outputDirPath;

        // Create spoon launcher
        this.spoon = new Launcher();

        // Load the configuration
        YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
        probabilities = (RdcProbabilities) yamlReaderWriter.load(configFilePath);

        // Create the refactorings
        modifications = List.of(
                new LocalVariableRenamer(spoon, probabilities),
                new FieldRenamer(spoon, probabilities),
                new MethodRenamer(spoon, probabilities),
                new MethodInliner(spoon, probabilities));

        // Setup spoon
        setupSpoon();

        // Setup done
        if (LOG.isInfoEnabled()) {
            LOG.info("ReadabilityDecreaser created with config file: " + configFilePath);
        }
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
     *
     * @param fileNames the names of the files
     */
    public void readInput(final String... fileNames) {

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
        spoon.prettyprint();
    }

    /**
     * Process the input files using the registered refactorings.
     */
    public void process() {
        readInput();
        modifications.forEach(AbstractModification::apply);
        writeOutput();
    }

    /**
     * Process the given input files using the registered refactorings.
     *
     * @param fileNames the names of the files
     */
    public void process(final String... fileNames) {
        readInput(fileNames);
        modifications.forEach(AbstractModification::apply);
        writeOutput();
    }

    /**
     * Display a graphical overview of the spoon model.
     */
    public void display() {
        // Get a graphical overview, constructing is enough
        new SpoonModelTree(spoon.getFactory());
    }


}
