package de.uni_passau.fim.se2.rdh.config;

import jakarta.validation.Configuration;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

/**
 * Loads and saves YAML files.
 */
public class YamlLoaderSaver {

    private static final String DEFAULT_CONFIG_FILE = "config.yaml";

    private static final String DEFAULT_CONFIG_FILE_PATH = "src/main/resources";

    private static final Logger LOG = LoggerFactory.getLogger(YamlLoaderSaver.class);

    private String configFilePath = DEFAULT_CONFIG_FILE_PATH;

    /**
     * Loads the given YAML file. The file must be in the "resources" folder.
     *
     * @param configFileName the name of the YAML file
     * @param clazz          the class of the object to load
     * @return the loaded object
     */
    public Object load(@NotBlank final String configFileName, @NotNull final Class<?> clazz) {
        try (InputStream inputStream = YamlLoaderSaver.class.getClassLoader().getResourceAsStream(configFileName)) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, clazz);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not load config file: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * Loads the given YAML file. The file must be in the "resources" folder.
     *
     * @param configFileName the name of the YAML file
     * @return the loaded object
     */
    public RdcProbabilities loadRdcProbabilities(@NotBlank final String configFileName) {
        RdcProbabilities loadedData = (RdcProbabilities) load(configFileName, RdcProbabilities.class);
        validate(loadedData);
        return loadedData;
    }

    /**
     * Validates the given object.
     *
     * @param data the object to validate
     */
    private void validate(@NotNull final RdcProbabilities data) {
        Configuration<? extends Configuration<?>> validatorConfig = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator());

        try (ValidatorFactory validatorFactory = validatorConfig.buildValidatorFactory()) {
            Validator validator = validatorFactory.getValidator();

            Set<ConstraintViolation<RdcProbabilities>> violations = validator.validate(data);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<RdcProbabilities> violation : violations) {
                    if (LOG.isErrorEnabled()) {
                        LOG.error("Invalid config file: {}", violation.getMessage());
                    }
                }
                throw new IllegalArgumentException("Invalid config file");
            }
        }

        List<Double> spaces = new java.util.ArrayList<>(data.getSpace());
        if (spaces.get(0) != 0) {
            spaces.set(0, 0.0);
            data.setSpace(spaces);
            if (LOG.isWarnEnabled()) {
                LOG.warn("The first space value was not 0.0. It was set to 0.0.");
            }
        }
    }

    /**
     * Loads the default YAML file.
     *
     * @return the loaded object
     */
    public Object load() {
        return load(DEFAULT_CONFIG_FILE, RdcProbabilities.class);
    }

    /**
     * Saves the given object to the given YAML file.
     *
     * @param configFileName the name of the YAML file
     * @param object         the object to save
     */
    public void save(@NotBlank final String configFileName, @NotNull final Object object) {

        try (FileWriter fw = new FileWriter(configFilePath + "/" + configFileName, StandardCharsets.UTF_8)) {

            // Create DumperOptions to configure the output format
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            // Create a YAML instance with the specified options
            Yaml yaml = new Yaml(options);

            // Convert the RdcProbabilities object to YAML and write it to the file
            yaml.dump(object, fw);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not save config file: {}", e.getMessage());
            }
        }
    }

    /**
     * Saves the given object to the default YAML file.
     *
     * @param object the object to save
     */
    public void save(@NotNull final Object object) {
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
    public void setConfigFilePath(@NotNull final String configFilePath) {
        this.configFilePath = configFilePath;
    }
}
