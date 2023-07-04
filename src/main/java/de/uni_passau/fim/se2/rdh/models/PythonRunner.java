package de.uni_passau.fim.se2.rdh.models;

import de.uni_passau.fim.se2.rdh.config.ModelConfig;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Runs python scripts.
 */
public final class PythonRunner {

    private PythonRunner() {
    }

    private static final String MODEL_CONFIG_NAME = "modelConfig.yaml";

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(PythonRunner.class);

    /**
     * Starts code2vec.
     */
    public static void createMethodNamePredictions() {

        YamlLoaderSaver yamlLoaderSaver = new YamlLoaderSaver();
        ModelConfig config = (ModelConfig) yamlLoaderSaver.load(MODEL_CONFIG_NAME, ModelConfig.class);

        /*
         C:\Users\lukas>conda run -p C:/Users/lukas/anaconda3/envs/code2vec python
         C:/Users/lukas/PycharmProjects/code2vec/code2vec.py --load
         C:/Users/lukas/PycharmProjects/Code2Vec/models/java14_model/saved_model_iter8.release --predict
        -i test/resources/code
         */
        try {
            ProcessBuilder processBuilder =
                new ProcessBuilder("conda", "run", "-p", config.getCondaPath(), "python",
                    config.getPythonScriptPath() + "/code2vec.py",
                    "--load", config.getModelPath(), "--predict", "-i",
                    config.getInputPath());

            processBuilder.directory(new File(config.getPythonScriptPath()));

            processBuilder.inheritIO();

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Error while running the python script.", e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
