package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.threading.IFuncOnSomeException;

/**
 * TCP/IP common core things that contains methods for communication.
 * This class will be overridden to implement {@link MS_TcpIPClient} and {@link MS_TcpIPServer}.
 *
 * @author eMeS
 * @version 2.0.
 * @see lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient
 * @see lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer
 */
public abstract class MS_TcpIPAbstract implements Runnable {

    /**
     * Set this to handle this kind of error when trying to read message sent by communication partner!
     * <p>(exception) -&gt; {};
     */
    public IFuncOnUTFDataFormatException onUTFDataFormatException = Throwable::printStackTrace;
    /**
     * Set this to handle this kind of error when trying to read message sent by communication partner!
     * <p>(exception) -&gt; {};
     */
    public IFuncOnIOException onIOException = Throwable::printStackTrace;

    /**
     * Set this to handle any exception that occurs when executing some caller command.
     */
    public IFuncOnSomeException onExecutionException = Throwable::printStackTrace;

    protected boolean isActive = false;

    protected MS_StringList dataContainer = new MS_StringList();

    /**
     * Adds some data for next command.
     *
     * @param someData textual data content to be added to send together with next command.
     */
    public synchronized void addDataToContainer(String someData) {
        dataContainer.add(someData);
    }

    public abstract boolean getIsActive();
}
