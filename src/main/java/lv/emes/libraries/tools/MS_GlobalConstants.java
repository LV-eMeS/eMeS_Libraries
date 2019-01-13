package lv.emes.libraries.tools;

import lv.emes.libraries.file_system.MS_PropertiesFile;
import lv.emes.libraries.file_system.MS_PropertyFileManager;

/**
 * This module can be overridden to operate with constants that is read from .properties file using static way to access them.
 * First of all you need to create a single instance of class, then you can change path to a .properties file.
 * After this you can use method <b>updatePropsFromFile</b> to load everything in class static variables and use them later.
 * <p>Normally you just override this class to create more complicated class with more private and corresponding static variables.
 * After overriding be sure you implement (if necessary) <b>initProperties</b> and <b>doOnPropertiesReadException</b>.
 * <p>Example of use:<br>
 * MS_GlobalConstants consts = new MS_GlobalConstants("filename.properties");<br>
 * System.out.println(MS_GlobalConstants._DB_HOSTNAME);
 * <p>Public methods:
 * <ul>
 * <li>changePropsFileLocation</li>
 * <li>setPropsFileLocation</li>
 * <li>getCurrentPropsFileLocation</li>
 * <li>updatePropsFromFile</li>
 * </ul>
 * <p>Methods to override:
 * <ul>
 * <li>initProperties</li>
 * <li>doOnPropertiesReadException</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 * @since 2.2.2
 */
public abstract class MS_GlobalConstants {

    /**
     * Path to properties file.
     */
    private String propertiesFilePath;

    //variables containing values of constants
	//private static final String DB_HOSTNAME = "localhost";

	//public static String _DB_HOSTNAME = DB_HOSTNAME;

    /**
     * Create constants object, set filename of properties file and load those.
     *
     * @param propertiesFilename name of properties file.
     */
    public MS_GlobalConstants(String propertiesFilename) {
        this(propertiesFilename, true);
    }

    /**
     * Create constants object and set filename of properties file. If <b>autoInitProperties</b> = <tt>false</tt> then
     * after this you should call <code>updatePropsFromFile()</code> to read properties from this file into constant holder variables.
     *
     * @param propertiesFilename name of properties file.
     * @param autoInitProperties flag, whether <code>updatePropsFromFile()</code> should be called right after creating
     *                           this instance.
     */
    public MS_GlobalConstants(String propertiesFilename, boolean autoInitProperties) {
        changePropsFileLocation(propertiesFilename);
        if (autoInitProperties)
            updatePropsFromFile();
    }

    /**
     * Loads properties file and sets all the properties by calling {@link MS_GlobalConstants#initProperties(MS_PropertiesFile)}.
     */
    public final void updatePropsFromFile() {
        try {
            MS_PropertiesFile props = MS_PropertyFileManager.getProperties(propertiesFilePath);
            initProperties(props);
        } catch (MS_BadSetupException e) {
            doOnPropertiesReadException(e);
        }
    }

    /**
     * Synonym of <b>setPropsFileLocation</b>. Sets filename for properties file.
     *
     * @param newFilename path to .properties file.
     */
    public void changePropsFileLocation(String newFilename) {
        propertiesFilePath = newFilename;
    }

    /**
     * Synonym of <b>changePropsFileLocation</b>. Sets filename for properties file.
     *
     * @param newFilename path to .properties file.
     */
    public void setPropsFileLocation(String newFilename) {
        propertiesFilePath = newFilename;
    }

    /**
     * @return path to .properties file.
     */
    public String getCurrentPropsFileLocation() {
        return propertiesFilePath;
    }

    /**
     * Override this method to set values of static variables.
     * <p>You should define private static final variable with name "P_VARIABLE" for storing default value.
     * <p>You also need corresponding public static variable with name "VARIABLE", which value will be set here.
     * <p>Then just use line "VARIABLE = propsFile.getProperty("VARIABLE", P_VARIABLE);" in this method to set this value!
     * <p><code>_DB_HOSTNAME = propsFile.getProperty("db_hostname", P_DB_HOSTNAME);</code>
     *
     * @param propsFile properties file.
     */
    protected abstract void initProperties(MS_PropertiesFile propsFile);

    /**
     * Set this if you want to handle properties read errors instead of just throwing exception!
     *
     * @param exc an occurred exception.
     */
    protected void doOnPropertiesReadException(MS_BadSetupException exc) {
        throw exc;
    }
}