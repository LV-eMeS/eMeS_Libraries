package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.tools.MS_Tools;
import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.lists.MS_StringList;

import java.io.DataOutputStream;
import java.io.IOException;

/** 
 * TCP/IP client which operates commands to connect and communicate to server.
 * <p>Public methods:
 * -registerNewCommand
 * -getCommandList
 * -addDataToContainer
 * -cmdToServer
 * -getId
 * -connect
 * -disconnect
 * <p>Public properties to set:
 * -onServerGoingDown
 * <p>Protected methods:
 * -onIncomingServerMessage
 * -writeln
 * @author eMeS
 * @version 1.1.
 */
public class MS_TcpIPClient extends MS_TcpIPClientCore {
	//PUBLIC STRUCTURES, EXCEPTIONS AND CONSTANTS
	/**
	 * Set this property with lambda expression to do actions after server went down (disconnected).
	 * <br><u>Note</u>: communication with this server is already impossible at this moment.
	 * <p>() -&gt; {some action};
	 */
	public IFuncOnServerGoingDown onServerGoingDown = null;
	//PRIVĀTIE MAINĪGIE	
	private MS_List<MS_ClientCommand> commandList = new MS_List<MS_ClientCommand>();
	private MS_StringList dataContainer = new MS_StringList();
	private int id = 0;
	
	//PUBLIC VARIABLES
	//CONTRUCTORS
	/**
	 * Creates object and initializes default commands.
	 */
	public MS_TcpIPClient() {
		//set behavior of server disconnecting
		MS_ClientCommand tmp = new MS_ClientCommand();
		tmp.code = MS_ClientServerConstants.DC_NOTIFY_MESSAGE;
		tmp.doOnCommand = (client, data, out) -> {
			this.disconnect(); //break connection because it's already closed from server's side
			//user can define his own expected behavior when server goes down.
			if (onServerGoingDown != null)
				onServerGoingDown.doOnEvent();
		};
		this.registerNewCommand(tmp);
		
		//save this client's ID
		tmp = new MS_ClientCommand();
		tmp.code = MS_ClientServerConstants.NEW_CLIENT_ID_NOTIFY_MESSAGE;
		tmp.doOnCommand = (client, data, out) -> {
			id = data.getAsInteger(1); //save the id
		};
		this.registerNewCommand(tmp);
	}
	//STATISKIE KONSTRUKTORI	
	//PRIVĀTĀS METODES	
	//PUBLISKĀS METODES
	@Override
	protected void onIncomingServerMessage(String message, DataOutputStream out) {
		//Every time server sends a message client reads it. 
		//Messages are formatted in specific format.
		MS_StringList data = new MS_StringList(message);
		String userCmd = data.get(0);
		
		for (MS_ClientCommand cmd : commandList) 
			if (cmd.code.equals(userCmd)) { //command found
			cmd.doOnCommand.doMessageHandling(this, data, out);
			return;
		}		
	}
	
	/**
	 * Adds new command to command list. Commands are recognized by code. All commands must be unique. 
	 * Commands cannot be deleted, except by clearing command list.
	 * If server sends particular command to client, server acts just as command's implementation asks.
	 * @param cmd command code and method which will be triggered when client sends particular command.
	 */
	public void registerNewCommand(MS_ClientCommand cmd) {
		commandList.add(cmd);
	}
	
	/**
	 * That is recommended not to use this variable. 
	 * Use only if it is really necessary to do manual changes in list of already registered commands.
	 * @return List of all registered commands.
	 * @see MS_TcpIPClient#registerNewCommand
	 */
	public MS_List<MS_ClientCommand> getCommandList() {
		return commandList;
	}
	
	public int getId() {
		return id;
	}
	
	/**
	 * Tries to find command in registered command list. 
	 * @param cmdCode code of command.
	 * @return MS_ClientCommand or null if command doesn't exist in registered command list.
	 */
	public MS_ClientCommand getCommandByCode(String cmdCode) {
		for (MS_ClientCommand cmd : commandList)
			if (cmd.code.equals(cmdCode))
				return cmd;
		return null;
	}
	
	/**
	 * Adds some data for next command.
	 * @param someData textual data content to be added to send together with next command.
	 */
	public synchronized void addDataToContainer(String someData) {
		dataContainer.add(someData);
	}
	
	/**
	 * Use this method to send custom command to server!
	 * If some data added with <b>addDataToContainer</b>, those will also be sent by this message.
	 * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
	 * Data container is cleared after this method in every case.
	 * @param cmdCode command ID.
	 * @return false if error occurred during sending message or connection is not established.
	 * @see MS_TcpIPClient#addDataToContainer
	 */
	public synchronized boolean cmdToServer(String cmdCode) {
		if (! isConnected()) return false;
		dataContainer.insert(0, cmdCode);
		try {
			String message = dataContainer.toString();
			dataContainer.clear(); //after every message container is cleared
			this.out.writeUTF(message);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Sends last message to server that client is going offline.
	 */
	@Override
	protected void onDisconnectingFromServer() {
		//notify server that you are disconnecting!
		this.cmdToServer(MS_ClientServerConstants.CLIENT_DISCONNECTS_NOTIFY_MESSAGE);
	}
	
	/**
	 * Synonym for <b>isConnected</b>.
	 * @return whether client is ready for message exchange or not.
	 */
	@Override
	public boolean getIsActive() {
		return this.isConnected();
	}
	
	/**
	 * There is 4 things that server must know about client:<br>
	 * 1) OS of client;<br>
	 * 2) User name in OS of user;<br>
	 * 3) In OS directory where user launched his client's application;<br>
	 * 4) OS home directory of user;<br>
	 * Those things are sent as hello message to server. Client simply is introducing himself.
	 */
	@Override
	protected final void onSuccessfulConnect() {
		this.addDataToContainer(MS_Tools.getSystemOS);
		this.addDataToContainer(MS_Tools.getSystemUserName);
		this.addDataToContainer(MS_Tools.getSystemUserCurrentWorkingDir);
		this.addDataToContainer(MS_Tools.getSystemUserHomeDir);
		this.cmdToServer(MS_ClientServerConstants.INFO_ABOUT_NEW_CLIENT);
	}
}