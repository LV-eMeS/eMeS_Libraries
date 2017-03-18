package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.client.MS_ClientCommand;
import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_FileSystemTools;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Array;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSTpcIPServerTest {
    private static final int PORT = 12343;
    private static MS_TcpIPServer server;
    private static MS_TcpIPClient client1;
    private static MS_TcpIPClient client2;
    private static boolean serverIsOn;
    private static boolean secondReceivedThatServerIsGoingOff = false;
    private static String serverReceived = "";
    private static final String CMD1_DISCONNECT_AND_PRINTLN = "sysout with DC";
    private static final String CMD2_SIMPLY_PRINTLN = "writeln";
    private static final String CMD3_PRINTLN_PLUS_CLIENT_ID = "Server sysout";
    private static final String CMD4_SEND_BINARY_FROM_SERVER = "Sending bytes";
    private static final String TEXT1 = "Test 1";
    private static final String TEXT2 = "Test 2";
    private static final String TEXT3 = "bye";
    private static final String TEXT4 = "I am the mighty server!";
    private static final String TEXT5 = "";

    private static final String BINARY_SOURCE_FILE = "test_pic.png";
    private static final String BINARY_DEST_FILE_CLIENT = "src/test/resources/test_pic_back_from_server.png";
    private static final String BINARY_DEST_FILE_SERVER = "src/test/resources/test_pic_back.png";

    private int getServerConnectionCount() {
        return server.getClients().size();
    }

    @BeforeClass
    public static void setUp() throws Exception {
        //firstly delete all local files!
        MS_FileSystemTools.deleteFile(BINARY_DEST_FILE_SERVER);
        MS_FileSystemTools.deleteFile(BINARY_DEST_FILE_CLIENT);

        server = new MS_TcpIPServer(PORT);
        server.startServer();
        serverIsOn = true;

        MS_ServerCommand cmdRead = new MS_ServerCommand();
        cmdRead.code = CMD1_DISCONNECT_AND_PRINTLN;
        cmdRead.doOnCommand = (server, data, cli, out) -> {
            String text = data.get(1);
            System.out.println("Server received: " + text);
            serverReceived = text;
            if (text.equals(TEXT3))
                server.stopServer();
        };
        server.registerNewCommand(cmdRead);

        cmdRead = new MS_ServerCommand();
        cmdRead.code = CMD2_SIMPLY_PRINTLN;
        cmdRead.doOnCommand = (server, data, cli, out) -> {
            String text = data.get(1);
            System.out.println("Server received: " + text);
            serverReceived = text;
        };
        server.registerNewCommand(cmdRead);
        //CLIENT
        client1 = new MS_TcpIPClient();
        client1.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        MS_ClientCommand cmdReadClient = new MS_ClientCommand();
        cmdReadClient.code = CMD3_PRINTLN_PLUS_CLIENT_ID;
        cmdReadClient.doOnCommand = (client, data, out) -> {
            String text = data.get(1);
            System.out.println("Client (id = " + client.getId() + ") received: " + text);
            serverReceived = text;
        };
        client1.registerNewCommand(cmdReadClient);
        client1.onServerGoingDown = () -> {
            serverIsOn = false;
        };

        //SECOND CLIENT
        client2 = new MS_TcpIPClient();
        client2.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        cmdReadClient = new MS_ClientCommand();
        cmdReadClient.code = CMD3_PRINTLN_PLUS_CLIENT_ID;
        cmdReadClient.doOnCommand = (client, data, out) -> {
            String text = data.get(1);
            System.out.println("Client (id = " + client.getId() + ") received: " + text);
            serverReceived = text;
        };
        client2.registerNewCommand(cmdReadClient);
        client2.onServerGoingDown = () -> {
            secondReceivedThatServerIsGoingOff = true;
            System.out.println("Client with id = " + client2.getId() + " is going down too.");
        };
    }

    @AfterClass
    public static void tearDown() {
        server.stopServer();
    }

    @Test
    public void test01SimpleMessagging() throws InterruptedException {
        Thread.sleep(200);
        assertEquals(2, getServerConnectionCount());
        client1.addDataToContainer(TEXT1);
        assertTrue(client1.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        Thread.sleep(100);
        assertTrue(serverReceived.equals(TEXT1));

        assertTrue(client1.cmdToServer(CMD2_SIMPLY_PRINTLN));
        Thread.sleep(100);
        assertTrue(serverReceived.equals(TEXT5)); //empty text, cause no data added

        //second client
        client2.addDataToContainer(TEXT2);
        assertTrue(client2.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        Thread.sleep(100);
        assertTrue(serverReceived.equals(TEXT2)); //empty text, cause no data added

        //let's shutdown the server for a sek
        client1.addDataToContainer(TEXT3);
        assertTrue(client1.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        Thread.sleep(100);
        assertTrue(serverReceived.equals(TEXT3));
        assertTrue(!server.isRunning());
        assertTrue(!client1.isConnected());
        assertTrue(!serverIsOn);
        assertTrue(secondReceivedThatServerIsGoingOff);
    }

    @Test
    public void test02SimpleMessaggingToClient() throws Exception {
        //do reconnecting
        assertEquals(0, getServerConnectionCount());
        server.startServer();
        assertEquals(0, getServerConnectionCount());
        serverIsOn = true;
        client1.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        Thread.sleep(200);
        assertEquals(1, getServerConnectionCount());
        client1.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT); //double connecting shouldn't matter
        assertEquals(1, getServerConnectionCount());
        client2.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        Thread.sleep(300);
        assertEquals(2, getServerConnectionCount());

        Thread.sleep(100);
        assertTrue(server.cmdToClientByID(CMD3_PRINTLN_PLUS_CLIENT_ID, client1.getId()));
        Thread.sleep(100);
        assertTrue(serverReceived.equals(TEXT5));

        //server's empty message
        server.addDataToContainer(TEXT4);
        server.cmdToAll(CMD3_PRINTLN_PLUS_CLIENT_ID);
        Thread.sleep(1500);
        assertTrue(serverReceived.equals(TEXT4));

        //DC that client!
        server.disconnectAllClients();
        assertEquals(0, getServerConnectionCount());
        assertTrue(serverIsOn);
        assertTrue(server.getClients().size() == 0);
        Thread.sleep(1000);
        assertTrue(!client1.isConnected());
    }

    @Test
    public void test03BinaryDataExchangeOnServer() throws Exception {
        //do reconnecting
        client1.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        assertTrue(client1.isConnected());
        assertEquals(1, getServerConnectionCount());

        //make server listen the command that indicates that client is sending bytes to him
        MS_ServerCommand cmdReceiveBytesFromClient = new MS_ServerCommand();
        cmdReceiveBytesFromClient.code = CMD4_SEND_BINARY_FROM_SERVER;
        cmdReceiveBytesFromClient.doOnCommand = (server, data, cli, out) -> {
            assertTrue(cli.isConnected());
            byte[] bytesFromClient = MS_BinaryTools.stringToBytes(data.get(1));
//			Thread.sleep(1000);	

            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromClient), BINARY_DEST_FILE_SERVER);
            } catch (Exception e) {
            }
            assertTrue("Server returned no bytes.", Array.getLength(bytesFromClient) > 0);
        };
        server.registerNewCommand(cmdReceiveBytesFromClient);

        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        assertTrue(server.isRunning());
        client1.addDataToContainer(MS_BinaryTools.bytesToString(by));
        client1.cmdToServer(CMD4_SEND_BINARY_FROM_SERVER);
        assertTrue(server.isRunning());
        Thread.sleep(2000);         //w8 till server does his job
    }

    @Test
    public void test03BinaryDataExchangeOnClient() throws Exception {
        //do reconnecting
        client1.connect(MS_ClientServerConstants.DEFAULT_HOST, PORT);
        assertTrue(client1.isConnected());
        assertFalse(client2.isConnected());
        assertEquals("There must be 1 active connection!", 1, getServerConnectionCount());

        //make server listen the command that indicates that client is sending bytes to him
        MS_ClientCommand cmdReceiveBytesFromServer = new MS_ClientCommand();
        cmdReceiveBytesFromServer.code = CMD4_SEND_BINARY_FROM_SERVER;
        cmdReceiveBytesFromServer.doOnCommand = (client, data, out) -> {
            assertTrue(client.isConnected());
            byte[] bytesFromServer = MS_BinaryTools.stringToBytes(data.get(1));

            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromServer), BINARY_DEST_FILE_CLIENT);
            } catch (Exception e) {
            }
            assertTrue("Client returned no bytes.", Array.getLength(bytesFromServer) > 0);
        };
        client1.registerNewCommand(cmdReceiveBytesFromServer);

        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        assertTrue(server.isRunning());
        server.addDataToContainer(MS_BinaryTools.bytesToString(by));
        server.cmdToClientByID(CMD4_SEND_BINARY_FROM_SERVER, client1.getId());
        assertTrue(server.isRunning());
        Thread.sleep(2000);         //w8 till client does his job
    }
}
