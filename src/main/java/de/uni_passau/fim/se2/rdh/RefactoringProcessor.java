package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.imports.StarImporter;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.inline.MethodInliner;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.magic_numbers.OperationInserter;
import de.uni_passau.fim.se2.rdh.refactorings.rename.FieldRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.LocalVariableRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.MethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.SimpleMethodRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.rename.realistic.RealisticMethodRenamer;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.RdcJavaPrettyPrinter;
import spoon.support.gui.SpoonModelTree;

import java.util.List;

/**
 * The RefactoringProcessor is responsible for applying the refactorings to the AST.
 */
public class RefactoringProcessor {

    private final ProcessingPath outputDir;
    private static final Logger LOG = LoggerFactory.getLogger(RefactoringProcessor.class);
    private SpoonAPI spoon;
    private final RdcProbabilities probabilities;
    private List<AbstractModification> modifications;

    private boolean used = false;

    /**
     * Creates a new RefactoringProcessor.
     *
     * @param outputDirPath the path to the output directory
     * @param probabilities the probabilities to use
     */
    public RefactoringProcessor(final ProcessingPath outputDirPath, final RdcProbabilities probabilities) {
        this.outputDir = outputDirPath;
        this.probabilities = new RdcProbabilities(probabilities);

        setup();
    }

    /**
     * Initializes the RefactoringProcessor.
     */
    private void setup() {
        // Create spoon launcher
        this.spoon = new Launcher();

        MethodRenamer backupMethodRenamer = new SimpleMethodRenamer(spoon, probabilities);

        // Create the refactorings
        modifications = List.of(
                new LocalVariableRenamer(spoon, probabilities),
                new FieldRenamer(spoon, probabilities),
                new RealisticMethodRenamer(spoon, probabilities, backupMethodRenamer),
                new MethodInliner(spoon, probabilities),
                new OperationInserter(spoon, probabilities),
                new StarImporter(spoon, probabilities));

        // Setup spoon
        setupSpoon();

        // Setup done
        if (LOG.isInfoEnabled()) {
            LOG.info("RefactoringProcessor initialized with output directory {}", outputDir.getAbsolutePath());
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

        // Add a change listener that is needed for spoon.reflect.visitor.RdcJavaPrettyPrinter
        // new ChangeCollector().attachTo(env)

        // Set output type
        // env.setOutputType(OutputType.CLASSES);

        // Adjust the pretty printer with own token writer (RdcTokenWriter)
        DefaultJavaPrettyPrinter prettyPrinter = new RdcJavaPrettyPrinter(env, probabilities);
        PrinterHelper printerHelper = new PrinterHelper(env);
        prettyPrinter.setPrinterTokenWriter(new RdcTokenWriter(printerHelper, probabilities));
        prettyPrinter.setIgnoreImplicit(false);
        env.setPrettyPrinterCreator(() -> prettyPrinter);

        // Set the output directory
        spoon.setSourceOutputDirectory(outputDir.getAbsolutePath());
    }

    /**
     * Create the output files (java code) using the modified spoon pretty printer.
     */
    public void writeOutput() {
        spoon.prettyprint();
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
        writeOutput();
        used = true;
    }

    /**
     * Display a graphical overview of the spoon model.
     */
    public void display() {
        // Get a graphical overview, constructing is enough
        new SpoonModelTree(spoon.getFactory());
    }
}
