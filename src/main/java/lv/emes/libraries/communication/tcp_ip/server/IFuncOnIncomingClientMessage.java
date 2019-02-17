package lv.emes.libraries.communication.tcp_ip.server;

import lv.emes.libraries.tools.lists.MS_StringList;

import java.io.DataOutputStream;

/**
 * This functional interface is for client-server message handling purposes.
 * Set it to define behavior of incoming message for client.
 * <p><code>
 * IFuncOnIncomingClientMessage handler = (server, data, client, out) -&gt; {methods};</code>
 * <p><b>methods</b> are all the methods to be handled with list of string type data.
 * <p>MS_TcpIPServer server is server object itself.
 * <br>For data first element of data list is command code, remaining elements are user
 * defined data that will come together with message. The point is to instruct server by using this data.
 * <br>MS_ClientOfServer client - info about particular client.
 * <br>DataOutputStream out - stream which can be used to send some message to client.
 *
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnIncomingClientMessage {

    void doMessageHandling(MS_TcpIPServer server, MS_StringList data, MS_ClientOfServer client, DataOutputStream out);
}