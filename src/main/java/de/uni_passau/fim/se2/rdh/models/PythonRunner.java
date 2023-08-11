package de.uni_passau.fim.se2.rdh.models;

import de.uni_passau.fim.se2.rdh.config.Config;
import de.uni_passau.fim.se2.rdh.util.FileManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Runs python scripts.
 */
public final class PythonRunner {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(PythonRunner.class);
    private final Config config;

    /**
     * Creates a new instance of this class.
     *
     * @param config The model config to use.
     */
    public PythonRunner(final Config config) {
        this.config = new Config(config);
    }

    /**
     * Executes the python script to create method name predictions using the code2vec model. The predictions are in the
     * same directory as the input file.
     *
     * @param inputPath The input path.
     */
    public void createMethodNamePredictions(final Path inputPath) {
        FileManager.checkDirectory(inputPath.toFile());

        try {
            ProcessBuilder processBuilder =
                    new ProcessBuilder("conda", "run", "-p", config.getCondaPath(), "python",
                            config.getPythonScriptPath() + "/code2vec.py",
                            "--load", config.getModelPath(), "--predict", "-i", inputPath.toString());

            processBuilder.directory(new File(config.getPythonScriptPath()));

            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                LOG.error(
                        "Could not create method name predictions: Python script exited with command {} exited "
                                + "with code {}.",
                        processBuilder.command(),
                        exitCode);
            } else {
                LOG.info("Success: Created method name predictions.");
            }

        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error while running the python script.", e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
