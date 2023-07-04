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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static de.uni_passau.fim.se2.rdh.refactorings.rename.realistic.RealisticMethodRenamer.PREDICTION_QUALITY_INDEX;

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

        try (FileReader reader = new FileReader(fileName)) {

            return gson.<List<MethodRenamingData>>fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        validate(data);
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

        return null;
    }

    /**
     * Validates the loaded data.
     * <p>
     * If a method name is used twice, it is renamed.
     *
     * @param data the loaded data
     */
    public void validate(final List<MethodRenamingData> data) {
        Set<String> usedMethodNames = new HashSet<>();
        for (MethodRenamingData methodRenamingData : data) {
            String name = methodRenamingData.getPredictions().get(PREDICTION_QUALITY_INDEX).getName();
            if (usedMethodNames.contains(name)) {
                String newName = name + "1";
                if (LOG.isInfoEnabled()) {
                    LOG.info("Method name " + name + " is used twice. Renaming to " + newName);
                }
                methodRenamingData.getPredictions().get(PREDICTION_QUALITY_INDEX).setName(newName);
            }
            usedMethodNames.add(name);
        }
    }
}
