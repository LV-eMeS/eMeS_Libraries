package lv.emes.libraries.examples;

import lv.emes.libraries.tools.MS_Logger;

/** 
 * An example of log4j usage.
 */
public class LoggerExample {
	//logger config file (log4j.xml) should be placed in classpath folder. For example src or resources folder.
	public static void main(String[] args) {
		MS_Logger.getLogger(LoggerExample.class).info("Here you can add custom message for logging purposes.");		
		MS_Logger.getLogger(MS_Logger.class).error("Here you can add custom ERROR message for logging purposes.");		
		MS_Logger.getLogger("Custom name").info("You can also choose custom name for your messages.");		
	}	
}