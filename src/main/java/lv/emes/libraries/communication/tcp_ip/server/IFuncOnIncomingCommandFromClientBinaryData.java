package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.communication.tcp_ip.MS_ActionOnIncomingTcpIpCommand;

/**
 * This functional interface is for server-client message handling purposes.
 * Set it to define behavior of incoming message for server.
 * <p>IFuncOnIncomingCommandFromClientBinaryData handler = (server, client, binaryData) -&gt; {methods};
 * <p><b>methods</b> are all the methods to be handled with byte[] type data.
 * <p>MS_TcpIPServer is server object itself.
 * <p>MS_TcpIPClient is client, who sent the command.
 * <p><u>Warning</u>: you need to assume that any data can be <tt>null</tt>, if it's not agreed differently with server side.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
@FunctionalInterface
public interface IFuncOnIncomingCommandFromClientBinaryData extends MS_ActionOnIncomingTcpIpCommand {

    void handleCommand(MS_TcpIPServer server, MS_ClientOfServer client, byte[] data);
}