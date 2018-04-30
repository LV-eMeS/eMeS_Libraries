package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.tcp_ip.MS_TcpIPAbstract;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Core things of TCP/IP client. Accessible only to package because will be overridden.
 * <p>Public methods:
 * <ul>
 * <li>connect</li>
 * <li>disconnect</li>
 * <li>isConnected</li>
 * <li>writeBytes</li>
 * <li>readBytes</li>
 * </ul>
 * <p>Public properties to set:
 * <ul>
 * <li>onUTFDataFormatException</li>
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
 * @version 1.3.
 */
abstract class MS_TcpIPClientCore extends MS_TcpIPAbstract {

    private final int DEFAULT_THREAD_SLEEP_TIME = 250;
    private Socket server; // server socket

    protected int lastConnectedPort = -1;
    protected String lastConnectedHost = "";
    protected DataInputStream in;
    protected DataOutputStream out; // writer to server socket stream
    protected Thread messageThread = null; //thread for UTF-8 text message reader

    /**
     * Set this to define behavior of client when it reads message of client.
     *
     * @param message UTF-8 text from server.
     * @param out     output stream of server socket to use for sending client's response back to server.
     */
    abstract protected void onIncomingServerMessage(String message, DataOutputStream out);

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
     * @throws IOException              if an I/O error occurs when creating the socket or streams.
     * @throws IllegalArgumentException if the port parameter is outside the specified range of valid port values, which is between 0 and 65535, inclusive.
     */
    public void connect(String host, int port) throws IOException, IllegalArgumentException {
//			log.error("Failed to connect to server. \n@connectToServer(" + ip + ", " + port + ")");
        if (this.isConnected()) return;
        server = new Socket(host, port);
        in = new DataInputStream(server.getInputStream());
        out = new DataOutputStream(server.getOutputStream());
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
        // close all data streams
        try {
            messageThread.interrupt();
            messageThread = null;
            server.close();
            server = null;
        } catch (Exception e) {
//			log.error("Failed to close server socket and data streams. \n@disconnectFromServer()");
        }
    }

    @Override
    public void run() {
        //  read messages in the loop sent from server socket in this thread
        while (isConnected()) {
            //Every message is read as UTF-8 String.
            try {
                Thread.sleep(new Random().nextInt(DEFAULT_THREAD_SLEEP_TIME));
                String msg = in.readUTF();
                onIncomingServerMessage(msg, out); //calls central method of client message reading
            } catch (EOFException | InterruptedException exc) { //happens when server disconnects client without warning
                disconnect();
            } catch (UTFDataFormatException exc) {
//				log.error("Failed to read UTF-8 message from server correctly due to incorrect format. @run()");
                if (onUTFDataFormatException != null)
                    try {
                        if (this.isConnected()) //many times this error just happens because of client disconnected himself from the server, so check, if he is connected
                            onUTFDataFormatException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            } catch (IOException exc) {
//				log.info("Connection with server lost. @run()");
                if (onIOException != null)
                    try {
                        if (this.isConnected()) //many times this error just happens because of client disconnected himself from the server, so check, if he is connected
                            onIOException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
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
//			log.error(String.format("Couldn't write UTF-8 message to server. \n. @writeln(%s)", MS_StringUtils.eMeSSubstring(msg, 1, 50) + "..."));
            return false;
        }
    }

    public boolean isConnected() {
        return (server != null) && server.isConnected();
    }
}