package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.tools.MS_BadSetupException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class manages property file readers as singletons.
 * Instances of those property readers are created only on first request.
 * <p><u>Note</u>: Property writing via property objects retrieved from this property file manager is not thread safe
 * and therefore is not recommended to perform unless there is guarantee that non-concurrent reading will be performed.
 *
 * @author eMeS
 * @version 1.0
 * @since 2.1.6
 */
public class MS_PropertyFileManager {

    private static Map<String, MS_PropertiesFile> propReaders = new ConcurrentHashMap<>();

    public static MS_PropertiesFile getProperties(String pathToPropsFile) {
        MS_PropertiesFile properties = propReaders.get(pathToPropsFile);
        if (properties == null) {
            try {
                properties = new MS_PropertiesFile();
                properties.load(pathToPropsFile);
                propReaders.put(pathToPropsFile, properties);
            } catch (IOException e) {
                throw new MS_BadSetupException("Properties file [%s] cannot be read.", e, pathToPropsFile);
            }
        }
        return properties;
    }
}
