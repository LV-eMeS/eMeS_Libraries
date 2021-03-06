package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.temporal.ChronoUnit;

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
 * @version 1.7.
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

    private Long timeDiffFromServer;

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

    /**
     * If client ever sent his current time to server this method returns calculated time difference between server and client.
     *
     * @return <tt>null</tt> if client time was never set.
     * Value in nanoseconds that is positive if server time is closer to the future than client time (client is behind)
     * or negative if server time is closer to the past (client is ahead).
     */
    public Long getTimeDiffFromServer() {
        return timeDiffFromServer;
    }

    public void setClientTime(ZonedDateTime clientTime) {
        ZonedDateTime serverCurrentTime = ZonedDateTime.now();
        long diff = ChronoUnit.NANOS.between(clientTime, serverCurrentTime);
        if (this.timeDiffFromServer != null) {
            // Try to adjust the value based on previous one in order to make it to be more precise
            // Take smallest gap if the difference between previous and current diff is greater than 1 second
            long differenceBetweenDiffs = Math.abs(this.timeDiffFromServer) - Math.abs(diff);
            if (Math.abs(differenceBetweenDiffs) > 1_000_000_000L) {
                diff = differenceBetweenDiffs > 0L ? diff : this.timeDiffFromServer;
            } else { // Otherwise we can simply take avg value
                diff = (this.timeDiffFromServer + diff) / 2;
            }
        }
        this.timeDiffFromServer = diff;
    }
}
