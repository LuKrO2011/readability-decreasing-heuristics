package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;

import de.uni_passau.fim.se2.rdh.refactorings.AbstractModification;
import de.uni_passau.fim.se2.rdh.refactorings.experimental.StarImporter;
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
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import spoon.reflect.visitor.RdcJavaPrettyPrinter;
import spoon.support.gui.SpoonModelTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final RdcProbabilities probabilities;

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

        // Load the configuration
        YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
        probabilities = yamlReaderWriter.loadRdcProbabilities(configFilePath);
    }

    /**
     * Processes the given files or subdirectories.
     *
     * @param fileNames the files or subdirectories to processs
     */
    public void process(final String... fileNames) {
        RefactoringProcessor refactoringProcessor = new RefactoringProcessor(outputDir, probabilities);
        List<String> fullyQualifiedClassNames = new ArrayList<>();

        for (String fileName : fileNames) {
            fullyQualifiedClassNames.add(inputDir.getAbsolutePath() + "/" + fileName);
        }

        refactoringProcessor.process(fullyQualifiedClassNames.toArray(new String[0]));
    }

}
