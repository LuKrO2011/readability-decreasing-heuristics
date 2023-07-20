package de.uni_passau.fim.se2.rdh.config;

import lombok.Data;

/**
 * Contains the configuration for the model.
 */
@Data
public final class ModelConfig {

    private String condaPath;
    private String modelPath;
    private String pythonScriptPath;
    private String inputPath;

    /**
     * Creates a new {@link ModelConfig} all paths null.
     */
    public ModelConfig() {
    }

    /**
     * A copy constructor for {@link ModelConfig}.
     *
     * @param modelConfig The {@link ModelConfig} to copy.
     */
    public ModelConfig(final ModelConfig modelConfig) {
        this.condaPath = modelConfig.getCondaPath();
        this.modelPath = modelConfig.getModelPath();
        this.pythonScriptPath = modelConfig.getPythonScriptPath();
        this.inputPath = modelConfig.getInputPath();
    }
}
