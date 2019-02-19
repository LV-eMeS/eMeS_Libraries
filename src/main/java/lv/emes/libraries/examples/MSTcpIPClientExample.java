package lv.emes.libraries.examples;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.MS_TcpIPCommand;
import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.file_system.MS_BinaryTools;

import java.io.IOException;

public class MSTcpIPClientExample {

    private static final String BINARY_DEST_FILE_CLIENT = "exampleFileFromServer.png";
    private static final String HOST_TO_CONNECT_TO = "localhost";
    private static final int PORT_OF_HOST = MS_ClientServerConstants._DEFAULT_PORT_FOR_TESTING;

    public static void main(String[] args) throws IllegalArgumentException, IOException {
        MS_TcpIPClient client = new MS_TcpIPClient();
        client.onIOException = (exception) -> System.out.println("Some exception happened while reading message from server.");
        client.onServerGoingDown = () -> System.out.println("Server just went down.");
        client.onDataFormatException = (exc) -> System.out.println("Server just tried to send incorrect command.");

        //lets register only command for message listener of client - to receive file that server is sending
        client.registerCommandWithBinaryData("Read binary file that I sent to you!", (cli, data) -> {
            //save file to local hard disk
            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(data), BINARY_DEST_FILE_CLIENT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //says thanks to server
            // Note that you can use both `cli and `client variables here
            client.cmdToServer(new MS_TcpIPCommand("Print incoming text!", "Thanks for the file you sent to me! Everything went very well!!!"));

            //tell server to shutdown!
            cli.cmdToServer(new MS_TcpIPCommand("Do server shutdown now!"));

            //after execution of this code eventually method above will be executed: client.onServerGoingDown
        });

        //establish connection
        client.connect(HOST_TO_CONNECT_TO, PORT_OF_HOST);

        // together with command you can always send some data of different type for server to handle
        // server should also know, what type of data is coming together with specific command
        // in this case we will add text for server to print in command line
        // this is command which is recognized by server that he needs to print text to command line
        client.cmdToServer(new MS_TcpIPCommand("Print incoming text!", "Hello, world from client!"));
        //additionally together with this command client sends added data too
        //after executing the command data is cleared from buffer

        //now lets download binary file from server
        client.cmdToServer(new MS_TcpIPCommand("Send binary file from resources!")); //no data need to add if you want just send plain request
        //now rest of code will execute in another thread
        //server will send binary file back to client and client will do command "Read binary file that I sent to you!"
    }
}
