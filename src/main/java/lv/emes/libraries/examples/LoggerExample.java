package lv.emes.libraries.examples;

import lv.emes.libraries.tools.logging.MS_Log4Java;

import java.time.ZonedDateTime;

/** 
 * An example of log4j usage.
 */
public class LoggerExample {
	//logger config file (log4j.xml) should be placed in classpath folder. For example src or resources folder.
	public static void main(String[] args) {
		System.out.println(ZonedDateTime.now());
		MS_Log4Java.getLogger(LoggerExample.class).info("Here you can add custom message for logging purposes.");
		MS_Log4Java.getLogger(MS_Log4Java.class).error("Here you can add custom ERROR message for logging purposes.");
		MS_Log4Java.getLogger("Custom name").info("You can also choose custom name for your messages.");
	}	
}