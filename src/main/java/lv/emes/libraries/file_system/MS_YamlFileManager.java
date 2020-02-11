package lv.emes.libraries.file_system;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.apache.commons.lang3.tuple.Pair;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class manages YAML property file readers as singletons.
 * Instances of those property readers are created only on first request.
 * <p>Example of new properties file registration in extended class:
 * <pre><code>
 *     public static YamlConfig getProjectConfigurations() {
 *         // YamlConfig must be a class defining structure of YAML file
 *         return loadProperties("config.yml", YamlConfig.class);
 *     }
 * </code></pre>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.8.
 */
public class MS_YamlFileManager {

    protected static Map<Pair<String, Class<?>>, Object> configurations = new HashMap<>();

    @SuppressWarnings("unchecked")
    protected static <T> T loadProperties(String pathToPropsFile, Class<T> configStructureClass) {
        Pair<String, Class<?>> configUniqueKey = Pair.of(pathToPropsFile, configStructureClass);
        Object existingProperty = configurations.get(configUniqueKey);
        if (existingProperty != null) return (T) existingProperty;

        Constructor constructor = new Constructor(configStructureClass);
        PropertyUtils propUtils = new PropertyUtils();
        propUtils.setSkipMissingProperties(true);
        constructor.setPropertyUtils(propUtils);
        Yaml yaml = new Yaml(constructor);

        try (FileInputStream fileStream = new FileInputStream(pathToPropsFile)) {
            T config = yaml.loadAs(fileStream, configStructureClass);
            configurations.put(configUniqueKey, config);
            return config;
        } catch (FileNotFoundException e) {
            throw new MS_BadSetupException("Properties file [" + pathToPropsFile + "] doesn't exist.", e);
        } catch (IOException | ConstructorException e) {
            throw new MS_BadSetupException("Properties file [" + pathToPropsFile + "] cannot be read.", e);
        }
    }
}
