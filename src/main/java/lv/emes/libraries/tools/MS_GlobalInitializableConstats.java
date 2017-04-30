package lv.emes.libraries.tools;

import lv.emes.libraries.file_system.MS_PropertiesFile;

import java.io.IOException;

/** 
 * This module can be overridden to operate with constants that is read from .properties file using static way to access them.
 * First of all you need to create a single instance of class, then you can change path to a .properties file. 
 * After this you can use method <b>updatePropsFromFile</b> to load everything in class static variables and use them later.
 * <p>Normally you just override this class to create more complicated class with more private and corresponding static variables. 
 * After overriding be sure you implement (if necessary) <b>setAdditionalProperties</b> and <b>doOnPropertiesReadException</b>.
 * <p>Example of use:<br>
 * 		MS_GlobalInitializableConstats consts = new MS_GlobalInitializableConstats("filename.properties");<br>
		consts.updatePropsFromFile();<br>
		System.out.println(MS_GlobalInitializableConstats.DB_NAME);
 * <p>Public methods:
 * -changePropsFileLocation
 * -setPropsFileLocation
 * -getCurrentPropsFileLocation
 * -updatePropsFromFile
 * <p>Methods to override:
 * -setProperties
 * -doOnPropertiesReadException
 * @version 1.3.
 * @author eMeS
 */
public class MS_GlobalInitializableConstats {
	/**
	 * Path to properties file.
	 */
	private static String propertiesFilePath;
	
	private static final String P_DB_HOSTNAME = "localhost";
	private static final String P_DB_NAME = "test_fake_java_software";
	private static final int P_DB_PORT = 3306;
	
	public static String DB_HOSTNAME = P_DB_HOSTNAME;
	public static String DB_NAME = P_DB_NAME;
	public static int DB_PORT = P_DB_PORT;
	
	//CONSTRUCTORS
	/**
	 * Create constants object and set filename of properties file.
	 * After this you should call <code>updatePropsFromFile()</code> to read properties from this file into static constant variables.
	 * @param propertiesFilename name of properties file.
	 */
	public MS_GlobalInitializableConstats(String propertiesFilename) {
		changePropsFileLocation(propertiesFilename);
	}
	
	/**
	 * Creates properties file with default name <b>constants.properties</b>. You can also change name of file using method <code>changePropsFileLocation()</code>.
	 * After this you should call <code>updatePropsFromFile()</code> to read properties from this file into static constant variables.
	 */
	public MS_GlobalInitializableConstats() {
		this("constants.properties");
	}	
	
	/**
	 * Overrides default application connection and configuration constants 
	 * by constants from <b>propertiesFilePath</b> file.
	 */
	public void updatePropsFromFile() {
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
	 * @param propsFile properties file.
	 */
	protected void setProperties(MS_PropertiesFile propsFile) {
		DB_HOSTNAME = propsFile.getProperty("DB_HOSTNAME", P_DB_HOSTNAME);
		DB_NAME = propsFile.getProperty("DB_NAME", P_DB_NAME);
		DB_PORT = Integer.parseInt(propsFile.getProperty("DB_PORT", String.valueOf(P_DB_PORT) ));
	}
		
	/**
	 * Set this if you want to handle properties read errors differently from storing error message for log4j to handle.
	 * @param exc an occurred exception.
	 */
	protected void doOnPropertiesReadException(IOException exc) {
		MS_Log4Java.getLogger(MS_GlobalInitializableConstats.class).error("Couldn't read properties from file", exc);
		
	}
}