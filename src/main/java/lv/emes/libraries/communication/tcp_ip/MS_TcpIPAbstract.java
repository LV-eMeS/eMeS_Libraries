package lv.emes.libraries.communication.tcp_ip;

import lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient;
import lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer;
import lv.emes.libraries.tools.threading.IFuncOnSomeException;

import java.util.HashMap;
import java.util.Map;

/**
 * TCP/IP common core things that contains methods for communication.
 * This class is overridden to implement {@link MS_TcpIPClient} and {@link MS_TcpIPServer}.
 *
 * @author eMeS
 * @version 2.1.
 * @since 1.1.1.
 * @see lv.emes.libraries.communication.tcp_ip.client.MS_TcpIPClient
 * @see lv.emes.libraries.communication.tcp_ip.server.MS_TcpIPServer
 */
public abstract class MS_TcpIPAbstract implements Runnable {

    /**
     * Set this to handle this kind of error when trying to read message sent by communication partner!
     * <p>(exception) -&gt; {};
     */
    public IFuncOnUTFDataFormatException onDataFormatException = Throwable::printStackTrace;
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

    //Registered command actions by their type (Integer) and code (String)
    protected Map<Integer, Map<String, MS_ActionOnIncomingTcpIpCommand>> commands = new HashMap<>();

    public abstract boolean isActive();
}
