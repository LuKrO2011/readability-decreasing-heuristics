package de.uni_passau.fim.se2.rdh.refactorings.rename.realistic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.uni_passau.fim.se2.rdh.config.YamlLoaderSaver;
import de.uni_passau.fim.se2.rdh.validators.JsonFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Loads the JSON data from a json file.
 */
public class JsonLoader {

    private static final Logger LOG = LoggerFactory.getLogger(YamlLoaderSaver.class);

    /**
     * Loads the JSON data from the file.
     *
     * @param type     the type of the data
     * @param fileName the name of the JSON file
     * @return the loaded JSON data
     */
    private Object load(final Type type, @JsonFile final String fileName) {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(fileName, StandardCharsets.UTF_8)) {
            return gson.<List<MethodRenamingData>>fromJson(reader, type);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) {
                LOG.error("Could not load JSON file.", e);
            }
        }
        return List.of();
    }

    /**
     * Loads the method renaming data from the JSON file.
     *
     * @param fileName the name of the JSON file
     * @return the loaded method renaming data
     */
    public List<MethodRenamingData> loadMethodRenamingData(@JsonFile final String fileName) {
        Type type = new TypeToken<List<MethodRenamingData>>() {
        }.getType();

        Object loadedData = load(type, fileName);

        List<MethodRenamingData> data = castLoadedData(loadedData);
        return data;
    }

    /**
     * Validates the loaded data.
     *
     * @param loadedData the loaded data
     * @return the validated data
     */
    public List<MethodRenamingData> castLoadedData(final Object loadedData) {
        if (loadedData instanceof List) {
            List<Object> list = (List<Object>) loadedData;
            if (!list.isEmpty() && (list.get(0) instanceof MethodRenamingData)) {
                return (List<MethodRenamingData>) loadedData;
            }
        }

        if (LOG.isErrorEnabled()) {
            LOG.error("Could not validate method renaming data.");
        }

        return List.of();
    }
}
