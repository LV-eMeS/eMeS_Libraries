package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.tools.lists.MS_StringList;

/** 
 * TCP/IP common core things that contains methods for communication. 
 * This class will be overridden to implement MS_TcpIPClient and MS_TcpIPServer.
 * @author eMeS
 * @version 1.2.
 * @see lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient
 * @see lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer
 */
public abstract class MS_TcpIPAbstract implements Runnable {
	//PUBLISKĀS STRUKTŪRAS, IZŅĒMUMI UN KONSTANTES
	/**
	 * Set this to handle this kind of error when trying to read message sent by communication partner!
	 * <p>(exception) -> {};
	 */
	public IFuncOnUTFDataFormatException onUTFDataFormatException = (exception) -> {exception.printStackTrace();}; 
	/**
	 * Set this to handle this kind of error when trying to read message sent by communication partner!
	 * <p>(exception) -> {};
	 */
	public IFuncOnIOException onIOException = (exception) -> {exception.printStackTrace();}; 

	//PRIVĀTIE MAINĪGIE
	protected boolean isActive = false; 

	protected MS_StringList dataContainer = new MS_StringList();
	protected static final int BYTE_BUFFER_SIZE = 1024;

	//PRIVATE METHODS	
	//PUBLIC METHODS
	/**
	 * Adds some data for next command.
	 * @param someData textual data content to be added to send together with next command.
	 */
	public synchronized void addDataToContainer(String someData) {
		dataContainer.add(someData);
	}
	
	public abstract boolean getIsActive();
}
