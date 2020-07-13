package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants;
import lv.emes.libraries.communication.tcp_ip.MS_TcpIPAbstract;
import lv.emes.libraries.patches.android_compat.JavaUtilCompatibility;
import lv.emes.libraries.tools.MS_BadSetupException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import static lv.emes.libraries.communication.tcp_ip.MS_ClientServerConstants.*;

/**
 * Core things of TCP/IP client. Accessible only to package because will be overridden.
 * <p>Public methods:
 * <ul>
 * <li>registerCommandWithNoData</li>
 * <li>registerCommandWithStringData</li>
 * <li>registerCommandWithJsonObjectData</li>
 * <li>registerCommandWithJsonArrayData</li>
 * <li>registerCommandWithBinaryData</li>
 * <li>connect</li>
 * <li>disconnect</li>
 * <li>isConnected</li>
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
 * <li>onIncomingServerMessage</li>
 * <li>onDisconnectingFromServer</li>
 * <li>writeln</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.2.
 * @since 1.1.1
 */
abstract class MS_TcpIPClientCore extends MS_TcpIPAbstract {

    private final int DEFAULT_THREAD_SLEEP_TIME = 250;
    private Socket server; // server socket
    private int connectTimeout = MS_ClientServerConstants._DEFAULT_CONNECT_TIMEOUT; //connection to server timeout in milliseconds
    private int writeTimeout = MS_ClientServerConstants._DEFAULT_WRITE_TIMEOUT; //write timeout (milliseconds)

    protected int lastConnectedPort = -1;
    protected String lastConnectedHost = "";
    protected DataInputStream in;
    protected DataOutputStream out; // writer to server socket stream
    protected Thread messageThread = null; //thread for UTF-8 text message reader

    /**
     * Set this to define behavior of client when it reads message send by server.
     *
     * @param message UTF-8 text from server.
     */
    abstract protected void onIncomingServerMessage(String message);

    abstract protected void onDisconnectingFromServer();

    /**
     * Implement this method to send hello or something else to server!
     */
    abstract protected void onSuccessfulConnect();

    /**
     * Establishes new TCP/IP connection to the server. Message listener goes to new thread and listens for messages.
     *
     * @param host hostname or IP address of server.
     * @param port port number in which server can accept connections.
     * @throws UnknownHostException     if the IP address of the host could not be determined.
     * @throws IOException              if an I/O error occurs when creating the socket or streams (rare case).
     * @throws SocketTimeoutException   if timeout expires before connecting.
     * @throws IllegalArgumentException if hostname is <tt>null</tt> or the port parameter is outside the specified range
     *                                  of valid port values, which is between 0 and 65535, inclusive.
     */
    public void connect(String host, int port) throws IOException, SocketTimeoutException, IllegalArgumentException {
        if (this.isConnected()) return;
        server = new Socket();
        try {
            server.connect(new InetSocketAddress(host, port), connectTimeout);
            in = new DataInputStream(server.getInputStream());
            out = new DataOutputStream(server.getOutputStream());
        } catch (Exception e) {
            server = null;
            throw e;
        }
        Thread t = new Thread(this);
        messageThread = t;
        t.start();

        lastConnectedHost = host; //for later use
        lastConnectedPort = port;
        if (this.isConnected())
            onSuccessfulConnect();
    }

    public void disconnect() {
        onDisconnectingFromServer();
        closeAllDataStreams();
    }

    private void closeAllDataStreams() {
        try {
            messageThread.interrupt();
            server.close();
        } catch (Exception ignored) {
        } finally {
            messageThread = null;
            server = null;
        }
    }

    /**
     * Disconnects from the server if connected and attempts to establish new connection.
     *
     * @throws IOException              if an I/O error occurs when creating the socket or streams (rare case).
     * @throws SocketTimeoutException   if timeout expires before re-connecting.
     */
    public void restartConnection() throws IOException, SocketTimeoutException {
        if (isConnected()) disconnect();
        connect(lastConnectedHost, lastConnectedPort);
    }

    /**
     * Adds new command to known commands that can be received from server. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithNoData(String code, IFuncOnIncomingCommandFromServerNoData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_NO_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from server. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithStringData(String code, IFuncOnIncomingCommandFromServerStringData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_STRING_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from server. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithJsonObjectData(String code, IFuncOnIncomingCommandFromServerJsonObjectData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_JSON_OBJECT_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from server. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithJsonArrayData(String code, IFuncOnIncomingCommandFromServerJsonArrayData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_JSON_ARRAY_DATA, commands -> new HashMap<>()).put(code, action);
    }

    /**
     * Adds new command to known commands that can be received from server. Commands are identified by code.
     * All commands must be unique. Commands cannot be deleted, except by clearing command list.
     *
     * @param code   unique command code.
     * @param action method which will be triggered when client receives command with given <b>code</b>.
     */
    public void registerCommandWithBinaryData(String code, IFuncOnIncomingCommandFromServerBinaryData action) {
        Objects.requireNonNull(code);
        Objects.requireNonNull(action);
        JavaUtilCompatibility.computeIfAbsent(commands, _CMD_WITH_BINARY_DATA, commands -> new HashMap<>()).put(code, action);
    }

    @Override
    public void run() {
        // Read messages in the loop sent from server socket in this thread
        while (isConnected()) {
            // Every message is read as UTF-8 String.
            try {
                Thread.sleep(new Random().nextInt(DEFAULT_THREAD_SLEEP_TIME));
                onIncomingServerMessage(in.readUTF()); //calls central method of client message reading
            } catch (EOFException | InterruptedException exc) { //happens when server disconnects client without warning
                disconnect();
            } catch (UTFDataFormatException exc) {
                if (onDataFormatException != null)
                    try {
                        if (this.isConnected()) //many times this error just happens because of client disconnected himself from the server, so check, if he is connected
                            onDataFormatException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            } catch (IOException exc) {
                if (onIOException != null)
                    try {
                        if (this.isConnected()) //many times this error just happens because of client disconnected himself from the server, so check, if he is connected
                            onIOException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new MS_BadSetupException(e);
                    }
                disconnect();
            }
        } //while ends here
    }

    /**
     * If connected writes message to server.
     *
     * @param msg UTF-8 text.
     * @return true if message successfully sent; false if not connected.
     */
    protected synchronized boolean writeln(String msg) {
        try {
            this.out.writeUTF(msg);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isConnected() {
        return (server != null) && server.isConnected();
    }

    /**
     * @return connection to server timeout in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * @param connectTimeout connection to server timeout in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * @return timeout (milliseconds) or TTL of single command "on air", which is not delivered to server yet.
     * A timeout of zero is interpreted as an infinite timeout.
     */
    public int getWriteTimeout() {
        return writeTimeout;
    }

    /**
     * @param writeTimeout timeout (milliseconds) or TTL of single command "on air", which is not delivered to server yet.
     *                     A timeout of zero is interpreted as an infinite timeout.
     */
    public void setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}