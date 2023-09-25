package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.output.AbstractOutputWriter;
import de.uni_passau.fim.se2.rdh.output.NewFolderOutputWriter;
import de.uni_passau.fim.se2.rdh.output.SameFolderOutputWriter;
import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.util.ProcessingPath;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.JavaPrettyPrinterC;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.RdcJavaPrettyPrinter;
import spoon.reflect.visitor.RdcTokenWriter;
import spoon.support.gui.SpoonModelTree;

import java.util.ArrayList;
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
    private AbstractOutputWriter outputWriter;

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

        // Write the output
        if (outputWriter instanceof NewFolderOutputWriter) { // Create a new folder for the output
            outputWriter.writeOutput();
        } else if (outputWriter instanceof SameFolderOutputWriter) { // Write the output to the input directory
            ((SameFolderOutputWriter) outputWriter).addInputs(input);
            outputWriter.writeOutput();
        } else {
            throw new IllegalStateException("Unexpected value: " + outputWriter.getClass());
        }

        used = true;
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

    /**
     * Sets the output writer to use.
     *
     * @param outputWriter the output writer
     */
    public void setOutputWriter(final AbstractOutputWriter outputWriter) {
        this.outputWriter = outputWriter;
    }
}
