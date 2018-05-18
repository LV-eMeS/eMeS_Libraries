package lv.emes.libraries.file_system;

import java.io.*;
import java.util.Properties;

/**
 * Can be used to work with .properties files.
 * You can either read properties from files or write new properties, or even rewrite existing properties.
 *
 * @version 1.3.
 */
public class MS_PropertiesFile extends Properties {

    private static final long serialVersionUID = -7488390805687561806L;

    /**
     * Loads all properties from file with specified path.
     *
     * @param aPropertyFilename path to file
     * @throws IOException if file not found or cannot be read
     */
    public void load(String aPropertyFilename) throws IOException {
        InputStream in = new FileInputStream(aPropertyFilename);
        this.load(new InputStreamReader(in, "UTF-8"));
        in.close();
    }

    /**
     * Saves all the properties to file with specified path.
     *
     * @param aPropertyFilename path to file
     * @param aComment          optional, if don't need, just leave blank! NOT null!
     * @throws IOException if file not found or cannot be replaced
     */
    public void save(String aPropertyFilename, String aComment) throws IOException {
        FileOutputStream out = new FileOutputStream(aPropertyFilename);
        this.store(out, aComment);
        out.close();
    }

    /**
     * Searches for the property with the specified key in this property list and converts it's value from
     * string to integer.
     *
     * @param key key of value we are looking for.
     * @return the value in this property list with the specified key value.
     * @throws NumberFormatException if such property isn't found or cannot be converted to integer.
     */
    public int getInt(String key) throws NumberFormatException {
        String tmp = this.getProperty(key);
        return Integer.parseInt(tmp == null ? "" : tmp);
    }

    /**
     * Searches for the property with the specified key in this property list. If the key is not found in this
     * property list, the default property list, and its defaults, recursively, are then checked.
     * The method returns the default value argument if the property is not found.
     *
     * @param key          key of value we are looking for.
     * @param defaultValue return value if key doesn't exist or value is incorrect.
     * @return the value in this property list with the specified key value.
     */
    public int getInt(String key, int defaultValue) {
        String tmp = this.getProperty(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(tmp);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
