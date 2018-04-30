package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.threading.MS_FutureEvent;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * TCP/IP server which operates commands to communicate to client.
 * <p>Public methods:
 * <ul>
 * <li>registerNewCommand</li>
 * <li>getCommandList</li>
 * <li>addDataToContainer</li>
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
 * <li>getIsActive</li>
 * <li>getPort</li>
 * <li>getClients</li>
 * <li>getClientByID</li>
 *
 * <li>setPort</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.2.
 */
public class MS_TcpIPServer extends MS_TcpIPServerCore {

    //PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
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
    //PRIVATE VARIABLES
    private MS_List<MS_ServerCommand> commandList = new MS_List<>();
    /**
     * If true, all clients will be notified that server is going down when <b>stopServer</b> is called.
     * Default: true.
     */
    public boolean notifyClientsOnDC = true;

    //KONSTRUKTORI

    /**
     * Creates new server instance and sets its port.
     *
     * @param port 1..~65k.
     */
    public MS_TcpIPServer(int port) {
        super(port);
        //save information about connecting client. Right after connection success client will send some info about himself (OS,
        MS_ServerCommand tmp = new MS_ServerCommand();
        tmp.code = MS_ClientServerConstants._INFO_ABOUT_NEW_CLIENT;
        tmp.doOnCommand = (server, data, client, out) -> {
            client.os = data.get(1);
            client.osUserName = data.get(2);
            client.currentWorkingDirectory = data.get(3);
            client.systemHomeDirectory = data.get(4);
            if (onClientSayingHi != null)
                try {
                    onClientSayingHi.doOnEvent(client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        };
        this.registerNewCommand(tmp);

        //set behavior of client disconnecting. Note that server and list already is aware that client is missing.
        //Here you need just to set user-defined behavior
        tmp = new MS_ServerCommand();
        tmp.code = MS_ClientServerConstants._CLIENT_DISCONNECTS_NOTIFY_MESSAGE;
        tmp.doOnCommand = (server, data, client, out) -> {
            //user can define his own expected behavior when server goes down.
            if (onClientGoingOffline != null)
                try {
                    onClientGoingOffline.doOnEvent(client);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        };
        this.registerNewCommand(tmp);
    }

    //PUBLIC METHODS
    @Override
    protected void onIncomingClientMessage(String message, MS_ClientOfServer client, DataOutputStream out) {
        //Every time client sends a message server reads it.
        //Messages are formatted in specific format.
        MS_StringList data = new MS_StringList(message);
        String userCmd = data.get(0);

        for (MS_ServerCommand cmd : commandList)
            if (cmd.code.equals(userCmd)) { //command found
                if (cmd.doOnCommand != null) {
                    MS_FutureEvent commandExecution = new MS_FutureEvent()
                            .withThreadName("MS_TcpIPServer.onIncomingClientMessage")
                            .withAction(() -> cmd.doOnCommand.doMessageHandling(this, data, client, out));
                    if (this.onExecutionException != null)
                        commandExecution.withActionOnException((ex) -> this.onExecutionException.doOnError(ex));
                    commandExecution.schedule();
                }
                return;
            }
    }

    /**
     * Adds new command to command list. Commands are recognized by code. All commands must be unique.
     * Commands cannot be deleted, except by clearing command list.
     * If client sends particular command to server, server acts just as command's implementation asks.
     *
     * @param cmd command code and method which will be triggered when client sends particular command.
     */
    public void registerNewCommand(MS_ServerCommand cmd) {
        commandList.add(cmd);
    }

    /**
     * That is recommended not to use this variable.
     * Use only if really necessary to do manual changes in list of registered commands.
     *
     * @return List of all registered commands.
     * @see MS_TcpIPServer#registerNewCommand
     */
    public MS_List<MS_ServerCommand> getCommandList() {
        return commandList;
    }

    /**
     * Tries to find command in registered command list.
     *
     * @param cmdCode code of command.
     * @return MS_ServerCommand or null if command doesn't exist in registered command list.
     */
    public MS_ServerCommand getCommandByCode(String cmdCode) {
        for (MS_ServerCommand cmd : commandList)
            if (cmd.code.equals(cmdCode))
                return cmd;
        return null;
    }

    /**
     * Use this method to send custom command to particular client by his ID!
     * If some data added with <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method in every case.
     *
     * @param cmdCode  command ID.
     * @param clientID id of addressee.
     * @return false if error occurred during sending message.
     * @see MS_TcpIPServer#addDataToContainer
     */
    public synchronized boolean cmdToClientByID(String cmdCode, int clientID) {
        dataContainer.insert(0, cmdCode);
        try {
            String message = dataContainer.toString();
            dataContainer.clear(); //after every message container is cleared
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
     * @param cmdCode command ID.
     * @param client  addressee itself.
     * @return false if error occurred during sending message.
     * @see MS_TcpIPServer#addDataToContainer
     */
    public synchronized boolean cmdToClient(String cmdCode, MS_ClientOfServer client) {
        dataContainer.insert(0, cmdCode);
        try {
            String message = dataContainer.toString();
            dataContainer.clear(); //after every message container is cleared
            client.getOut().writeUTF(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Use this method to send custom command to every single connected client!
     * This method works silently and don't throw any exceptions if something bad happens,
     * but it tries to send messages to every single client even if IOException happens in the middle of list.<p>
     * If some data added with <b>addDataToContainer</b>, those will also be sent by this message.
     * <p>Data is simply string list with index starting with 1, cause 0 is for <b>cmdCode</b>.
     * Data container is cleared after this method.
     *
     * @param cmdCode command ID.
     * @see MS_TcpIPServer#addDataToContainer
     */
    public synchronized void cmdToAll(String cmdCode) {
        dataContainer.insert(0, cmdCode);
        String message = dataContainer.toString();
        dataContainer.clear(); //after every message container is cleared
        for (MS_ClientOfServer client : clients)
            try {
                client.getOut().writeUTF(message);
            } catch (Exception e) {
            }
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
            this.cmdToAll(MS_ClientServerConstants._DC_NOTIFY_MESSAGE);
        //make them all disconnect themselves too (only server side sockets, not client side sockets)
        this.disconnectAllClients();
        super.stopServer();
    }

    @Override
    protected void onNewClientConnected(MS_ClientOfServer client) {
        this.addDataToContainer(Integer.toString(client.id));
        this.cmdToClient(MS_ClientServerConstants._NEW_CLIENT_ID_NOTIFY_MESSAGE, client);
        if (onClientConnecting != null)
            try {
                onClientConnecting.doOnEvent(client);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Synonym for <b>isRunning</b>.
     *
     * @return whether server is ready for message exchange or not.
     */
    @Override
    public boolean getIsActive() {
        return this.isRunning();
    }
}
