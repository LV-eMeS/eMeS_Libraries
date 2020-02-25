package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * Abstract implementation of Yaml structure that allows to parse configuration represented as key-value
 * structure with possible nested structures in it. Configuration can be also located in system properties or
 * environment variables, as well as in yaml file itself.
 * 
 * <p>Protected helper methods:
 * <ul>
 *     <li>getConfiguration</li>
 *     <li>getConfigurationOptional</li>
 *     <li>getFromSysOrEnvProps</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
public abstract class MS_YamlStructureBase {

    private static final String ERR_MESS_FMT = "Property cannot be found by given key: %s";
    private static final String ERR_MESS_FMT2 = "Property cannot be found by given key [%s], node [%s] doesn't exist";

    /**
     * Extracts concrete configuration property starting from "<b>propertiesRoot</b>" section by provided key <b>key</b>,
     * e.g. "<code>startup.failFast</code>", which in this case will be of type <code>Boolean</code>.
     * <p><u>Note</u>: Currently <b>key</b> should be delimited separated by dots only.
     *
     * @param <T> type of desired property.
     * @return configuration property object.
     * @throws MS_BadSetupException in case property cannot be found by provided key, either it cannot be cast to
     *                              desired type.
     * @see MS_YamlStructureBase#getConfiguration(String, Class, Map)
     */
    protected final <T> T getConfiguration(String key, Map<String, Object> propertiesRoot) throws MS_BadSetupException {
        return getConfiguration(key, null, propertiesRoot);
    }

    /**
     * Extracts concrete configuration property from environment variables, system properties or properties file (starting from "<b>propertiesRoot</b>" section)
     * by provided key <b>key</b>, e.g. "<code>startup.failFast</code>", which in this case will be of type <code>Boolean</code>.
     * <p><u>Note 1</u>: As system properties and environment variables can be only {@link String} values, {@link MS_StringUtils#stringToPrimitive(String, Class)}
     * is performed to extract those values. If that operation fails, {@link MS_BadSetupException} will be thrown.
     * Therefore it's only possible to operate with data types supported by extracting method.
     * <p><u>Note 2</u>: Currently <b>key</b> should be delimited separated by dots only.
     *
     * @param <T>           type of desired property.
     * @param propertyClass class of desired property.
     * @return configuration property object.
     * @throws MS_BadSetupException in case property cannot be found by provided key, either it cannot be cast to
     *                              desired type.
     * @see MS_YamlStructureBase#getConfiguration(String, Map)
     */
    @SuppressWarnings("unchecked")
    protected final <T> T getConfiguration(String key, Class<T> propertyClass, Map<String, Object> propertiesRoot) throws MS_BadSetupException {
        if (StringUtils.isEmpty(key))
            throw new MS_BadSetupException(ERR_MESS_FMT, key);

        T sysOrEnvProperty = getFromSysOrEnvProps(key, propertyClass);
        // If there is something under key passed as system property or environment variable return it
        if (sysOrEnvProperty != null) return sysOrEnvProperty;

        // Otherwise look in actual properties file
        String[] path = key.split("\\.");

        if (path.length == 0)
            throw new MS_BadSetupException(ERR_MESS_FMT, key);
        else if (path.length == 1) {
            Object res = propertiesRoot.get(path[0]);
            if (res == null)
                throw new MS_BadSetupException(ERR_MESS_FMT, key);
            return (T) res;
        } else {
            int i = 0;
            Map<String, Object> currentNode = (Map<String, Object>) propertiesRoot.get(path[i]);
            while (++i < path.length - 1) {
                if (currentNode == null)
                    throw new MS_BadSetupException(ERR_MESS_FMT2, key, path[i - 1]);
                currentNode = (Map<String, Object>) currentNode.get(path[i]);
            }

            String lastNodeKey = path[path.length - 1];
            Object res = currentNode.get(lastNodeKey);
            if (res == null)
                throw new MS_BadSetupException(ERR_MESS_FMT2, key, lastNodeKey);
            return (T) res;
        }
    }

    /**
     * Extracts concrete configuration property from environment variables, system properties or properties file (starting from "<b>propertiesRoot</b>" section)
     * by provided key <b>key</b>, e.g. "<code>startup.failFast</code>", which in this case will be of type <code>Boolean</code>.
     * <p><u>Note 1</u>: As system properties and environment variables can be only {@link String} values, {@link MS_StringUtils#stringToPrimitive(String, Class)}
     * is performed to extract those values. If that operation fails, {@link MS_BadSetupException} will be thrown.
     * Therefore it's only possible to operate with data types supported by extracting method.
     * <p><u>Note 2</u>: Currently <b>key</b> should be delimited separated by dots only.
     *
     * @param <T>           type of desired property.
     * @param propertyClass class of desired property.
     * @return configuration property object or <tt>null</tt> if there is not such configuration defined.
     * @throws MS_BadSetupException in case actual property type cannot be cast to desired type.
     * @see MS_YamlStructureBase#getConfiguration(String, Map)
     */
    @SuppressWarnings("unchecked")
    protected final <T> T getConfigurationOptional(String key, Class<T> propertyClass, Map<String, Object> propertiesRoot) throws MS_BadSetupException {
        if (StringUtils.isEmpty(key))
            throw new MS_BadSetupException(ERR_MESS_FMT, key);

        T sysOrEnvProperty = getFromSysOrEnvProps(key, propertyClass);
        // If there is something under key passed as system property or environment variable return it
        if (sysOrEnvProperty != null) return sysOrEnvProperty;

        // Otherwise look in actual properties file
        String[] path = key.split("\\.");

        if (path.length == 0)
            return null;
        else if (path.length == 1) {
            Object res = propertiesRoot.get(path[0]);
            return (T) res;
        } else {
            int i = 0;
            Map<String, Object> currentNode = (Map<String, Object>) propertiesRoot.get(path[i]);
            while (++i < path.length - 1) {
                if (currentNode == null) return null;
                currentNode = (Map<String, Object>) currentNode.get(path[i]);
            }

            String lastNodeKey = path[path.length - 1];
            Object res = currentNode.get(lastNodeKey);
            return (T) res;
        }
    }

    protected final <T> T getFromSysOrEnvProps(String key, Class<T> propertyClass) {
        // Try to find key passed as system property or environment variable
        if (propertyClass != null) {
            String envPropertyForKey = System.getenv(key);
            if (StringUtils.isNotEmpty(envPropertyForKey)) {
                return MS_StringUtils.stringToPrimitive(envPropertyForKey, propertyClass);
            }
            String sysPropertyForKey = System.getProperty(key);
            if (StringUtils.isNotEmpty(sysPropertyForKey)) {
                return MS_StringUtils.stringToPrimitive(sysPropertyForKey, propertyClass);
            }
        }
        return null;
    }
}
