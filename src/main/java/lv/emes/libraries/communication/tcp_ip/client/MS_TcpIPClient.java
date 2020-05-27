package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.http.MS_Polling;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.communication.tcp_ip.IFuncOnIOException;
import lv.emes.libraries.communication.tcp_ip.MS_ActionOnIncomingTcpIpCommand;
import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.MS_TcpIPCommand;
import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.tools.decision.MS_ConditionalDecision;
import lv.emes.libraries.tools.decision.MS_FibonacciDecision;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TCP/IP client which operates commands to connect and communicate to server.
 * <p>Public methods:
 * <ul>
 * <li>registerCommandWithNoData</li>
 * <li>registerCommandWithStringData</li>
 * <li>registerCommandWithJsonObjectData</li>
 * <li>registerCommandWithJsonArrayData</li>
 * <li>registerCommandWithBinaryData</li>
 * <li>getCommands</li>
 * <li>cmdToServer</li>
 * <li>cmdToServerExc</li>
 * <li>cmdToServerAcknowledge</li>
 * <li>cmdToServerNotify</li>
 * <li>getId</li>
 * <li>connect</li>
 * <li>disconnect</li>
 * <li>stopServer</li>
 * <li>disconnectClientByID</li>
 * </ul>
 * <p>Public properties to set:
 * <ul>
 * <li>onServerGoingDown</li>
 * </ul>
 * <p>Protected methods:
 * <ul>
 * <li>onIncomingServerMessage</li>
 * <li>writeln</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.2.
 * @since 2.2.2
 */
public class MS_TcpIPClient extends MS_TcpIPClientCore {

    public static final int POLLING_SLEEP_INTERVAL = 50;

    /**
     * Set this property with lambda expression to do actions after server went down (disconnected).
     * <br><u>Note</u>: communication with this server is already impossible at this moment.
     * <p>() -&gt; {some action};
     */
    public IFuncOnServerGoingDown onServerGoingDown = null;

    private long id = 0; //client ID
    private final Queue<String> sentCommandsWaitingForAcknowledgement = new ConcurrentLinkedQueue<>();
    private final MS_ConditionalDecision<Long> needToSendCurrentTime = new MS_FibonacciDecision();
    private boolean autoRestartIfDisconnected = true;
    private boolean abortAcknowledgementCmdsOnTimeOut = false;

    /**
     * Creates object and initializes default commands.
     */
    public MS_TcpIPClient() {
        // Set behavior of server disconnecting
        this.registerCommandWithNoData(MS_ClientServerConstants._DC_NOTIFY_MESSAGE, client -> {
            client.disconnect(); //break connection, because it's already closed from server's side
            //user can define his own expected behavior when server goes down.
            if (onServerGoingDown != null)
                onServerGoingDown.doOnEvent();
        });

        // Save this client ID
        this.registerCommandWithStringData(MS_ClientServerConstants._NEW_CLIENT_ID_NOTIFY_MESSAGE,
                (client, data) -> id = Long.parseLong(data)
        );

        // Register server acknowledgement of received command
        this.registerCommandWithStringData(MS_ClientServerConstants._SERVER_ACKNOWLEDGEMENT,
                (client, data) -> sentCommandsWaitingForAcknowledgement.remove(data)
        );
    }

    @Override
    public void connect(String host, int port) throws IOException, IllegalArgumentException {
        super.connect(host, port);
        needToSendCurrentTime.reset();
    }

    @Override
    protected void onIncomingServerMessage(String message) {
        //Every time server sends a message client reads it.
        //Messages are formatted in specific format.
        MS_JSONObject command = new MS_JSONObject(message);
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

                    this.onDataFormatException.doOnEvent(new UTFDataFormatException("Received command from server with type ["
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
                this.onIOException.doOnEvent(new IOException("Received command from server with unknown command code: " + cmdCode));
        } else { // all good, continue on execution (in separate thread, as execution sometimes might take some while)
            MS_FutureEvent commandExecutionThread = new MS_FutureEvent().withThreadName("MS_TcpIPClient.onIncomingServerMessage");

            switch (cmdType) {
                case MS_ClientServerConstants._CMD_WITH_NO_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromServerNoData action = (IFuncOnIncomingCommandFromServerNoData) genericAction;
                        action.handleCommand(this);
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_STRING_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromServerStringData action = (IFuncOnIncomingCommandFromServerStringData) genericAction;
                        action.handleCommand(this, command.getString("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_JSON_OBJECT_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromServerJsonObjectData action = (IFuncOnIncomingCommandFromServerJsonObjectData) genericAction;
                        action.handleCommand(this, command.getJSONObject("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_JSON_ARRAY_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromServerJsonArrayData action = (IFuncOnIncomingCommandFromServerJsonArrayData) genericAction;
                        action.handleCommand(this, command.getJSONArray("data"));
                    });
                    break;
                case MS_ClientServerConstants._CMD_WITH_BINARY_DATA:
                    commandExecutionThread.withAction(() -> {
                        IFuncOnIncomingCommandFromServerBinaryData action = (IFuncOnIncomingCommandFromServerBinaryData) genericAction;
                        action.handleCommand(this, MS_BinaryTools.stringToBytes(command.getString("data")));
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
     * Send command altogether with its data to server.
     *
     * @param command a command.
     * @return false if error occurred during sending message or connection is not established.
     */
    public boolean cmdToServer(MS_TcpIPCommand command) {
        if (!isConnected()) {
            if (autoRestartIfDisconnected) {
                try {
                    restartConnection();
                } catch (IOException e) {
                    return false;
                }
            } else
                return false;
        }
        return this.writeln(command.buildCommand().toString());
    }

    /**
     * Send command altogether with its data to server and expect {@link IOException} (if any will be thrown in the process).
     *
     * @param command a command.
     * @throws IOException if message couldn't be sent or server is not connected.
     */
    public void cmdToServerExc(MS_TcpIPCommand command) throws IOException {
        if (!isConnected()) {
            if (autoRestartIfDisconnected)
                restartConnection();
            else
                throw new IOException("Command [" + command.getCmdCode() + "] cannot be sent because server is disconnected");
        }

        this.out.writeUTF(command.buildCommand().toString());
    }

    /**
     * <u>Warning</u>: This is a thread blocking method.
     * <p>Use this method to send custom command to server and wait for acknowledgment from server that command is received.
     * Maximum waiting time (in milliseconds) until {@link SocketTimeoutException} will be thrown is
     * specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     *
     * @param command a command.
     * @throws IOException            if message couldn't be sent or server is not connected.
     * @throws SocketTimeoutException if server doesn't answer in given time specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     */
    public void cmdToServerAcknowledge(MS_TcpIPCommand command) throws SocketTimeoutException, IOException {
        if (!isConnected()) {
            if (autoRestartIfDisconnected)
                restartConnection();
            else
                throw new IOException("Command [" + command.getCmdCode() + "] cannot be sent because server is disconnected");
        }

        MS_JSONObject cmd = command.buildCommand();
        String commandId = cmd.getString("id");
        sentCommandsWaitingForAcknowledgement.add(commandId);
        // Send ack type command, which actually will contain original command
        MS_JSONObject payload = new MS_JSONObject().put(MS_ClientServerConstants._CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE, cmd);
        if (abortAcknowledgementCmdsOnTimeOut) {
            payload.put(MS_ClientServerConstants._CURRENT_CLIENT_TIME, getCurrentTimeNow());
            payload.put(MS_ClientServerConstants._CLIENT_TIMEOUT, this.getWriteTimeout());
        }
        this.out.writeUTF(payload.toString());
        try {
            new MS_Polling<Queue<String>>()
                    .withAction(() -> sentCommandsWaitingForAcknowledgement)
                    .withCheck((commands) -> !commands.contains(commandId))
                    .withSleepInterval(POLLING_SLEEP_INTERVAL)
                    .withMaxPollingAttempts(getWriteTimeout() > POLLING_SLEEP_INTERVAL ? getWriteTimeout() / POLLING_SLEEP_INTERVAL : 1)
                    .poll();
        } catch (MS_ExecutionFailureException e) {
            sentCommandsWaitingForAcknowledgement.remove(commandId); // this command is already lost, it will never be needed again
            throw new SocketTimeoutException(String.format("Failed to receive server acknowledgement of execution of command [%s][%s]",
                    cmd.getString("code"), commandId));
        }
        sendCurrentTimeToTheServer();
    }

    /**
     * <u>Warning</u>: This is a thread blocking method.
     * Use this method to send custom command to server and after acknowledgment from server that command is received
     * perform specific action <b>notificationOnSuccess</b>.
     * Maximum waiting time (in milliseconds) for thread to wait for is
     * specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     * <p>If some data added by <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param command               a command.
     * @param notificationOnSuccess action (with consumed command code) to do when command is successfully executed on server side.
     * @param notificationOnFailure action to do when execution of command fails.
     */
    public void cmdToServerNotify(MS_TcpIPCommand command, Consumer<String> notificationOnSuccess, IFuncOnIOException notificationOnFailure) {
        Objects.requireNonNull(notificationOnSuccess);
        Objects.requireNonNull(notificationOnFailure);
        new MS_FutureEvent()
                .withThreadName("MS_TcpIPClient.cmdToServerNotify")
                .withTimeout(this.getWriteTimeout())
                .withAction(() -> {
                    try {
                        cmdToServerAcknowledge(command);
                        notificationOnSuccess.accept(command.getCmdCode());
                    } catch (IOException e) {
                        notificationOnFailure.doOnEvent(e);
                    }
                })
                .schedule();
    }

    /**
     * Sends last message to server that client is going offline.
     */
    @Override
    protected void onDisconnectingFromServer() {
        //notify server that you are disconnecting!
        this.cmdToServer(new MS_TcpIPCommand(MS_ClientServerConstants._CLIENT_DISCONNECTS_NOTIFY_MESSAGE));
    }

    /**
     * Synonym for <b>isConnected</b>.
     *
     * @return whether client is ready for message exchange or not.
     */
    @Override
    public boolean isActive() {
        return this.isConnected();
    }

    public long getId() {
        return id;
    }

    public boolean isAutoRestartIfDisconnected() {
        return autoRestartIfDisconnected;
    }

    public void setAutoRestartIfDisconnected(boolean autoRestartIfDisconnected) {
        this.autoRestartIfDisconnected = autoRestartIfDisconnected;
    }

    public boolean isAbortAcknowledgementCmdsOnTimeOut() {
        return abortAcknowledgementCmdsOnTimeOut;
    }

    public void setAbortAcknowledgementCmdsOnTimeOut(boolean abortAcknowledgementCmdsOnTimeOut) {
        this.abortAcknowledgementCmdsOnTimeOut = abortAcknowledgementCmdsOnTimeOut;
    }

    /**
     * There is 4 things that server must know about client:
     * <ol>
     * <li>OS of client;</li>
     * <li>User name in OS of user;</li>
     * <li>In OS directory where user launched his client's application;</li>
     * <li>OS home directory of user.</li>
     * </ol>
     * Those things are sent as hello message to server. Client simply is introducing himself.
     */
    @Override
    protected final void onSuccessfulConnect() {
        this.cmdToServer(new MS_TcpIPCommand(MS_ClientServerConstants._INFO_ABOUT_NEW_CLIENT, new MS_JSONObject()
                .put(MS_ClientServerConstants._CMD_DATA_KEY_OS, MS_CodingUtils.getSystemOS)
                .put(MS_ClientServerConstants._CMD_DATA_KEY_USER_NAME, MS_CodingUtils.getSystemUserName)
                .put(MS_ClientServerConstants._CMD_DATA_KEY_WORKING_DIR, MS_CodingUtils.getSystemUserCurrentWorkingDir)
                .put(MS_ClientServerConstants._CMD_DATA_KEY_HOME_DIR, MS_CodingUtils.getSystemUserHomeDir)
        ));
    }

    private void sendCurrentTimeToTheServer() {
        if (abortAcknowledgementCmdsOnTimeOut && (needToSendCurrentTime.verify())) {
            new MS_FutureEvent().withAction(() -> {
                String currentTime = getCurrentTimeNow();
                this.cmdToServer(new MS_TcpIPCommand(MS_ClientServerConstants._REFRESH_CLIENT_CURRENT_TIME, currentTime));
            }).schedule();
        }
    }

    private String getCurrentTimeNow() {
        return MS_DateTimeUtils.formatDateTime(ZonedDateTime.now(), MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET);
    }
}