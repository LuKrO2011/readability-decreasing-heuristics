package de.uni_passau.fim.se2.rdh;

import de.uni_passau.fim.se2.rdh.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.refactorings.FieldRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.LocalVariableRenamer;
import de.uni_passau.fim.se2.rdh.refactorings.MethodInliner;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import de.uni_passau.fim.se2.rdh.printer.RdcTokenWriter;
import spoon.support.gui.SpoonModelTree;

import java.io.File;

public class ReadabilityDecreaser {

  private static final String DEFAULT_OUTPUT_DIR = "output";
  // public static final String CONFIG_FILE_NAME = "config-no-modification.yaml";
  public static final String CONFIG_FILE_NAME = "config.yaml";

  public File getInputDir() {
    return inputDir;
  }

  public File getOutputDir() {
    return outputDir;
  }

  private final File inputDir;
  private final File outputDir;

  private final LocalVariableRenamer localVariableRenamer;
  private final MethodRenamer methodRenamer;

  // private static final Logger log = LoggerFactory.getLogger(ReadabilityDecreaser.class);

  private final SpoonAPI spoon;
  private final FieldRenamer fieldRenamer;
  private final MethodInliner methodInliner;
  private RdcProbabilities probabilities;

  public ReadabilityDecreaser(String inputDirPath) {
    this(inputDirPath, DEFAULT_OUTPUT_DIR);
  }

  public ReadabilityDecreaser(String inputDirPath, String outputDirPath) {
    this.inputDir = new File(inputDirPath);
    checkFile(inputDir);

    if (outputDirPath == null) {
      outputDirPath = DEFAULT_OUTPUT_DIR;
    }

    this.outputDir = createFolder(outputDirPath);

    this.spoon = new Launcher();

    // Load RdcProbabilities from yaml file
    YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
    probabilities = (RdcProbabilities) yamlReaderWriter.load(CONFIG_FILE_NAME);

    this.localVariableRenamer = new LocalVariableRenamer(spoon, probabilities);
    this.methodRenamer = new MethodRenamer(spoon, probabilities);
    // this.allRenamer = new AllRenamer(spoon, probabilities   );
    this.fieldRenamer = new FieldRenamer(spoon, probabilities);
    this.methodInliner = new MethodInliner(spoon, probabilities);

    setupSpoon();
  }

  private void setupSpoon() {
    Environment env = spoon.getEnvironment();

    // Imports and comment settings
    env.setAutoImports(true);

    // Writing comments is done probabilistic in RdcTokenWriter
    // env.setCommentEnabled(true);

    // Add a change listener that is needed for RdcJavaPrettyPrinter
    // new ChangeCollector().attachTo(env)

    // Create own prittyprinter
    DefaultJavaPrettyPrinter prettyPrinter = new DefaultJavaPrettyPrinter(env);
    PrinterHelper printerHelper = new PrinterHelper(env);
    prettyPrinter.setPrinterTokenWriter(new RdcTokenWriter(printerHelper, probabilities));
    prettyPrinter.setIgnoreImplicit(false);

    // Sniper keeps structure of original and replaces only changes
    env.setPrettyPrinterCreator(() -> prettyPrinter);

    // where to write
    spoon.setSourceOutputDirectory(outputDir.getAbsolutePath());
  }

  public static void checkFiles(File... files) {
    for (File directory : files) {
      checkFile(directory);
    }
  }

  private static void checkFile(File directory) {
    if (directory == null || !directory.exists()) {
      throw new IllegalArgumentException("Directory does not exist: " + directory);
    }
  }

  private static File createFolder(String path) {
    File folder = new File(path);
    if (!folder.exists()) {
      boolean success = folder.mkdirs();

      if (!success) {
        // log.error("Could not create folder: {}", path);
      }
    }
    return folder;
  }


  public void readInput(String... fileNames) {

    // Add all files with the given names to the input
    for (String fileName : fileNames) {
      spoon.addInputResource(inputDir.getAbsolutePath() + "/" + fileName);
    }

    spoon.buildModel();
  }

  public void readInput() {

    // Add the whole folder to the input
    spoon.addInputResource(inputDir.getAbsolutePath());

    spoon.buildModel();
  }

  public void writeOutput() {
    // check if the output directory exists
    checkFile(outputDir);

    // default printing
    spoon.prettyprint();
  }

  public void process() {
    readInput();
    methodInliner.inline();
    fieldRenamer.rename();
    localVariableRenamer.rename();
    methodRenamer.rename();
    writeOutput();
  }

  public void process(String... fileNames) {
    readInput(fileNames);
    methodInliner.inline();
    fieldRenamer.rename();
    localVariableRenamer.rename();
    methodRenamer.rename();
    writeOutput();
  }

  public void display() {
    // Get a graphical overview, constructing is enough
    SpoonModelTree tree = new SpoonModelTree(spoon.getFactory());
  }
}
