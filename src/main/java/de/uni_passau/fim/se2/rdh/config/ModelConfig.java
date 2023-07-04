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
}
