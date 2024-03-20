package de.uni_passau.fim.se2.rdh.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import org.yaml.snakeyaml.error.YAMLException;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Loads and saves YAML files.
 */
public class YamlLoaderSaver {

    private static final Logger LOG = LoggerFactory.getLogger(YamlLoaderSaver.class);

    /**
     * Loads the given YAML file. The file must be in the "resources" folder.
     *
     * @param yamlPath the name of the YAML file
     * @param clazz    the class of the object to load
     * @return the loaded object
     * @throws IOException if the file could not be loaded
     */
    public Object load(@NotBlank final Path yamlPath, @NotNull final Class<?> clazz) throws IOException {
        try (InputStream inputStream = new FileInputStream(yamlPath.toString())) {
            Yaml yaml = new Yaml();
            return yaml.loadAs(inputStream, clazz);
        } catch (IOException | YAMLException e) {
            throw new IOException("Could not load yaml file: " + yamlPath, e);
        }
    }

    /**
     * Loads a {@link RdcProbabilities} object from the file with the given path.
     *
     * @param configPath the path to the config file
     * @return the loaded {@link RdcProbabilities}
     * @throws IOException if the file could not be loaded
     */
    public RdcProbabilities loadRdcProbabilities(@NotBlank final Path configPath) throws IOException {
        RdcProbabilities loadedData = (RdcProbabilities) load(configPath, RdcProbabilities.class);

        validate(loadedData);
        return loadedData;
    }

    /**
     * Loads a {@link Config} from the file with the given path.
     *
     * @param configPath the path to the config file
     * @return the loaded {@link Config}
     * @throws IOException if the file could not be loaded
     */
    public Config loadConfig(@NotBlank final Path configPath) throws IOException {
        Config loadedData = (Config) load(configPath, Config.class);

        return loadedData;
    }

    /**
     * Validates the given object.
     *
     * @param data the object to validate
     * @throws IOException if the object is invalid
     */
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
    private void validate(@NotNull final RdcProbabilities data) throws IOException {
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
                throw new IOException("Invalid config file");
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
     * Saves the given object to the given YAML file.
     *
     * @param configPath the path to the config file
     * @param object     the object to save
     * @throws IOException if the file could not be saved
     */
    public void save(@NotBlank final Path configPath, @NotNull final Object object) throws IOException {

        try (FileWriter fw = new FileWriter(configPath.toString(), StandardCharsets.UTF_8)) {

            // Create DumperOptions to configure the output format
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            // Create a YAML instance with the specified options
            Yaml yaml = new Yaml(options);

            // Convert the RdcProbabilities object to YAML and write it to the file
            yaml.dump(object, fw);
        } catch (IOException | YAMLException e) {
            throw new IOException("Could not save yaml file: " + configPath, e);
        }
    }
}
