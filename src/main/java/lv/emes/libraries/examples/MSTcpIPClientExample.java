package lv.emes.libraries.examples;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.client.MS_ClientCommand;
import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.file_system.MS_BinaryTools;

import java.io.IOException;

public class MSTcpIPClientExample {

	public static final String BINARY_DEST_FILE_CLIENT = "src/main/java/lv/emes/examples/exampleFileFromServer.png";
	public static final String HOST_TO_CONNECT_TO = "localhost";
	public static final int PORT_OF_HOST = MS_ClientServerConstants._DEFAULT_PORT_FOR_TESTING;

	public static void main(String[] args) throws IllegalArgumentException, IOException {
		MS_TcpIPClient client = new MS_TcpIPClient();
		client.onIOException = (exception) -> {
			System.out.println("Some exception happened while reading message from server.");
		};
		client.onServerGoingDown = () -> {			System.out.println("Server just went down.");			};
		client.onUTFDataFormatException = (exc) -> {System.out.println("Server just tried to send incorrect command.");};
		//lets register command for message listener of client
		MS_ClientCommand cmd = new MS_ClientCommand("Read binary file that I sent to you!");
		cmd.doOnCommand = (cli, data, out) -> {
			//save file to local hard disk
			byte[] bytesFromServer = MS_BinaryTools.stringToBytes(data.get(1));
			try {
				MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromServer), BINARY_DEST_FILE_CLIENT);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//says thanks to server
			cli.addDataToContainer("Thanks for the file you sent to me! Everything went very well!!!");
			client.cmdToServer("Print incoming text!"); //you can use both `cli and `client variables here
			
			//tell server to shutdown!
			cli.cmdToServer("Do server shutdown!");
			
			//after this code going to above method client.onServerGoingDown
		}; 
		client.registerNewCommand(cmd);
		
		//establish connection
		client.connect(HOST_TO_CONNECT_TO, PORT_OF_HOST);
		
		//together with command you can always send some data for server to handle
		client.addDataToContainer("Hello, world from client!"); //in this case we will add text for server to print in command line
		client.cmdToServer("Print incoming text!"); //this is command which is recognized by server that he needs to print text to command line
		//additionally together with this command client sends added data too
		//after executing the command data is cleared from buffer
		
		//now lets download binary file from server
		client.cmdToServer("Send binary file to client now!"); //no data need to add if you want just send plain command
		//now rest of code will execute in another thread
		//server will send binary file back to client and client will do command "Read binary file that I sent to you!"
	}
}
