package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.MS_TakenPorts;

public class MS_ClientServerConstants {
	public static final String DEFAULT_HOST = "localhost"; 
	public static final String DC_NOTIFY_MESSAGE = "<Notify All clients on DC>";
	public static final String NEW_CLIENT_ID_NOTIFY_MESSAGE = "<Hello, your ID is...>";
	public static final String INFO_ABOUT_NEW_CLIENT = "<Hello, server! I am sending data about me>";
	public static final String CLIENT_DISCONNECTS_NOTIFY_MESSAGE = "<I am going offline. Bye, bye!>";
	public static final int DEFAULT_PORT_FOR_TESTING = MS_TakenPorts.DEFAULT_PORT_FOR_TESTING;
}
