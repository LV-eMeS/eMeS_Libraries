package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.MS_TakenPorts;

public class MS_ClientServerConstants {

	public static final String _DEFAULT_HOST = "localhost";
	public static final String _DC_NOTIFY_MESSAGE = "<Notify All clients on DC>";
	public static final String _NEW_CLIENT_ID_NOTIFY_MESSAGE = "<Hello, your ID is...>";
	public static final String _INFO_ABOUT_NEW_CLIENT = "<Hello, server! I am sending data about me>";
	public static final String _CLIENT_DISCONNECTS_NOTIFY_MESSAGE = "<I am going offline. Bye, bye!>";
	public static final Integer _DEFAULT_PORT_FOR_TESTING = MS_TakenPorts._DEFAULT_PORT_FOR_TESTING;
}
