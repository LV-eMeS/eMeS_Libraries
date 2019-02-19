package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.tcp_ip.MS_ActionOnIncomingTcpIpCommand;

/**
 * This functional interface is for client-server message handling purposes.
 * Set it to define behavior of incoming message for server.
 * <p>IFuncOnIncomingCommandFromServerNoData handler = client -&gt; {methods};
 * <p><b>methods</b> are all the methods to be handled.
 * <p>MS_TcpIPClient client is client object itself.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
@FunctionalInterface
public interface IFuncOnIncomingCommandFromServerNoData extends MS_ActionOnIncomingTcpIpCommand {

    void handleCommand(MS_TcpIPClient client);
}