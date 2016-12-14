package lv.emes.libraries.communication.tcp_ip.client;

import lv.emes.libraries.tools.lists.MS_StringList;

import java.io.DataOutputStream;

/**
 * This functional interface is for client-server message handling purposes. 
 * Set it to define behavior of incoming message for server.
 * <p>IFuncOnIncomingClientMessage handler = (client, data, out) -&gt; {methods};
 * <p><b>methods</b> are all the methods to be handled with list of string type data. 
 * <p>MS_TcpIPClient client is client object itself. 
 * <br>For data first element of data list is command code, remaining elements are user
 * defined data that will come together with message. The point is to instruct server by using this data. 
 * <br>DataOutputStream out - stream which can be used to send some message to server.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnIncomingServerMessage {
	void doMessageHandling(MS_TcpIPClient client, MS_StringList data, DataOutputStream out);
}