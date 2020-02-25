package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.Map;

/**
 * Class that defines properties file, registered in {@link  MS_YamlFileManager}, structure with default root properties node.
 * It has common methods that allows to browse property of any supported type by its path in yaml.
 * Successors of the class must define a variable that will hold <code>Map&lt;String, Object&gt;</code> structure in it
 * and the setter for this variable. Getter should be direct implementation of the method <b>getRootProperties</b>.
 * <p>Public methods:
 * <ul>
 *     <li>getProperty</li>
 *     <li>getPropertyOptional</li>
 * </ul>
 * <p>Methods to override:
 * <ul>
 *     <li>getRootProperties</li>
 * </ul>
 * <p>Protected helper methods:
 * <ul>
 *     <li>getConfiguration</li>
 *     <li>getConfigurationOptional</li>
 *     <li>getFromSysOrEnvProps</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.8.
 */
public abstract class MS_YamlStructureWithDefaultRoot extends MS_YamlStructureBase {

    /**
     * @return root node of properties, under which individual properties will be searched with methods:
     * <ul>
     *     <li>{@link MS_YamlStructureWithDefaultRoot#getProperty(String)}</li>
     *     <li>{@link MS_YamlStructureWithDefaultRoot#getProperty(String, Class)}</li>
     *     <li>{@link MS_YamlStructureWithDefaultRoot#getPropertyOptional(String, Class)}</li>
     * </ul>
     */
    protected abstract Map<String, Object> getRootProperties();

    /**
     * Extracts concrete configuration property starting from "<b>propertiesRoot</b>" section by provided key <b>key</b>,
     * e.g. "<code>startup.failFast</code>", which in this case will be of type <code>Boolean</code>.
     * <p><u>Note</u>: Currently <b>key</b> should be delimited separated by dots only.
     *
     * @param <T> type of desired property.
     * @return configuration property object.
     * @throws MS_BadSetupException in case property cannot be found by provided key, either it cannot be cast to
     *                              desired type.
     * @see #getProperty(String, Class)
     * @see #getPropertyOptional(String, Class)
     */
    public <T> T getProperty(String key) throws MS_BadSetupException {
        return getConfiguration(key, null, getRootProperties());
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
     * @see #getProperty(String)
     * @see #getPropertyOptional(String, Class)
     */
    public <T> T getProperty(String key, Class<T> propertyClass) throws MS_BadSetupException {
        return getConfiguration(key, propertyClass, getRootProperties());
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
     * @see #getProperty(String)
     * @see #getProperty(String, Class)
     */
    public <T> T getPropertyOptional(String key, Class<T> propertyClass) throws MS_BadSetupException {
        return getConfigurationOptional(key, propertyClass, getRootProperties());
    }
}
