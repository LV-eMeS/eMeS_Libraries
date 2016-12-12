package lv.emes.libraries.examples;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.server.MS_ServerCommand;
import lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_FileSystemTools;

import java.io.IOException;

public class MSTcpIPServerExample {

	public static void main(String[] args) {
		int portNumber = MS_ClientServerConstants.DEFAULT_PORT_FOR_TESTING; //default port for this application
		try {
			portNumber = Integer.parseInt(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MS_ServerCommand cmd;
		
		//declare server which will use given port number when it will start
		MS_TcpIPServer server = new MS_TcpIPServer(portNumber);
		server.onClientConnecting = (client) -> {
			System.out.println("New client with IP address: " + client.getIp() + " connected.");
		};
		
		server.onClientSayingHi = (client) -> {
			System.out.println("Client's name: " + client.getOSUserName());
			System.out.println("Client's OS: " + client.getOSName());
			System.out.println("Client's working directory: " + client.getCurrentWorkingDirectory());
			System.out.println("Client's home directory: " + client.getSystemHomeDirectory());
		};
		
		//init all the commands!
		//beware of using commands that is defined in MS_ClientServerConstants		
		cmd = new MS_ServerCommand("Print incoming text!");
		cmd.doOnCommand = (srvr, data, cli, out) -> {
			System.out.println(data.get(1)); //data starts with first element. Command code is stored in zero'th element
		};	
		server.registerNewCommand(cmd);
		
		cmd = new MS_ServerCommand("Do server shutdown!");
		cmd.doOnCommand = (srvr, data, cli, out) -> {
			srvr.stopServer();
			System.out.println("Job done. Application closes.");
		};	
		server.registerNewCommand(cmd);
		
		cmd = new MS_ServerCommand("Send binary file to client now!");
		cmd.doOnCommand = (srvr, data, cli, out) -> {
			//load image from resources folder into byte array
			byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream("test_pic.png"));
			//prepare to send byte array as text
			srvr.addDataToContainer(MS_BinaryTools.bytesToString(by));
			//send both command "Read binary file that I sent to you!" and binary data to client, which sent command "Send binary file to client now!" to server.
			srvr.cmdToClient("Read binary file that I sent to you!", cli);			
		};	
		server.registerNewCommand(cmd);
		
		try {
			server.startServer();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
