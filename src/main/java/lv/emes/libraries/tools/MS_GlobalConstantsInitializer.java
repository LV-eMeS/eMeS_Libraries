package lv.emes.libraries.tools;

import lv.emes.libraries.file_system.MS_PropertiesFile;
import lv.emes.libraries.tools.logging.MS_Log4Java;

import java.io.IOException;

/** 
 * This module can be overridden to operate with constants that is read from .properties file using static way to access them.
 * First of all you need to create a single instance of class, then you can change path to a .properties file. 
 * After this you can use method <b>updatePropsFromFile</b> to load everything in class static variables and use them later.
 * <p>Normally you just override this class to create more complicated class with more private and corresponding static variables. 
 * After overriding be sure you implement (if necessary) <b>setProperties</b> and <b>doOnPropertiesReadException</b>.
 * <p>Example of use:<br>
 * 		MS_GlobalConstantsInitializer consts = new MS_GlobalConstantsInitializer("filename.properties");<br>
		consts.updatePropsFromFile();<br>
		System.out.println(MS_GlobalConstantsInitializer._DB_HOSTNAME);
 * <p>Public methods:
 * <ul>
 *     <li>changePropsFileLocation</li>
 *     <li>setPropsFileLocation</li>
 *     <li>getCurrentPropsFileLocation</li>
 *     <li>updatePropsFromFile</li>
 * </ul>
 * <p>Methods to override:
 * <ul>
 *     <li>setProperties</li>
 *     <li>doOnPropertiesReadException</li>
 * </ul>
 * @version 1.4.
 * @author eMeS
 */
public abstract class MS_GlobalConstantsInitializer {

	/**
	 * Path to properties file.
	 */
	private static String propertiesFilePath;

	//variables containing values of constants
//	private static final String P_DB_HOSTNAME = "localhost";

//	public static String _DB_HOSTNAME = P_DB_HOSTNAME;

	//CONSTRUCTORS
	/**
	 * Create constants object and set filename of properties file.
	 * After this you should call <code>updatePropsFromFile()</code> to read properties from this file into static constant variables.
	 * @param propertiesFilename name of properties file.
	 */
	public MS_GlobalConstantsInitializer(String propertiesFilename) {
		changePropsFileLocation(propertiesFilename);
	}
	
	/**
	 * Overrides default application connection and configuration constants 
	 * by constants from <b>propertiesFilePath</b> file.
	 */
	public final void updatePropsFromFile() {
		MS_PropertiesFile props = new MS_PropertiesFile();
		try {
			props.load(propertiesFilePath);
			setProperties(props);
		} catch (IOException e) {
			doOnPropertiesReadException(e);
		}
	}
	
	/**
	 * Synonym of <b>setPropsFileLocation</b>. Sets filename for properties file.
	 * @param newFilename path to .properties file.
	 */
	public void changePropsFileLocation(String newFilename) {
		propertiesFilePath = newFilename;
	}
	
	/**
	 * Synonym of <b>changePropsFileLocation</b>. Sets filename for properties file.
	 * @param newFilename path to .properties file.
	 */
	public void setPropsFileLocation(String newFilename) {
		propertiesFilePath = newFilename;
	}
	
	/**
	 * @return path to .properties file.
	 */
	public static String getCurrentPropsFileLocation() {
		return propertiesFilePath;
	}
	
	//PROTECTED METHODS

	/**
	 * Override this method to set values of static variables.
	 * <p>You should define private static final variable with name "P_VARIABLE" for storing default value.
	 * <p>You also need corresponding public static variable with name "VARIABLE", which value will be set here.
	 * <p>Then just use line "VARIABLE = propsFile.getProperty("VARIABLE", P_VARIABLE);" in this method to set this value!
	 * <p><code>_DB_HOSTNAME = propsFile.getProperty("db_hostname", P_DB_HOSTNAME);</code>
	 * @param propsFile properties file.
	 */
	protected abstract void setProperties(MS_PropertiesFile propsFile);

	/**
	 * Set this if you want to handle properties read errors differently from storing error message for log4j to handle.
	 * @param exc an occurred exception.
	 */
	protected void doOnPropertiesReadException(IOException exc) {
		MS_Log4Java.getLogger(MS_GlobalConstantsInitializer.class).error("Couldn't read properties from file", exc);
	}
}