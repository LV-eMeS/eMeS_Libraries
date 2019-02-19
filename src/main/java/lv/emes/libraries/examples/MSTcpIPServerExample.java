package lv.emes.libraries.examples;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.MS_TcpIPCommand;
import lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_FileSystemTools;

import java.io.IOException;

public class MSTcpIPServerExample {

	public static void main(String[] args) {
		int portNumber = MS_ClientServerConstants._DEFAULT_PORT_FOR_TESTING; //default port for this application
		try {
			portNumber = Integer.parseInt(args[0]); //set port number if given as an argument
		} catch (Exception ignored) {
		}

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
		//beware of using commands with codes that are defined in MS_ClientServerConstants
		server.registerCommandWithStringData("Print incoming text!", (srvr, cli, data) -> System.out.println(data));

		server.registerCommandWithNoData("Do server shutdown now!", (srvr, cli) -> {
			srvr.stopServer();
			System.out.println("Job done. Application closes.");
		});

		server.registerCommandWithNoData("Send binary file from resources!", (srvr, cli) -> {
			//load image from resources folder into byte array
			// Note: Make sure that you copy "test_pic.png" from "src/test/resources/test_pic.png"
			// to "src/main/resources/test_pic.png" first so that it would be accessible as resource from these Examples classes!
			byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream("test_pic.png"));
			//send command "Read binary file that I sent to you!" with binary data to client
			srvr.cmdToClient(new MS_TcpIPCommand("Read binary file that I sent to you!", by), cli);
		});
		
		try {
			server.startServer();
			//as new thread is created, app will not be terminated unless that thread will be terminated (on srvr.stopServer())
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
