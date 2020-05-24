package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.communication.tcp_ip.MS_ActionOnIncomingTcpIpCommand;
import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.MS_TcpIPCommand;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.logging.MS_Log4Java;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.log4j.Logger;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TCP/IP server which operates commands to communicate to client.
 * <p>Public methods:
 * <ul>
 * <li>registerCommandWithNoData</li>
 * <li>registerCommandWithStringData</li>
 * <li>registerCommandWithJsonObjectData</li>
 * <li>registerCommandWithJsonArrayData</li>
 * <li>registerCommandWithBinaryData</li>
 * <li>getCommands</li>
 * <li>cmdToClientByID</li>
 * <li>cmdToClient</li>
 * <li>cmdToAll</li>
 *
 * <li>startServer</li>
 * <li>stopServer</li>
 * <li>disconnectClientByID</li>
 * </ul>
 * <p>Public properties to set:
 * <ul>
 * <li>onClientConnecting</li>
 * <li>onClientGoingOffline</li>
 * <li>onClientSayingHi</li>
 * </ul>
 * <p>Protected methods:
 * <ul>
 * <li>onIncomingClientMessage</li>
 * <li>onNewClientConnected</li>
 * <li>writeln</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>isRunning</li>
 * <li>isActive</li>
 * <li>getPort</li>
 * <li>getClients</li>
 * <li>getClientByID</li>
 *
 * <li>setPort</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.2.
 * @since 1.1.1
 */
public class MS_TcpIPServer extends MS_TcpIPServerCore {

    private static final Logger LOGGER = MS_Log4Java.getLogger(MS_TcpIPServer.class);

    /**
     * Set this property with lambda expression to do actions after client went down (disconnected).
     * <br><u>Note</u>: communication with this client is already impossible at this moment.
     * <code>
     * (client) -&gt; {};
     * </code>
     */
    public IFuncOnClientDoingSomething onClientGoingOffline = null;

    /**
     * Set this property with lambda expression to do actions after client successfully connected to server.
     * <code>
     * (client) -&gt; {};
     * </code>
     */
    public IFuncOnClientDoingSomething onClientConnecting = null;

    /**
     * Right after successful connection client is sending something like "Hi" message that introduces him.
     * Client also sends data about his device: OS name, system user name, path to working directory and path to home directory.
     */
    public IFuncOnClientDoingSomething onClientSayingHi = null;

    /**
     * If true, all clients will be notified that server is going down when <b>stopServer</b> is called.
     * Default: true.
     */
    public boolean notifyClientsOnDC = true;

    /**
     * Creates new server instance and sets its port.
     *
     * @param port 1..~65k.
     */
    public MS_TcpIPServer(int port) {
        super(port);
        //save information about connecting client. Right after connection success client will send some info about himself (OS,
        this.registerCommandWithJsonObjectData(MS_ClientServerConstants._INFO_ABOUT_NEW_CLIENT, (server, client, data) -> {
            client.os = data.getString(MS_ClientServerConstants._CMD_DATA_KEY_OS);
            client.osUserName = data.getString(MS_ClientServerConstants._CMD_DATA_KEY_USER_NAME);
            client.currentWorkingDirectory = data.getString(MS_ClientServerConstants._CMD_DATA_KEY_WORKING_DIR);
            client.systemHomeDirectory = data.getString(MS_ClientServerConstants._CMD_DATA_KEY_HOME_DIR);
            if (onClientSayingHi != null)
                try {
                    onClientSayingHi.doOnEvent(client);
                } catch (Exception e) {
                    throw new MS_BadSetupException(e);
                }
        });

        this.registerCommandWithStringData(MS_ClientServerConstants._REFRESH_CLIENT_CURRENT_TIME, (server, client, timeStr) -> {
            ZonedDateTime clientCurrentTime = MS_DateTimeUtils.formatDateTimeBackported(timeStr, MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
            client.setClientTime(clientCurrentTime);
        });

        //set behavior of client disconnecting. Note that server and list already is aware that client is missing.
        //Here you need just to set user-defined behavior
        this.registerCommandWithNoData(MS_ClientServerConstants._CLIENT_DISCONNECTS_NOTIFY_MESSAGE, (server, client) -> {
            //user can define his own expected behavior when server goes down.
            if (onClientGoingOffline != null)
                try {
                    onClientGoingOffline.doOnEvent(client);
                } catch (Exception e) {
                    throw new MS_BadSetupException(e);
                }
        });
    }

    @Override
    protected void onIncomingClientMessage(String message, MS_ClientOfServer client) {
        // Every time client sends a message server reads it.
        // Messages are formatted in specific JSON format by client-server contract.
        // Client might also send acknowledgement command type, which has metadata information that needs to be
        // extracted first and then handled differently
        MS_JSONObject command = new MS_JSONObject(message);
        if (command.has(MS_ClientServerConstants._CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE)) {
            if (client.getTimeDiffFromServer() != null && command.isNotNull(MS_ClientServerConstants._CURRENT_CLIENT_TIME)) {
                String timeStr = command.getString(MS_ClientServerConstants._CURRENT_CLIENT_TIME);
                ZonedDateTime clientTime = MS_DateTimeUtils.formatDateTimeBackported(timeStr, MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
                ZonedDateTime timeNow;
                if (clientTime
                        .plusNanos(client.getTimeDiffFromServer())
                        // Client gave time when he sent the command. _CLIENT_TIMEOUT is max allowed time on the fly
                        // between point when client sent command and server received it
                        .plusNanos(command.optInt(MS_ClientServerConstants._CLIENT_TIMEOUT, 0) * 1000_000)
                        .isAfter(timeNow = ZonedDateTime.now()))
                    LOGGER.warn(String.format("Client message has timed out and not been executed. Server time: %s; client message:\n%s", timeNow, message));
                return;
            }
            MS_JSONObject actualAkcCmd = command.getJSONObject(MS_ClientServerConstants._CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE);
            //1. Respond to client that command is received
            this.cmdToClient(new MS_TcpIPCommand(MS_ClientServerConstants._SERVER_ACKNOWLEDGEMENT, actualAkcCmd.getString("id")), client);
            //2. React to actual command
            handleIncomingCommand(actualAkcCmd, client);
        } else {
            handleIncomingCommand(command, client);
        }
    }

    private void handleIncomingCommand(MS_JSONObject command, MS_ClientOfServer client) {
        int cmdType = command.getInt("type");
        String cmdCode = command.getString("code");
        Map<String, MS_ActionOnIncomingTcpIpCommand> commandActionsByCmdCode = this.commands.get(cmdType);
        MS_ActionOnIncomingTcpIpCommand genericAction = commandActionsByCmdCode == null ? null : commandActionsByCmdCode.get(cmdCode);
        if (genericAction == null) {
            if (this.onDataFormatException != null) {
                // first try to find out, maybe command is registered under different type and inform by exception,
                // that this kind of type should be used in message exchange
                List<Map.Entry<Integer, Map<String, MS_ActionOnIncomingTcpIpCommand>>> commandsOfDifferentTypesThatMatchesCode =
                        this.commands.entrySet().stream()
                                .filter(entry -> entry.getValue().get(cmdCode) != null)
                                .collect(Collectors.toList());
                if (commandsOfDifferentTypesThatMatchesCode.size() > 0) {
                    MS_StringList validTypes = new MS_StringList();
                    commandsOfDifferentTypesThatMatchesCode.forEach(commandEntry -> validTypes.add(
                            MS_ClientServerConstants._CMD_DATA_TYPE_DESCRIPTIONS.get(commandEntry.getKey()))
                    );

                    this.onDataFormatException.doOnEvent(new UTFDataFormatException("Received command from client with type ["
                            + MS_ClientServerConstants._CMD_DATA_TYPE_DESCRIPTIONS.get(cmdType) + "] and code: ["
                            + cmdCode + "], " + MS_StringUtils._LINE_BRAKE +
                            "but such command code is registered as recognizable only with one " +
                            "of following data types: [" + validTypes.toStringWithNoLastDelimiter() + "].")
                    );
                }
                return;
            }

            // if not, simply complain about not found command
            if (this.onIOException != null)
                this.onIOException.doOnEvent(new IOException("Received command from client with unknown command code: " + cmdCode));
        } else { // all good, continue on execution (in separate thread, as execution sometimes might take some while)
            MS_FutureEvent commandExecutionThread = new MS_FutureEvent().withThreadName("MS_TcpIPServer.onIncomingClientMessage");

            switch (cmdType) {
                case MS_ClientServerConstants._CMD_WITH_NO_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromClientNoData action = (IFuncOnIncomingCommandFromClientNoData) genericAction;
                        action.handleCommand(this, client);
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_STRING_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromClientStringData action = (IFuncOnIncomingCommandFromClientStringData) genericAction;
                        action.handleCommand(this, client, command.getString("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_JSON_OBJECT_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromClientJsonObjectData action = (IFuncOnIncomingCommandFromClientJsonObjectData) genericAction;
                        action.handleCommand(this, client, command.getJSONObject("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_JSON_ARRAY_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromClientJsonArrayData action = (IFuncOnIncomingCommandFromClientJsonArrayData) genericAction;
                        action.handleCommand(this, client, command.getJSONArray("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_BINARY_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromClientBinaryData action = (IFuncOnIncomingCommandFromClientBinaryData) genericAction;
                        action.handleCommand(this, client, MS_BinaryTools.stringToBytes(command.getString("data")));
                    });
                    break;
                default:
                    if (this.onIOException != null) {
                        this.onIOException.doOnEvent(new IOException("Unsupported command type received"));
                    }
                    return;
            }

            if (this.onExecutionException != null)
                commandExecutionThread.withActionOnException((ex) -> this.onExecutionException.doOnError(ex));
            commandExecutionThread.schedule();
        }
    }

    /**
     * Use this method to send custom command to particular client by his ID!
     * If some data added with <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param command  a command.
     * @param clientID id of addressee.
     * @return false if error occurred during sending message.
     */
    public synchronized boolean cmdToClientByID(MS_TcpIPCommand command, long clientID) {
        try {
            String message = command.buildCommand().toString();
            this.getClientByID(clientID).getOut().writeUTF(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Use this method to send custom command to particular client!
     * If some data added with <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param command a command.
     * @param client  addressee itself.
     * @return false if error occurred during sending message.
     */
    public synchronized boolean cmdToClient(MS_TcpIPCommand command, MS_ClientOfServer client) {
        try {
            String message = command.buildCommand().toString();
            client.getOut().writeUTF(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Use this method to send custom command to every single connected client at once!
     * This method works silently and don't throw any exceptions if something bad happens,
     * but it tries to send messages to every single client even if {@link IOException} happens in the middle of list.
     *
     * @param command a command.
     */
    public synchronized void cmdToAll(MS_TcpIPCommand command) {
        String message = command.buildCommand().toString();
        clients.forEach((id, client) -> {
            try {
                client.getOut().writeUTF(message);
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * {@inheritDoc}
     * And notifies all connected clients that server is shutting down with command <b>_DC_NOTIFY_MESSAGE</b>
     * which is first command in <b>commandList</b> of client objects. <p>To disable this option change <b>notifyClientsOnDC</b> to false or
     * simply remove 0-th element in <b>commandList</b> of all client objects (<b>MS_TcpClient</b>), which is not recommended.
     */
    @Override
    public void stopServer() {
        if (notifyClientsOnDC)
            this.cmdToAll(new MS_TcpIPCommand(MS_ClientServerConstants._DC_NOTIFY_MESSAGE));
        //make them all disconnect themselves too (only server side sockets, not client side sockets)
        this.disconnectAllClients();
        super.stopServer();
    }

    @Override
    protected void onNewClientConnected(MS_ClientOfServer client) {
        this.cmdToClient(new MS_TcpIPCommand(MS_ClientServerConstants._NEW_CLIENT_ID_NOTIFY_MESSAGE, Long.toString(client.id)), client);
        if (onClientConnecting != null)
            onClientConnecting.doOnEvent(client);
    }

    /**
     * Synonym for <b>isRunning</b>.
     *
     * @return whether server is ready for message exchange or not.
     */
    @Override
    public boolean isActive() {
        return this.isRunning();
    }
}
