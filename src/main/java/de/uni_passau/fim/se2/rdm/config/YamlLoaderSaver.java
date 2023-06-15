package de.uni_passau.fim.se2.rdm.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class YamlLoaderSaver {

  private static final String DEFAULT_CONFIG_FILE = "config.yaml";
  private static final String CONFIG_FILE_PATH = "src/main/resources";

  public Object load(String configFileName) {
    try (InputStream inputStream = new FileInputStream(CONFIG_FILE_PATH + "/" + configFileName)) {
      Yaml yaml = new Yaml();
      return yaml.loadAs(inputStream, RdcProbabilities.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public Object load() {
    return load(DEFAULT_CONFIG_FILE);
  }

  public void save(String configFileName, Object object) {
    try {
      // Create DumperOptions to configure the output format
      DumperOptions options = new DumperOptions();
      options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

      // Create a YAML instance with the specified options
      Yaml yaml = new Yaml(options);

      // Convert the RdcProbabilities object to YAML and write it to the file
      yaml.dump(object, new FileWriter(CONFIG_FILE_PATH + "/" + configFileName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void save(Object object) {
    save(DEFAULT_CONFIG_FILE, object);
  }
}
