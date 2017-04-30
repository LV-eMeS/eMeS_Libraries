package lv.emes.libraries.tools;

import org.apache.log4j.Logger;

/** 
 * This class just uses functionality of Apache Log4j 1.x. It is meant for <u>static use only</u>.
 * A logging can be done only if <b>log4j.xml</b> is found in classpath (it's recommended to store this file in resources package).
 * Log file is created after first call of get logger method.
 * <br><u>Warning</u>: this logger isn't thread safe as it wraps original logger and statically binds it to this class.
 * <p>Methods:
 * -getLogger
 * @version 1.1.
 * @author eMeS
 * @see lv.emes.libraries.examples.LoggerExample
 */
public class MS_Log4Java {

	private MS_Log4Java() {
	}

	//PRIVATE VARIABLES
	private static Logger log = null;
	
	//PUBLIC METHODS
	/**
	 * Creates reference to logger with given class name.
	 * @param callerClass class, from which logger is called.
	 * @return log4j logger.
	 */
	public static Logger getLogger(Class<?> callerClass) {
		if ((log == null) || ( ! log.getName().equals(callerClass.getName()))) 
			log = Logger.getLogger(callerClass.getName());
		return log;		
	}
	
	/**
	 * Creates reference to logger with given name.
	 * @param name given name to recognize particular message from different logger messages.
	 * @return log4j logger.
	 */
	public static Logger getLogger(String name) {
		if ((log == null) || ( ! log.getName().equals(name))) 
			log = Logger.getLogger(name);
		return log;		
	}
}
