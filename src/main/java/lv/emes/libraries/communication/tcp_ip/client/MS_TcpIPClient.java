package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.http.MS_Polling;
import lv.emes.libraries.communication.tcp_ip.IFuncOnIOException;
import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_ExecutionFailureException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

/**
 * TCP/IP client which operates commands to connect and communicate to server.
 * <p>Public methods:
 * <ul>
 * <li>registerNewCommand</li>
 * <li>getCommandList</li>
 * <li>addDataToContainer</li>
 * <li>cmdToServer</li>
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
 * @version 2.0.
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

    private MS_List<MS_ClientCommand> commandList = new MS_List<>();
    private MS_StringList dataContainer = new MS_StringList();
    private int id = 0;
    private Queue<String> sentCommandsWaitingForAcknowledgement = new ConcurrentLinkedQueue<>();

    /**
     * Creates object and initializes default commands.
     */
    public MS_TcpIPClient() {
        // Set behavior of server disconnecting
        MS_ClientCommand tmp = new MS_ClientCommand();
        tmp.code = MS_ClientServerConstants._DC_NOTIFY_MESSAGE;
        tmp.doOnCommand = (client, data, out) -> {
            this.disconnect(); //break connection, because it's already closed from server's side
            //user can define his own expected behavior when server goes down.
            if (onServerGoingDown != null)
                onServerGoingDown.doOnEvent();
        };
        this.registerNewCommand(tmp);

        // Save this client's ID
        tmp = new MS_ClientCommand();
        tmp.code = MS_ClientServerConstants._NEW_CLIENT_ID_NOTIFY_MESSAGE;
        tmp.doOnCommand = (client, data, out) -> {
            id = data.getAsInteger(1); //save the id
        };
        this.registerNewCommand(tmp);

        // Register server acknowledgement of received command
        tmp = new MS_ClientCommand();
        tmp.code = MS_ClientServerConstants._SERVER_ACKNOWLEDGEMENT;
        tmp.doOnCommand = (client, data, out) -> {
            sentCommandsWaitingForAcknowledgement.remove(data.get(1));
        };
        this.registerNewCommand(tmp);
    }

    @Override
    protected void onIncomingServerMessage(String message, DataOutputStream out) {
        //Every time server sends a message client reads it.
        //Messages are formatted in specific format.
        MS_StringList data = new MS_StringList(message);
        String userCmd = data.get(0);

        for (MS_ClientCommand cmd : commandList)
            if (cmd.code.equals(userCmd)) { //command found
                if (cmd.doOnCommand != null) {
                    MS_FutureEvent commandExecution = new MS_FutureEvent()
                            .withThreadName("MS_TcpIPClient.onIncomingServerMessage")
                            .withAction(() -> cmd.doOnCommand.doMessageHandling(this, data, out));
                    if (onExecutionException != null)
                        commandExecution.withActionOnException((ex) -> this.onExecutionException.doOnError(ex));
                    commandExecution.schedule();
                }
                return;
            }
    }

    /**
     * Adds new command to command list. Commands are recognized by code. All commands must be unique.
     * Commands cannot be deleted, except by clearing command list.
     * If server sends particular command to client, server acts just as command's implementation asks.
     *
     * @param cmd command code and method which will be triggered when client sends particular command.
     */
    public void registerNewCommand(MS_ClientCommand cmd) {
        if (cmd != null) commandList.add(cmd);
    }

    /**
     * That is recommended not to use this variable.
     * Use only if it is really necessary to do manual changes in list of already registered commands.
     *
     * @return List of all registered commands.
     * @see MS_TcpIPClient#registerNewCommand
     */
    public MS_List<MS_ClientCommand> getCommandList() {
        return commandList;
    }

    public int getId() {
        return id;
    }

    /**
     * Tries to find command in registered command list.
     *
     * @param cmdCode code of command.
     * @return MS_ClientCommand or null if command doesn't exist in registered command list.
     */
    public MS_ClientCommand getCommandByCode(String cmdCode) {
        for (MS_ClientCommand cmd : commandList)
            if (cmd.code.equals(cmdCode)) return cmd;
        return null;
    }

    /**
     * Adds some data for next command.
     *
     * @param someData textual data content to be added to send together with next command.
     */
    public synchronized void addDataToContainer(String someData) {
        dataContainer.add(someData);
    }

    /**
     * Use this method to send custom command to server!
     * If some data added by <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param cmdCode command ID.
     * @return false if error occurred during sending message or connection is not established.
     * @see MS_TcpIPClient#addDataToContainer
     */
    public synchronized boolean cmdToServer(String cmdCode) {
        if (!isConnected()) return false;
        String message = prepareMessageToSend(cmdCode);
        return this.writeln(message);
    }

    /**
     * Use this method to send custom command to server and expect exception!
     * If some data added by <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param cmdCode command ID.
     * @throws IOException if message couldn't be sent or server is not connected.
     * @see MS_TcpIPClient#addDataToContainer
     */
    public synchronized void cmdToServerExc(String cmdCode) throws IOException {
        if (!isConnected())
            throw new IOException("Command [" + cmdCode + "] cannot be sent because server is disconnected");

        String message = prepareMessageToSend(cmdCode);
        this.out.writeUTF(message);
    }

    /**
     * <u>Warning</u>: This is a thread blocking method.
     * <p>Use this method to send custom command to server and wait for acknowledgment from server that command is received.
     * Maximum waiting time (in milliseconds) until {@link SocketTimeoutException} will be thrown is
     * specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     * <p>If some data added by <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param cmdCode command ID.
     * @throws IOException            if message couldn't be sent or server is not connected.
     * @throws SocketTimeoutException if server doesn't answer in given time specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     * @see MS_TcpIPClient#addDataToContainer
     */
    public synchronized void cmdToServerAcknowledge(String cmdCode) throws SocketTimeoutException, IOException {
        if (!isConnected())
            throw new IOException("Command [" + cmdCode + "] cannot be sent because server is disconnected");

        String commandId = UUID.randomUUID().toString();
        sentCommandsWaitingForAcknowledgement.add(commandId);
        dataContainer.insert(0, commandId);
        dataContainer.insert(1, cmdCode);
        this.out.writeUTF(prepareMessageToSend(MS_ClientServerConstants._CLIENT_COMMAND_WITH_ACKNOWLEDGEMENT_MODE));
        try {
            new MS_Polling<Queue<String>>()
                    .withAction(() -> sentCommandsWaitingForAcknowledgement)
                    .withCheck((commands) -> !commands.contains(commandId))
                    .withSleepInterval(POLLING_SLEEP_INTERVAL)
                    .withMaxPollingAttempts(getWriteTimeout() > POLLING_SLEEP_INTERVAL ? getWriteTimeout() / POLLING_SLEEP_INTERVAL : 1)
                    .poll();
        } catch (MS_ExecutionFailureException e) {
            throw new SocketTimeoutException(String.format("Failed to receive server acknowledgement of execution of command [%s]", commandId));
        }
    }

    /**
     * Use this method to send custom command to server and after acknowledgment from server that command is received
     * perform specific action <b>notificationOnSuccess</b>.
     * Maximum waiting time (in milliseconds) for thread to wait for is
     * specified by <b>writeTimeout</b> ({@link MS_TcpIPClient#getWriteTimeout()}).
     * <p>If some data added by <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param cmdCode               command ID.
     * @param notificationOnSuccess action to do when command is successfully executed on server side.
     * @param notificationOnFailure action to do when execution of command fails.
     * @see MS_TcpIPClient#addDataToContainer
     */
    public void cmdToServerNotify(String cmdCode, Consumer<String> notificationOnSuccess, IFuncOnIOException notificationOnFailure) {
        Objects.requireNonNull(notificationOnSuccess);
        Objects.requireNonNull(notificationOnFailure);
        new MS_FutureEvent()
                .withThreadName("MS_TcpIPClient.cmdToServerNotify")
                .withTimeout(this.getWriteTimeout())
                .withAction(() -> {
                    try {
                        cmdToServerAcknowledge(cmdCode);
                        notificationOnSuccess.accept(cmdCode);
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
        this.cmdToServer(MS_ClientServerConstants._CLIENT_DISCONNECTS_NOTIFY_MESSAGE);
    }

    /**
     * Synonym for <b>isConnected</b>.
     *
     * @return whether client is ready for message exchange or not.
     */
    @Override
    public boolean getIsActive() {
        return this.isConnected();
    }

    /**
     * There is 4 things that server must know about client:<br>
     * 1) OS of client;<br>
     * 2) User name in OS of user;<br>
     * 3) In OS directory where user launched his client's application;<br>
     * 4) OS home directory of user;<br>
     * Those things are sent as hello message to server. Client simply is introducing himself.
     */
    @Override
    protected final void onSuccessfulConnect() {
        this.addDataToContainer(MS_CodingUtils.getSystemOS);
        this.addDataToContainer(MS_CodingUtils.getSystemUserName);
        this.addDataToContainer(MS_CodingUtils.getSystemUserCurrentWorkingDir);
        this.addDataToContainer(MS_CodingUtils.getSystemUserHomeDir);
        this.cmdToServer(MS_ClientServerConstants._INFO_ABOUT_NEW_CLIENT);
    }

    private String prepareMessageToSend(String cmdHeader) {
        dataContainer.insert(0, cmdHeader);
        String message = dataContainer.toString();
        dataContainer.clear(); //after every message container is cleared
        return message;
    }
}