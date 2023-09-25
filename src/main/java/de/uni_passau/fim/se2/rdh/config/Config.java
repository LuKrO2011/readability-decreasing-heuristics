package de.uni_passau.fim.se2.rdh.config;

import lombok.Data;

/**
 * Contains the configuration for the model.
 */
@Data
public final class Config {

    private String condaPath;
    private String modelPath;
    private String pythonScriptPath;
    private Scope scope = Scope.FILE;
    private RenameMode renameMethodMode = RenameMode.ITERATIVE; // TODO: Add input validation?
    private OutputMode outputMode = OutputMode.NEW_DIRECTORY; // TODO: Add input validation?

    /**
     * Creates a new {@link Config} all paths null.
     */
    public Config() {
    }

    /**
     * A copy constructor for {@link Config}.
     *
     * @param config The {@link Config} to copy.
     */
    public Config(final Config config) {
        this.condaPath = config.getCondaPath();
        this.modelPath = config.getModelPath();
        this.pythonScriptPath = config.getPythonScriptPath();
        this.scope = config.getScope();
        this.renameMethodMode = config.getRenameMethodMode();
        this.outputMode = config.getOutputMode();
    }

    @Override
    public String toString() {
        return ("Config{condaPath='%s', modelPath='%s', pythonScriptPath='%s', scope=%s, renameMethodMode=%s "
                + "outputMode=%s}")
                .formatted(condaPath, modelPath, pythonScriptPath, scope, renameMethodMode, outputMode);
    }
}
