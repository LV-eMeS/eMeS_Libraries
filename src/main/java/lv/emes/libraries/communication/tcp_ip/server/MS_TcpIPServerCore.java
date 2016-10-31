package lv.emes.libraries.communication.tcp_ip.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import lv.emes.libraries.communication.tcp_ip.MS_TcpIPAbstract;

//import org.apache.log4j.Logger;

//import lv.emes.tools.MS_StringTools;

/** 
 * Core things of TCP/IP server. Accessible only to package because will be overridden.
 * <p>Public methods:
 * -startServer
 * -stopServer
 * -isRunning
 * -setPort
 * -getPort
 * -getClients
 * -getClientByID
 * -disconnectClientByID
 * -disconnectAllClients
 * -writeBytes
 * -readBytes
 * <p>Public properties to set:
 * -onUTFDataFormatException
 * -onIOException
 * <p>Protected methods:
 * -onIncomingClientMessage
 * -onNewClientConnected
 * -writeln
 * @author eMeS
 * @version 1.2.
 */
abstract class MS_TcpIPServerCore extends MS_TcpIPAbstract {
	//PRIVĀTIE MAINĪGIE
//	static Logger log = Logger.getLogger(MS_TcpServerCore.class.getName());
	private int port;
	private ServerSocket server;
	private int lastClientId = 0;		

	protected Thread serverThread = null; //thread of server
	protected Vector<MS_ClientOfServer> clients;	
	/**
	 * Set this to define behavior of server when it reads message of client.
	 * @param message UTF-8 text from client.
	 * @param client sender.
	 * @param out output stream of client to send server's response back to client.
	 */
	abstract protected void onIncomingClientMessage(String message, MS_ClientOfServer client, DataOutputStream out);
	/**
	 * Set this to define behavior of server when it new client is connected.
	 * @param ClientID newly connected client's ID.
	 */
	abstract protected void onNewClientConnected(MS_ClientOfServer client);
	
	/**
	 * Writes message to particular client.
	 * @param msg UTF-8 text.
	 * @param clientID id of client.
	 */
	protected synchronized void writeln(String msg, int clientID) {
		try {
			this.getClientByID(clientID).getOut().writeUTF(msg);
		} catch (IOException e) {
//			log.error(String.format("Couldn't write UTF-8 message to client. \n. @writeln(%s, %d)", MS_StringTools.eMeSSubstring(msg, 1, 50) + "...", clientID));
		}
	}
	
	//PUBLISKIE MAINĪGIE
	
	//KONSTRUKTORI
	/**
	 * Creates new server instance and sets its port.
	 * @param port 1..~65k.
	 */
	public MS_TcpIPServerCore(int port) {
		this.port = port;
		clients = new Vector<MS_ClientOfServer>();
	}
	
	//STATISKIE KONSTRUKTORI
	
	//PRIVĀTĀS METODES
	
	//PUBLISKĀS METODES	
	/**
	 * Changes port in which server will be listening. If this will be done when server is already launched, this will do no effect.
	 * You should change this property only before method <b>startServer</b>.
	 * @param portNumber particular port number (1..65k)
	 */
	public void setPort(int portNumber) {
		port = portNumber;		
	}
	
	public int getPort() {
		return port;
	}
	
	/**
	 * Starts server at defined port. New thread is created and server starts listening for incoming client connections.
	 * @throws IllegalArgumentException if server invalid port defined during creation of server object.
	 * @throws IOException if server cannot be started due to an I/O error when opening the socket.
	 */
	public void startServer() throws IllegalArgumentException, IOException {		
		server = new ServerSocket(this.getPort());	
		Thread t = new Thread(this);
		serverThread = t; //saving for future needs
		isActive = true;
		t.start(); //code goes to run() method		
	}
	
	/**
	 * Stops server for listening.
	 */
	public void stopServer() {
		if (! isActive) return;
		serverThread.interrupt(); //do not listen for new connections anymore!
		isActive = false;
		try {
			server.close();
		} catch (Exception e) {
		}
		server = null;	
		if (clients.size() > 0)
			clients.clear(); //also delete all connections
	}	

	/**
	 * Gets information about server online status.
	 * @return true if server is running; false, if it's down.
	 */
	public synchronized boolean isRunning() {
		return (server != null) && isActive;
	}

	/**
	 * This will listen for new client connections and creates new thread for every connected client.
	 */
	@Override
	public void run() {
		try {
			//starts server and waits for new client to establish connection
			while (isActive){		
				Socket socket = server.accept(); //incoming client	
				MS_ClientOfServer client = new MS_ClientOfServer(++lastClientId, socket); //assigns id of client and adds it to 
				clients.addElement(client);
				MS_MessageHandler worker = new MS_MessageHandler(client, this);				
				Thread t = new Thread(worker);
				t.setDaemon(true);
				t.start();	
				
				onNewClientConnected(client); //calls method to do actions after client is successfully connected
			}			
		} catch (Exception e) {
			//everything is ok, because threads are slower that infinite loop, so isRunning didn't catch up with actual situation
		} 	
		finally {
			stopServer();	
		}
	}
	
	/**
	 * @return all the connected clients.
	 */
	public Vector<MS_ClientOfServer> getClients() {
		return clients;
	}
	
	/**
	 * Disconnects particular client. Also removes client from <b>clients</b> list.
	 * @param clientId id of client.
	 */
	public void disconnectClientByID(int clientId) {
		MS_ClientOfServer cl = this.getClientByID(clientId);
		cl.disconnect();
		this.clients.remove(cl);
	}
	
	/**
	 * Disconnects all clients. Also removes clients from <b>clients</b> list.
	 * Server is still running after execution of this method.
	 */
	public synchronized void disconnectAllClients() {
		this.lastClientId = 0; //id counter can be restarted, cause no more clients connected => This way ID's cannot conflict with new connection ID's.
		for (MS_ClientOfServer cl : clients) 
			cl.disconnect();
		this.clients.clear();			
	}
	
	/**
	 * Goes through the list of connected clients and seeks for particular client. 
	 * @param clientId id (starting with 1) of client to look for.
	 * @return MS_ClientOfServer if client found; otherwise returns null.
	 */
	public MS_ClientOfServer getClientByID(int clientId) {
		for (MS_ClientOfServer cl : clients) 
			if (cl.id == clientId)
				return cl;
		return null;
	}
}