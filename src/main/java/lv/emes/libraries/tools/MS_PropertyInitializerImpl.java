package lv.emes.libraries.tools;

import lv.emes.libraries.file_system.MS_PropertyFileManager;
import lv.emes.libraries.utilities.MS_StringUtils;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * Implementation of enumerations as properties.
 * This class should be used in enumeration, which has values named by property names and which also implements {@link MS_PropertyInitializer}.
 * <p>Example of use:
 * <pre>
 * {@code
 * public enum SomeEnumeration implements MS_PropertyInitializer {
 *  SOME_ENUM_VALUE("default property value for this enum");
 *  private MS_PropertyInitializer impl;
 *  SomeEnumeration(String defaultValue) {
 *      impl = new MS_PropertyInitializerImpl(this, "fileName.properties", defaultValue);
 *  }
 *  // Boilerplate implementations of getProperty() method by using given implementation
 *  public String getProperty() {
 *      return impl.getProperty();
 *  }
 *  public <T> T getProperty(Class<T> valueClass) {
 *      return impl.getProperty(valueClass);
 *  }
 * }
 * }
 * </pre>
 *
 * @author eMeS
 * @version 2.0.
 * @since 2.2.3
 */
public class MS_PropertyInitializerImpl implements MS_PropertyInitializer {

    private final Enum en;
    private final String propertiesFilePath;
    private Supplier<Properties> props = Lazy.lazily(() -> props = Lazy.value(getProps()));

    private final String defaultValue;
    private boolean useDefaultValue;

    /**
     * Creates implementation that does not provide default value if property is not found.
     *
     * @param en                 enumeration which uses this particular property initializer implementation.
     * @param propertiesFilename name of properties file.
     * @throws MS_BadSetupException if properties file cannot be read.
     */
    public MS_PropertyInitializerImpl(Enum en, String propertiesFilename) {
        this(en, propertiesFilename, null);
        this.useDefaultValue = false;
    }

    /**
     * Creates implementation that provides default value if property is not found.
     *
     * @param en                 enumeration which uses this particular property initializer implementation.
     * @param propertiesFilename name of properties file.
     * @param defaultValue       default value of property if it's not defined in file.
     * @throws MS_BadSetupException if properties file cannot be read.
     */
    public MS_PropertyInitializerImpl(Enum en, String propertiesFilename, String defaultValue) {
        this.propertiesFilePath = propertiesFilename;
        this.defaultValue = defaultValue;
        this.en = en;
        this.useDefaultValue = true;
    }

    public void setUseDefaultValue(boolean useDefaultValue) {
        this.useDefaultValue = useDefaultValue;
    }

    @Override
    public String getProperty() {
        if (useDefaultValue) {
            try {
                return props.get().getProperty(en.name(), defaultValue);
            } catch (MS_BadSetupException e) { //file not found or read; nevertheless use default value
                // for next calls assure that file manager will not be disturbed anymore
                makePropertiesSupplierAlwaysThrowException(e);
                return defaultValue;
            }
        } else {
            return props.get().getProperty(en.name());
        }
    }

    @Override
    public <T> T getProperty(Class<T> valueClass) {
        String property;
        if (useDefaultValue) {
            try {
                property = props.get().getProperty(en.name(), defaultValue);
            } catch (MS_BadSetupException e) { //file not found or read; nevertheless use default value
                // for next calls assure that file manager will not be disturbed anymore
                makePropertiesSupplierAlwaysThrowException(e);
                property = defaultValue;
            }
        } else {
            property = props.get().getProperty(en.name());
        }
        return MS_StringUtils.stringToPrimitive(property, valueClass);
    }

    /**
     * Lazily gets properties.
     *
     * @return properties read from file.
     */
    private Properties getProps() {
        return MS_PropertyFileManager.getProperties(propertiesFilePath);
    }

    private void makePropertiesSupplierAlwaysThrowException(MS_BadSetupException e) {
        props = () -> {throw e;};
    }
}