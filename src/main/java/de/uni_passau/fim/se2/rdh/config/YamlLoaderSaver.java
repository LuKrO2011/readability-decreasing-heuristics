package de.uni_passau.fim.se2.rdh.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * Loads and saves YAML files.
 */
public class YamlLoaderSaver {

    private static final String DEFAULT_CONFIG_FILE = "config.yaml";

    private static final String DEFAULT_CONFIG_FILE_PATH = "src/main/resources";

    private static final Logger log = LoggerFactory.getLogger(YamlLoaderSaver.class);

    private String configFilePath = DEFAULT_CONFIG_FILE_PATH;

    /**
     * Loads the given YAML file. The file must be in the resources folder.
     *
     * @param configFileName the name of the YAML file
     * @return the loaded object
     */
    public Object load(@NotBlank String configFileName) {
        try (InputStream inputStream = YamlLoaderSaver.class.getClassLoader().getResourceAsStream(configFileName)) {
            Yaml yaml = new Yaml();
            RdcProbabilities loadedData = yaml.loadAs(inputStream, RdcProbabilities.class);
            validate(loadedData);
            return loadedData;
        } catch (IOException e) {
            log.error("Could not load config file: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Validates the given object.
     *
     * @param data the object to validate
     */
    private void validate(@NotNull RdcProbabilities data) {
        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory().getValidator();

        Set<ConstraintViolation<RdcProbabilities>> violations = validator.validate(data);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<RdcProbabilities> violation : violations) {
                log.error("Invalid config file: {}", violation.getMessage());
            }
            throw new IllegalArgumentException("Invalid config file");
        }
    }

    /**
     * Loads the default YAML file.
     *
     * @return the loaded object
     */
    public Object load() {
        return load(DEFAULT_CONFIG_FILE);
    }

    /**
     * Saves the given object to the given YAML file.
     *
     * @param configFileName the name of the YAML file
     * @param object         the object to save
     */
    public void save(@NotBlank String configFileName, @NotNull Object object) {

        try (FileWriter fw = new FileWriter(configFilePath + "/" + configFileName)) {

            // Create DumperOptions to configure the output format
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            // Create a YAML instance with the specified options
            Yaml yaml = new Yaml(options);

            // Convert the RdcProbabilities object to YAML and write it to the file
            yaml.dump(object, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the given object to the default YAML file.
     *
     * @param object the object to save
     */
    public void save(@NotNull Object object) {
        save(DEFAULT_CONFIG_FILE, object);
    }

    /**
     * Returns the path to the config file.
     *
     * @return the path to the config file
     */
    public String getConfigFilePath() {
        return configFilePath;
    }

    /**
     * Sets the path to the config file.
     *
     * @param configFilePath the path to the config file
     */
    public void setConfigFilePath(@NotNull String configFilePath) {
        this.configFilePath = configFilePath;
    }
}
