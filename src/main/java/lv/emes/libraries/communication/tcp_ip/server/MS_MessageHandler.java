package lv.emes.libraries.communication.tcp_ip.server;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.SocketException;
import java.util.Random;

/**
 * Contains common operations for incoming message handling. Used in <b>MS_TcpServerCore</b>.
 *
 * @author eMeS
 * @version 1.2.
 * @see MS_TcpIPServerCore
 */
class MS_MessageHandler implements Runnable {
    //	static Logger log = Logger.getLogger(MS_MessageHandler.class.getName());
    private final int DEFAULT_THREAD_SLEEP_TIME = 250;
    MS_ClientOfServer client;
    MS_TcpIPServerCore server;

    /**
     * Saves references of linked client and server sockets.
     * Also sets I/O handlers so in run()
     *
     * @param client
     * @param server
     */
    public MS_MessageHandler(MS_ClientOfServer client, MS_TcpIPServerCore server) {
        this.client = client;
        this.server = server;
    }

    /**
     * This methods serves for listening of client messages to server.
     * It includes common responses to each client that are defined in <b>server.onIncomingClientMessage</b>.
     * <p>Method ends work only if server is stopped or client is disconnected.
     */
    @Override
    public void run() {
        while (server.isRunning() && client.isConnected()) {
            //Every message is read as UTF-8 String.
            try {
                Thread.sleep(new Random().nextInt(DEFAULT_THREAD_SLEEP_TIME));
                String msg = client.getIn().readUTF();
                server.onIncomingClientMessage(msg, client, client.getOut()); //calls central method of server message reading
            } catch (SocketException e) {
                client.disconnect();
            } catch (UTFDataFormatException exc) {
//					log.error("Failed to read UTF-8 message from client correctly due to incorrect format. @run()");
                if (server.onUTFDataFormatException != null)
                    try {
                        server.onUTFDataFormatException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
            } catch (IOException exc) {
//					log.info("Connection with client with id = " + client.id + " lost. @run()");
                if (server.onIOException != null)
                    try {
                        server.onIOException.doOnEvent(exc);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                client.disconnect();
            } catch (Exception e) { //if another exception then just exit thread
                server.getClients().remove(client);
                return;
            }
        } //while ends here
        server.getClients().remove(client);
        try {
            client.disconnect();
        } catch (Exception e) {
//			log.error("Something unexcepted when trying to close I/O. @run()", e);
        }
    }

}
