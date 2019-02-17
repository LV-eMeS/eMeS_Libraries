package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.MS_TakenPorts;
import lv.emes.libraries.communication.http.MS_Polling;
import lv.emes.libraries.communication.tcp_ip.client.MS_ClientCommand;
import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.communication.tcp_ip.server.MS_ServerCommand;
import lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.testdata.TestData;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_TpcIPClientServerTest {

    private static final String BINARY_SOURCE_FILE = TestData.TEST_FILE_IMAGE;
    private static final String BINARY_DEST_FILE_CLIENT = TestData.TEST_RESOURCES_DIR + "test_pic_back_from_server.png";
    private static final String BINARY_DEST_FILE_SERVER = TestData.TEST_RESOURCES_DIR + "test_pic_back.png";

    private static final int PORT = MS_TakenPorts._DEFAULT_PORT_FOR_TESTING;
    private static final int DEFAULT_SLEEP_TIME = 190;
    private static MS_TcpIPServer server;
    private static MS_TcpIPClient client1;
    private static MS_TcpIPClient client2;
    private static boolean serverIsOn;
    private static boolean secondReceivedThatServerIsGoingOff = false;
    private static String serverReceived = "";
    private static boolean serverFinishedCommand = false;
    private static final String CMD1_DISCONNECT_AND_PRINTLN = "sysout with DC";
    private static final String CMD2_SIMPLY_PRINTLN = "writeln";
    private static final String CMD3_PRINTLN_PLUS_CLIENT_ID = "Server sysout";
    private static final String CMD4_SEND_BINARY_FROM_SERVER = "Sending bytes";
    private static final String CMD5_WAIT_FOR_SOME_WHILE = "Server, please, process something!";
    private static final String TEXT1 = "Test 1";
    private static final String TEXT2 = "Test 2";
    private static final String TEXT3 = "bye";
    private static final String TEXT4 = "I am the mighty server!";
    private static final String TEXT5 = "";

    private int getServerConnectionCount() {
        return server.getClients().size();
    }

    private static void doSleepForXTimes(int times) {
        MS_CodingUtils.sleep(times * DEFAULT_SLEEP_TIME);
    }

    @BeforeClass
    public static void init() throws Exception {
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

        server.registerNewCommand(new MS_ServerCommand(CMD5_WAIT_FOR_SOME_WHILE, (server, data, cli, out) -> {
            serverReceived = data.get(1);
            MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 10);
            serverFinishedCommand = true;
        }));

        //CLIENT
        client1 = new MS_TcpIPClient();
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
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

        //_SECOND CLIENT
        client2 = new MS_TcpIPClient();
        client2.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
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
    public void test01SimpleMessagging() {
        doSleepForXTimes(2);
        assertEquals(2, getServerConnectionCount());
        client1.addDataToContainer(TEXT1);
        assertTrue(client1.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        doSleepForXTimes(1);
        assertEquals(serverReceived, TEXT1);

        assertTrue(client1.cmdToServer(CMD2_SIMPLY_PRINTLN));
        doSleepForXTimes(1);
        assertEquals(serverReceived, TEXT5); //empty text, cause no data added

        //second client
        client2.addDataToContainer(TEXT2);
        assertTrue(client2.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        doSleepForXTimes(1);
        assertEquals(serverReceived, TEXT2); //empty text, cause no data added

        //let's shutdown the server for a sek
        client1.addDataToContainer(TEXT3);
        assertTrue(client1.cmdToServer(CMD1_DISCONNECT_AND_PRINTLN));
        doSleepForXTimes(1);
        assertEquals(serverReceived, TEXT3);
        assertTrue(!server.isRunning());
        assertTrue(!client1.isConnected());
        assertTrue(!serverIsOn);
        assertTrue(secondReceivedThatServerIsGoingOff);
    }

    /**
     * Before this test both clients are disconnected and server is down.
     * In test server is restarted and both clients are connecting back.
     * At the end of test server disconnects clients, but stays online.
     *
     * @throws Exception on any error.
     */
    @Test
    public void test02SimpleMessaggingToClient() throws Exception {
        //do reconnecting
        assertEquals(0, getServerConnectionCount());
        server.startServer();
        assertEquals(0, getServerConnectionCount());
        serverIsOn = true;
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        doSleepForXTimes(2);
        assertEquals(1, getServerConnectionCount());
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT); //double connecting shouldn't matter
        assertEquals(1, getServerConnectionCount());
        client2.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        doSleepForXTimes(3);
        assertEquals(2, getServerConnectionCount());

        doSleepForXTimes(1);
        assertTrue(server.cmdToClientByID(CMD3_PRINTLN_PLUS_CLIENT_ID, client1.getId()));
        doSleepForXTimes(1);
        assertEquals(serverReceived, TEXT5);

        //server's empty message
        server.addDataToContainer(TEXT4);
        server.cmdToAll(CMD3_PRINTLN_PLUS_CLIENT_ID);
        doSleepForXTimes(5);
        assertEquals(serverReceived, TEXT4);

        //DC those client!
        server.disconnectAllClients();
        assertEquals(0, getServerConnectionCount());
        assertTrue(serverIsOn);
        assertEquals(0, server.getClients().size());
        doSleepForXTimes(3);
        assertFalse(client1.isConnected());
    }

    /**
     * Before this test both clients are disconnected, but server is still up.
     * In test only first client is connecting back.
     * At the end of test server is on and still 1 client is connected.
     *
     * @throws Exception on any error.
     */
    @Test
    public void test03BinaryDataExchangeOnServer() throws Exception {
        //do reconnecting
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        assertTrue(client1.isConnected());
        doSleepForXTimes(2);
        assertEquals(1, getServerConnectionCount());

        //make server listen the command that indicates that client is sending bytes to him
        MS_ServerCommand cmdReceiveBytesFromClient = new MS_ServerCommand();
        cmdReceiveBytesFromClient.code = CMD4_SEND_BINARY_FROM_SERVER;
        cmdReceiveBytesFromClient.doOnCommand = (server, data, cli, out) -> {
            assertTrue(cli.isConnected());
            byte[] bytesFromClient = MS_BinaryTools.stringToBytes(data.get(1));

            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromClient), BINARY_DEST_FILE_SERVER);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file locally");
            }
            assertTrue("Server returned no bytes.", Array.getLength(bytesFromClient) > 0);
        };
        server.registerNewCommand(cmdReceiveBytesFromClient);

        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        assertTrue(server.isRunning());
        client1.addDataToContainer(MS_BinaryTools.bytesToString(by));
        client1.cmdToServer(CMD4_SEND_BINARY_FROM_SERVER);
        assertTrue(server.isRunning());
        doSleepForXTimes(8); //w8 till server does his job
    }

    /**
     * In test only first client is connected.
     * At the end of test server is on and still 1 client is connected.
     *
     * @throws Exception on any error.
     */
    @Test
    public void test04BinaryDataExchangeOnClient() throws Exception {
        //do reconnecting
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
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
                throw new RuntimeException("Failed to save file locally");
            }
            assertTrue("Client returned no bytes.", Array.getLength(bytesFromServer) > 0);
        };
        client1.registerNewCommand(cmdReceiveBytesFromServer);

        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        assertTrue(server.isRunning());
        server.addDataToContainer(MS_BinaryTools.bytesToString(by));
        server.cmdToClientByID(CMD4_SEND_BINARY_FROM_SERVER, client1.getId());
        assertTrue(server.isRunning());
        doSleepForXTimes(8); //w8 till server does his job
    }

    @Test
    public void test11ConnectionTimeout() {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setConnectTimeout(1);
        assertEquals(1, impatientClient.getConnectTimeout());
        assertThatThrownBy(() -> impatientClient.connect(TestData.TESTING_SERVER_HOSTAME, PORT))
                .isInstanceOf(SocketTimeoutException.class);
    }

    /*
     * Before test server is on and only first client1 is connected, but that doesn't matter, as new client is created here.
     * When test runs, server gets introduced to new command: wait for 2 seconds.
     * At the end of test server is on and still 1 client is connected.
     */
    @Test
    public void test12WriteTimeout() throws IOException {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setWriteTimeout(DEFAULT_SLEEP_TIME * 5);
        assertEquals(DEFAULT_SLEEP_TIME * 5, impatientClient.getWriteTimeout());
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);

        serverFinishedCommand = false; //init value to check afterwards
        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";

        impatientClient.addDataToContainer(DATA_THAT_SERVER_SHOULD_PROCESS);
        impatientClient.cmdToServerAcknowledge(CMD5_WAIT_FOR_SOME_WHILE);

        assertEquals("Server didn't even receive command from client", DATA_THAT_SERVER_SHOULD_PROCESS, serverReceived);
        assertFalse("Server should not be able to process this command in time, but it does", serverFinishedCommand);
    }

    @Test
    public void test13CmdToServerNotifySuccess() throws IOException, MS_ExecutionFailureException {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setWriteTimeout(DEFAULT_SLEEP_TIME * 5);
        assertEquals(DEFAULT_SLEEP_TIME * 5, impatientClient.getWriteTimeout());
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);

        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean failure = new AtomicBoolean(false);
        AtomicReference<String> receivedCmdCode = new AtomicReference<>();

        impatientClient.addDataToContainer(DATA_THAT_SERVER_SHOULD_PROCESS);
        impatientClient.cmdToServerNotify(CMD5_WAIT_FOR_SOME_WHILE, (code) -> {
            success.set(true);
            receivedCmdCode.set(code);
        }, (exc) -> failure.set(true));

        //wait until things changes
        new MS_Polling<Boolean>().withAction(success::get).withCheck((a) -> success.get() || failure.get())
                .withSleepInterval(DEFAULT_SLEEP_TIME / 2).withMaxPollingAttempts(6).poll();

        assertEquals("Server didn't even receive command from client", DATA_THAT_SERVER_SHOULD_PROCESS, serverReceived);
        assertTrue("Notification on success was not received, but we had enough time to successfully process command", success.get());
    }

    @Test
    public void test14CmdToServerNotifyFailure() throws IOException, MS_ExecutionFailureException {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setWriteTimeout(1);
        assertEquals(1, impatientClient.getWriteTimeout());
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        //make sure that client will be disconnected before sending command to server
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2);
        server.disconnectClientByID(impatientClient.getId());

        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean failure = new AtomicBoolean(false);
        AtomicReference<String> receivedCmdCode = new AtomicReference<>();

        impatientClient.addDataToContainer(DATA_THAT_SERVER_SHOULD_PROCESS);
        impatientClient.cmdToServerNotify(CMD5_WAIT_FOR_SOME_WHILE, (code) -> {
            success.set(true);
            receivedCmdCode.set(code);
        }, (exc) -> failure.set(true));

        //wait until things changes
        new MS_Polling<Boolean>().withAction(success::get).withCheck((a) -> success.get() || failure.get())
                .withSleepInterval(1).withMaxPollingAttempts(100).poll();

        assertTrue("Notification on failure was not received, even though command shouldn't reach server's thread in time", failure.get());
    }
}
