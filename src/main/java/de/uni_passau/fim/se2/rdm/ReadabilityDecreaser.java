package de.uni_passau.fim.se2.rdm;

import de.uni_passau.fim.se2.rdm.config.RdcProbabilities;
import de.uni_passau.fim.se2.rdm.config.YamlLoaderSaver;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.Environment;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.RdcTokenWriter;
import spoon.support.gui.SpoonModelTree;

import java.io.File;

public class ReadabilityDecreaser {

  private static final String DEFAULT_OUTPUT_DIR = "output";
  private final File inputDir; // TODO: Make getter and setter
  private final File outputDir; // TODO: Make getter and setter

  private final LocalVariableRenamer localVariableRenamer;
  private final MethodRenamer methodRenamer;
  private final AllRenamer allRenamer;

  // private static final Logger log = LoggerFactory.getLogger(ReadabilityDecreaser.class);

  private final SpoonAPI spoon;
  private final FieldRenamer fieldRenamer;
  private final MethodInliner methodInliner;

  public ReadabilityDecreaser(String inputDirPath) {
    this(inputDirPath, DEFAULT_OUTPUT_DIR);
  }

  public ReadabilityDecreaser(String inputDirPath, String outputDirPath) {
    this.inputDir = new File(inputDirPath);
    checkFile(inputDir);

    this.outputDir = createFolder(outputDirPath);

    this.spoon = new Launcher();

    this.localVariableRenamer = new LocalVariableRenamer(spoon);
    this.methodRenamer = new MethodRenamer(spoon);
    this.allRenamer = new AllRenamer(spoon);
    this.fieldRenamer = new FieldRenamer(spoon);
    this.methodInliner = new MethodInliner(spoon);

    setupSpoon();
  }

  private void setupSpoon() {
    Environment env = spoon.getEnvironment();

    // Imports and comment settings
    env.setAutoImports(true);
    env.setCommentEnabled(false);

    // Add a change listener that is needed for RdcJavaPrettyPrinter
    // new ChangeCollector().attachTo(env)

    // Load RdcProbabilities from yaml file
    YamlLoaderSaver yamlReaderWriter = new YamlLoaderSaver();
    RdcProbabilities probabilities = (RdcProbabilities) yamlReaderWriter.load("config.yaml");

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
    //methodInliner.inline();
    fieldRenamer.rename();
    localVariableRenamer.rename();
    methodRenamer.rename();
    writeOutput();
  }

    /*public void process(String... fileNames) {
        readInput(fileNames);
        variableRenamer.rename();
        methodRenamer.rename();
        writeOutput();
    }*/

  public void display() {
    // Get a graphical overview, constructing is enough
    SpoonModelTree tree = new SpoonModelTree(spoon.getFactory());
  }
}
