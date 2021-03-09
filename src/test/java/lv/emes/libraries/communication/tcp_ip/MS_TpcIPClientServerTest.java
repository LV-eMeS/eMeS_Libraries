package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.MS_TakenPorts;
import lv.emes.libraries.communication.http.MS_Polling;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.tcp_ip.client.IFuncOnIncomingCommandFromServerStringData;
import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
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
import java.io.UTFDataFormatException;
import java.lang.reflect.Array;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.TestCase.assertFalse;
import static lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    private static boolean serverIsUp;
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
        serverIsUp = true;

        server.registerCommandWithStringData(CMD1_DISCONNECT_AND_PRINTLN, (server, client, data) -> {
            System.out.println("Server received: " + data);
            serverReceived = data;
            if (data.equals(TEXT3))
                server.stopServer();
        });

        server.registerCommandWithStringData(CMD2_SIMPLY_PRINTLN, (server, client, data) -> {
            System.out.println("Server received: " + data);
            serverReceived = data;
        });

        server.registerCommandWithStringData(CMD5_WAIT_FOR_SOME_WHILE, (server, client, data) -> {
            serverReceived = data;
            MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 10);
            serverFinishedCommand = true;
        });


        //CLIENT
        IFuncOnIncomingCommandFromServerStringData cmd3Action = (client, data) -> {
            System.out.println("Client (id = " + client.getId() + ") received: " + data);
            serverReceived = data;
        };
        client1 = new MS_TcpIPClient();
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        client1.registerCommandWithStringData(CMD3_PRINTLN_PLUS_CLIENT_ID, cmd3Action);
        client1.onServerGoingDown = () -> serverIsUp = false;

        //_SECOND CLIENT
        client2 = new MS_TcpIPClient();
        client2.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        client2.registerCommandWithStringData(CMD3_PRINTLN_PLUS_CLIENT_ID, cmd3Action);
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
        assertThat(getServerConnectionCount()).isEqualTo(2);
        assertThat(client1.cmdToServer(new MS_TcpIPCommand(CMD1_DISCONNECT_AND_PRINTLN, TEXT1))).isTrue();
        doSleepForXTimes(1);
        assertThat(TEXT1).isEqualTo(serverReceived);

        assertThat(client1.cmdToServer(new MS_TcpIPCommand(CMD2_SIMPLY_PRINTLN, ""))).isTrue();
        doSleepForXTimes(1);
        assertThat(TEXT5).isEqualTo(serverReceived); //empty text, cause no data added

        //second client
        assertThat(client2.cmdToServer(new MS_TcpIPCommand(CMD1_DISCONNECT_AND_PRINTLN, TEXT2))).isTrue();
        doSleepForXTimes(1);
        assertThat(TEXT2).isEqualTo(serverReceived); //empty text, cause no data added

        //let's shutdown the server for a sek
        assertThat(client1.cmdToServer(new MS_TcpIPCommand(CMD1_DISCONNECT_AND_PRINTLN, TEXT3))).isTrue();
        doSleepForXTimes(1);
        assertThat(TEXT3).isEqualTo(serverReceived);
        assertThat(!server.isRunning()).isTrue();
        assertThat(!client1.isConnected()).isTrue();
        assertThat(!serverIsUp).isTrue();
        assertThat(secondReceivedThatServerIsGoingOff).isTrue();
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
        assertThat(getServerConnectionCount()).isEqualTo(0);
        server.startServer();
        assertThat(getServerConnectionCount()).isEqualTo(0);
        serverIsUp = true;
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        doSleepForXTimes(2);
        assertThat(getServerConnectionCount()).isEqualTo(1);
        client1.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT); //double connecting shouldn't matter
        assertThat(getServerConnectionCount()).isEqualTo(1);
        client2.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        doSleepForXTimes(3);
        assertThat(getServerConnectionCount()).isEqualTo(2);

        doSleepForXTimes(1);
        assertThat(server.cmdToClientByID(new MS_TcpIPCommand(CMD3_PRINTLN_PLUS_CLIENT_ID, ""), client1.getId())).isTrue();
        doSleepForXTimes(1);
        assertThat(TEXT5).isEqualTo(serverReceived);

        //server's message
        server.cmdToAll(new MS_TcpIPCommand(CMD3_PRINTLN_PLUS_CLIENT_ID, TEXT4));
        doSleepForXTimes(5);
        assertThat(TEXT4).isEqualTo(serverReceived);

        //DC those client!
        server.disconnectAllClients();
        assertThat(getServerConnectionCount()).isEqualTo(0);
        assertThat(serverIsUp).isTrue();
        assertThat(server.getClients().size()).isEqualTo(0);
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
        assertThat(client1.isConnected()).isTrue();
        doSleepForXTimes(2);
        assertThat(getServerConnectionCount()).isEqualTo(1);

        //make server listen the command that indicates that client is sending bytes to him
        server.registerCommandWithBinaryData(CMD4_SEND_BINARY_FROM_SERVER, (server, cli, bytesFromClient) -> {
            assertThat(cli.isConnected()).isTrue();

            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromClient), BINARY_DEST_FILE_SERVER);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file locally");
            }
            assertThat(Array.getLength(bytesFromClient) > 0).as("Server returned no bytes.").isTrue();
        });

        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        assertThat(server.isRunning()).isTrue();
        client1.cmdToServer(new MS_TcpIPCommand(CMD4_SEND_BINARY_FROM_SERVER, by));
        assertThat(server.isRunning()).isTrue();
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
        assertThat(client1.isConnected()).isTrue();
        assertFalse(client2.isConnected());
        assertThat(getServerConnectionCount()).as("There must be 1 active connection!").isEqualTo(1);

        //make server listen the command that indicates that client is sending bytes to him
        client1.registerCommandWithBinaryData(CMD4_SEND_BINARY_FROM_SERVER, (client, bytesFromServer) -> {
            assertThat(client.isConnected()).isTrue();

            try {
                MS_BinaryTools.writeFile(MS_BinaryTools.bytesToIntput(bytesFromServer), BINARY_DEST_FILE_CLIENT);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save file locally");
            }
            assertThat(Array.getLength(bytesFromServer) > 0).as("Client returned no bytes.").isTrue();
        });

        assertThat(server.isRunning()).isTrue();
        byte[] by = MS_BinaryTools.inputToBytes(MS_FileSystemTools.getResourceInputStream(BINARY_SOURCE_FILE));
        server.cmdToClientByID(new MS_TcpIPCommand(CMD4_SEND_BINARY_FROM_SERVER, by), client1.getId());
        assertThat(server.isRunning()).isTrue();
        doSleepForXTimes(8); //w8 till server does his job
    }

    @Test
    public void test11ConnectionTimeout() {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setConnectTimeout(1);
        assertThat(impatientClient.getConnectTimeout()).isEqualTo(1);
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
        assertThat(impatientClient.getWriteTimeout()).isEqualTo(DEFAULT_SLEEP_TIME * 5);
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);

        serverFinishedCommand = false; //init value to check afterwards
        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";

        impatientClient.cmdToServerAcknowledge(new MS_TcpIPCommand(CMD5_WAIT_FOR_SOME_WHILE, DATA_THAT_SERVER_SHOULD_PROCESS));

        assertThat(serverReceived).as("Server didn't even receive command from client").isEqualTo(DATA_THAT_SERVER_SHOULD_PROCESS);
        assertFalse("Server should not be able to process this command in time, but it does", serverFinishedCommand);
    }

    @Test
    public void test13CmdToServerNotifySuccess() throws IOException, MS_ExecutionFailureException {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setWriteTimeout(DEFAULT_SLEEP_TIME * 5);
        assertThat(impatientClient.getWriteTimeout()).isEqualTo(DEFAULT_SLEEP_TIME * 5);
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);

        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean failure = new AtomicBoolean(false);
        AtomicReference<String> receivedCmdCode = new AtomicReference<>();

        impatientClient.cmdToServerNotify(new MS_TcpIPCommand(CMD5_WAIT_FOR_SOME_WHILE, DATA_THAT_SERVER_SHOULD_PROCESS), (code) -> {
            success.set(true);
            receivedCmdCode.set(code);
        }, (exc) -> failure.set(true));

        //wait until things changes
        new MS_Polling<Boolean>().withAction(success::get).withCheck((a) -> success.get() || failure.get())
                .withSleepInterval(DEFAULT_SLEEP_TIME / 2).withMaxPollingAttempts(8).poll();

        assertThat(serverReceived).as("Server didn't even receive command from client").isEqualTo(DATA_THAT_SERVER_SHOULD_PROCESS);
        assertThat(success.get()).as("Notification on success was not received, but we had enough time to successfully process command").isTrue();
    }

    @Test
    public void test14CmdToServerNotifyFailure() throws IOException, MS_ExecutionFailureException {
        MS_TcpIPClient impatientClient = new MS_TcpIPClient();
        impatientClient.setWriteTimeout(1);
        assertThat(impatientClient.getWriteTimeout()).isEqualTo(1);
        impatientClient.connect(MS_ClientServerConstants._DEFAULT_HOST, PORT);
        //make sure that client will be disconnected before sending command to server
        MS_CodingUtils.sleep(DEFAULT_SLEEP_TIME * 2);
        server.disconnectClientByID(impatientClient.getId());

        final String DATA_THAT_SERVER_SHOULD_PROCESS = "Some value that needs to be delivered to server";
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean failure = new AtomicBoolean(false);
        AtomicReference<String> receivedCmdCode = new AtomicReference<>();

        impatientClient.cmdToServerNotify(new MS_TcpIPCommand(CMD5_WAIT_FOR_SOME_WHILE, DATA_THAT_SERVER_SHOULD_PROCESS), (code) -> {
            success.set(true);
            receivedCmdCode.set(code);
        }, (exc) -> failure.set(true));

        //wait until things changes
        new MS_Polling<Boolean>().withAction(success::get).withCheck((a) -> success.get() || failure.get())
                .withSleepInterval(4).withMaxPollingAttempts(100).poll();

        assertThat(failure.get()).as("Notification on failure was not received, even though command shouldn't reach server's thread in time").isTrue();
    }

    @Test
    public void test21CommandToServerOfDifferentTypeThanRegistered() throws IOException {
        AtomicReference<UTFDataFormatException> caughtExceptionOnServerSide = new AtomicReference<>(null);
        server.onDataFormatException = (caughtExceptionOnServerSide::set);
        client1.cmdToServerAcknowledge(new MS_TcpIPCommand(CMD1_DISCONNECT_AND_PRINTLN, new MS_JSONArray())); //wrong type of command
        assertThatThrownBy(() -> {
            throw new MS_Polling<UTFDataFormatException>()
                    .withSleepInterval(DEFAULT_SLEEP_TIME / 2)
                    .withMaxPollingAttempts(10)
                    .withAction(caughtExceptionOnServerSide::get)
                    .withCheck(Objects::nonNull)
                    .poll();
        })
                .hasMessageContaining("Received command from client with type")
                .hasMessageContaining(CMD1_DISCONNECT_AND_PRINTLN)
                .hasMessageContaining(_CMD_DATA_TYPE_DESCRIPTIONS.get(_CMD_WITH_JSON_ARRAY_DATA)) //received
                .hasMessageContaining(_CMD_DATA_TYPE_DESCRIPTIONS.get(_CMD_WITH_STRING_DATA)) //expected
        ;
    }

    @Test
    public void test22CommandToClientOfDifferentTypeThanRegistered() {
        AtomicReference<UTFDataFormatException> caughtExceptionOnServerSide = new AtomicReference<>(null);
        client1.onDataFormatException = (caughtExceptionOnServerSide::set);
        server.cmdToClient(new MS_TcpIPCommand(CMD3_PRINTLN_PLUS_CLIENT_ID, (byte[]) null), server.getClientByID(client1.getId())); //wrong type of command
        assertThatThrownBy(() -> {
            throw new MS_Polling<UTFDataFormatException>()
                    .withSleepInterval(DEFAULT_SLEEP_TIME / 2)
                    .withMaxPollingAttempts(4)
                    .withAction(caughtExceptionOnServerSide::get)
                    .withCheck(Objects::nonNull)
                    .poll();
        })
                .hasMessageContaining("Received command from server with type")
                .hasMessageContaining(CMD3_PRINTLN_PLUS_CLIENT_ID)
                .hasMessageContaining(_CMD_DATA_TYPE_DESCRIPTIONS.get(_CMD_WITH_BINARY_DATA)) //received
                .hasMessageContaining(_CMD_DATA_TYPE_DESCRIPTIONS.get(_CMD_WITH_STRING_DATA)) //expected
        ;
    }
}
