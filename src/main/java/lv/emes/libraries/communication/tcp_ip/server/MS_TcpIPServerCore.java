package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.communication.tcp_ip.MS_TcpIPAbstract;
import lv.emes.libraries.patches.android_compat.JavaUtilCompatibility;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants.*;

/**
 * Core things of TCP/IP server. Accessible only to package because will be overridden.
 * <p>Public methods:
 * <ul>
 * <li>registerCommandWithNoData</li>
 * <li>registerCommandWithStringData</li>
 * <li>registerCommandWithJsonObjectData</li>
 * <li>registerCommandWithJsonArrayData</li>
 * <li>registerCommandWithBinaryData</li>
 * <li>startServer</li>
 * <li>stopServer</li>
 * <li>isRunning</li>
 * <li>setPort</li>
 * <li>getPort</li>
 * <li>getClients</li>
 * <li>getClientByID</li>
 * <li>disconnectClientByID</li>
 * <li>disconnectAllClients</li>
 * <li>writeBytes</li>
 * <li>readBytes</li>
 * </ul>
 * <p>Public properties to set:
 * <ul>
 * <li>onDataFormatException</li>
 * <li>onIOException</li>
 * </ul>
 * <p>Protected methods:
 * <ul>
 * <li>onIncomingClientMessage</li>
 * <li>onNewClientConnected</li>
 * <li>writeln</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.1.
 * @since 1.1.1
 */
abstract class MS_TcpIPServerCore extends MS_TcpIPAbstract {

    private int port;
    private ServerSocket server;
    private int lastClientId = 0;

    protected Thread serverThread = null; //thread of server
    protected Map<Long, MS_ClientOfServer> clients;

    /**
     * Creates new server instance and sets its port.
     *
     * @param port 1..~65k.
     */
    public MS_TcpIPServerCore(int port) {
        this.port = port;
        clients = new ConcurrentHashMap<>();
    }

    /**
     * Set this to define behavior of server when it reads message of client.
     *  @param message UTF-8 text from client.
     * @param client  sender.
     */
    abstract protected void onIncomingClientMessage(String message, MS_ClientOfServer client);

    /**
     * Set this to define behavior of server when new client is connected.
     *
     * @param client client that just connected to server.
     */
    abstract protected void onNewClientConnected(MS_ClientOfServer client);

    /**
     * Adds new command to known commands that can be received from any connected client. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithNoData(String code, IFuncOnIncomingCommandFromClientNoData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_NO_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from any connected client. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithStringData(String code, IFuncOnIncomingCommandFromClientStringData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_STRING_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from any connected client. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithJsonObjectData(String code, IFuncOnIncomingCommandFromClientJsonObjectData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_JSON_OBJECT_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from any connected client. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithJsonArrayData(String code, IFuncOnIncomingCommandFromClientJsonArrayData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_JSON_ARRAY_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from any connected client. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithBinaryData(String code, IFuncOnIncomingCommandFromClientBinaryData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_BINARY_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Writes message to particular client.
     *
     * @param msg      UTF-8 text.
     * @param clientID id of client.
     */
    protected synchronized void writeln(String msg, int clientID) {
        try {
            this.getClientByID(clientID).getOut().writeUTF(msg);
        } catch (IOException ignored) {
        }
    }

    /**
     * Changes port in which server will be listening. If this will be done when server is already launched, this will do no effect.
     * You should change this property only before method <b>startServer</b>.
     *
     * @param portNumber particular port number (1..65k)
     */
    public void setPort(int portNumber) {
        port = portNumber;
    }

    public int getPort() {
        return port;
    }

    public String getIPAddress() {
        return server.getInetAddress().getHostAddress();
    }

    /**
     * Starts server at defined port. New thread is created and server starts listening for incoming client connections.
     *
     * @throws IllegalArgumentException if server invalid port defined during creation of server object.
     * @throws IOException              if server cannot be started due to an I/O error when opening the socket.
     */
    public void startServer() throws IllegalArgumentException, IOException {
        server = new ServerSocket(this.getPort());
        Thread t = new Thread(this);
        serverThread = t; //saving for future needs
        isActive = true;
        t.start(); //code goes to run() method
    }

    /**
     * Stops server for listening.
     */
    public void stopServer() {
        if (!isActive) return;
        serverThread.interrupt(); //do not listen for new connections anymore!
        serverThread = null;
        isActive = false;
        try {
            server.close();
        } catch (Exception ignored) {
        }
        server = null;
        if (clients.size() > 0)
            clients.clear(); //also delete all connections
    }

    /**
     * Gets information about server online status.
     *
     * @return true if server is running; false, if it's down.
     */
    public synchronized boolean isRunning() {
        return (server != null) && isActive;
    }

    /**
     * This will listen for new client connections and creates new thread for every connected client.
     */
    @Override
    public void run() {
        try {
            //starts server and waits for new client to establish connection
            while (isActive) {
                Socket socket = server.accept(); //incoming client
                MS_ClientOfServer client = new MS_ClientOfServer(++lastClientId, socket); //assigns id of client and adds it to
                clients.put(client.id, client);
                MS_MessageHandler worker = new MS_MessageHandler(client, this);
                Thread t = new Thread(worker);
                t.setDaemon(true);
                t.start();

                onNewClientConnected(client); //calls method to do actions after client is successfully connected
            }
        } catch (Exception ignored) {
            //everything is ok, because threads are slower than infinite loop, so isActive didn't catch up with actual situation
        } finally {
            stopServer();
        }
    }

    /**
     * @return all the connected clients.
     */
    public Map<Long, MS_ClientOfServer> getClients() {
        return clients;
    }

    /**
     * Disconnects particular client. Also removes client from <b>clients</b> list.
     *
     * @param clientId id of client.
     */
    public void disconnectClientByID(long clientId) {
        MS_ClientOfServer cl = this.getClientByID(clientId);
        if (cl != null) {
            cl.disconnect();
            this.clients.remove(clientId);
        }
    }

    /**
     * Disconnects all clients. Also removes clients from <b>clients</b> map.
     * Server is still running after execution of this method.
     */
    public synchronized void disconnectAllClients() {
        this.lastClientId = 0; //id counter can be restarted, cause no more clients connected => This way ID's cannot conflict with new connection ID's.
        clients.forEach((id, cl) -> cl.disconnect());
        this.clients.clear();
    }

    /**
     * Goes through the list of connected clients and seeks particular client.
     *
     * @param clientId id (starting with 1) of client to look for.
     * @return {@link MS_ClientOfServer} if client found; otherwise returns null.
     */
    public MS_ClientOfServer getClientByID(long clientId) {
        return clients.get(clientId);
    }
}