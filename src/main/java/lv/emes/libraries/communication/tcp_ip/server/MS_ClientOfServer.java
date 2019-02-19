package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.tools.MS_BadSetupException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Holds information about connected client.
 * <p>Public methods:
 * <ul>
 * <li>isConnected</li>
 * <li>disconnect</li>
 * <li>getSocket</li>
 * <li>getId</li>
 * <li>getIp</li>
 * <li>getOut</li>
 * <li>isConnected</li>
 * </ul>
 *
 * @version 1.6.
 */
public class MS_ClientOfServer {

    protected long id;
    protected String osUserName = "";
    protected String os = "";
    protected String currentWorkingDirectory = "";
    protected String systemHomeDirectory = "";
    protected boolean isConnected = true;
    protected Socket clientSocket;
    // I/O
    protected DataInputStream in; // for reading from socket
    protected DataOutputStream out; // for writing to socket

    /**
     * @return input stream for client message reading.
     */
    public DataInputStream getIn() {
        return in;
    }

    /**
     * @return output stream for writing messages to client.
     */
    public DataOutputStream getOut() {
        return out;
    }

    /**
     * Saves information about connected client. Also created new DataInput/Output streams for socket of client.
     *
     * @param id           unique number to identify client in list of clients.
     * @param clientSocket tcp/ip socket for connection operations.
     */
    public MS_ClientOfServer(long id, Socket clientSocket) {
        this.id = id;
        this.clientSocket = clientSocket;
        try {
            this.in = new DataInputStream(this.clientSocket.getInputStream());
            this.out = new DataOutputStream(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new MS_BadSetupException(e);
        }
    }

    public synchronized boolean isConnected() {
        return isConnected;
    }

    /**
     * Closes connection with server socket.
     */
    void disconnect() {
        this.isConnected = false;
        if (clientSocket != null)
            try {
                clientSocket.close();
                clientSocket = null;
            } catch (Exception ignored) {
            }
    }

    /**
     * @return socket of connection.
     */
    public Socket getSocket() {
        return clientSocket;
    }

    /**
     * @return server's assigned unique ID of connection.
     */
    public long getId() {
        return id;
    }

    /**
     * @return IP address of connected client or <tt>null</tt> if client connection is already closed.
     */
    public String getIp() {
        return clientSocket == null ? null :
                clientSocket.getInetAddress() == null ? null :
                        clientSocket.getInetAddress().getHostAddress();
    }

    /**
     * @return User name in OS.
     */
    public String getOSUserName() {
        return osUserName;
    }

    /**
     * @return Operating system of user.
     */
    public String getOSName() {
        return os;
    }

    /**
     * @return System directory where user launched his client's application.
     */
    public String getCurrentWorkingDirectory() {
        return currentWorkingDirectory;
    }

    /**
     * @return OS home directory of user.
     */
    public String getSystemHomeDirectory() {
        return systemHomeDirectory;
    }
}
