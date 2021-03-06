package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.tcp_ip.MS_ActionOnIncomingTcpIpCommand;

/**
 * This functional interface is for client-server message handling purposes.
 * Set it to define behavior of incoming message for server.
 * <p>IFuncOnIncomingCommandFromServerJsonArrayData handler = (client, dataArr) -&gt; {methods};
 * <p><b>methods</b> are all the methods to be handled with {@link MS_JSONArray} type data.
 * <p>MS_TcpIPClient client is client object itself.
 * <p><u>Warning</u>: you need to assume that any data can be <tt>null</tt>, if it's not agreed differently with server side.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
@FunctionalInterface
public interface IFuncOnIncomingCommandFromServerJsonArrayData extends MS_ActionOnIncomingTcpIpCommand {

    void handleCommand(MS_TcpIPClient client, MS_JSONArray data);
}