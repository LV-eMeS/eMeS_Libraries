package lv.emes.libraries.tools.logging;

import org.apache.log4j.Logger;

/**
 * This class just uses functionality of Apache Log4j 1.x.
 * A logging can be done only if <b>log4j.xml</b> is found in
 * classpath (it's recommended to store this file in resources package).
 * <p>Methods:
 * <ul>
 * <li>getLogger(Class callerClass)</li>
 * <li>getLogger(String name)</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 * @see lv.emes.libraries.examples.LoggerExample
 */
public class MS_Log4Java {

    private MS_Log4Java() {
    }

    /**
     * Creates reference to logger with given class name.
     *
     * @param callerClass class, from which logger is called.
     * @return log4j logger.
     */
    public static Logger getLogger(Class<?> callerClass) {
        return Logger.getLogger(callerClass);
    }

    /**
     * Creates reference to logger with given name.
     *
     * @param name given name to recognize particular message from different logger messages.
     * @return log4j logger.
     */
    public static Logger getLogger(String name) {
        return Logger.getLogger(name);
    }

	/*
	<?xml version="1.0" encoding="iso-8859-1"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd" >
<log4j:configuration debug="false">

    <appender name="default.console" class="org.apache.log4j.ConsoleAppender">
        <param name="target" value="System.out" />
        <param name="threshold" value="debug" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{ISO8601} %-5p [%c{1}] line:%L - %m%n" />
        </layout>
    </appender>

    <appender name="default.file" class="org.apache.log4j.FileAppender">
        <param name="file" value="eMeS_Libraries.log" />
        <param name="append" value="true" />
        <param name="threshold" value="debug" />
        <layout class="org.apache.log4j.PatternLayout">
        <!-- %d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n -->
            <param name="ConversionPattern" value="%d{ISO8601} %-5p [%c{1}] line:%L - %m%n" />
        </layout>
    </appender>

<!--
    <appender name="another.file" class="org.apache.log4j.FileAppender">
        <param name="file" value="/log/anotherlogfile.log" />
        <param name="append" value="false" />
        <param name="threshold" value="debug" />
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%d{ISO8601} %-5p [%c{1}] - %m%n" />
        </layout>
    </appender>
        -->

<!--
    <logger name="com.yourcompany.SomeClass" additivity="false">
        <level value="debug" />
        <appender-ref ref="another.file" />
    </logger>
    -->

    <root>
        <priority value="info" />
        <appender-ref ref="default.console" />
        <appender-ref ref="default.file" />
    </root>
</log4j:configuration>
	 */
}
