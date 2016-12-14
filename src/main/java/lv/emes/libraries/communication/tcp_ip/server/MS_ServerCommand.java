package lv.emes.libraries.communication.tcp_ip.server;

/**
 * Mechanism to recognize messages sent by client and do common actions to respond.
 * @author eMeS
 * @see lv.emes.libraries.communication.tcp_ip.server.IFuncOnIncomingClientMessage
 */
public class MS_ServerCommand {
	public String code = "";
	public IFuncOnIncomingClientMessage doOnCommand;
	
	/**
	 * Just create new empty command. After this <b>code</b> and <b>doOnCommand</b> should be set.
	 */
	public MS_ServerCommand() {	}
	
	/**
	 * Creates command with particular name.<br>
	 * After this <b>doOnCommand</b> should be set.
	 * @param code unique name of command.
	 */
	public MS_ServerCommand(String code) {
		this.code = code;
	}
	
	/**
	 * Creates command with particular name and behavior.
	 * @param code unique name of command.
	 * @param lambda behavior of command. That is recommended to use lambda expression to fill this like:
	 * <p><code>
	 * (server, data, client, out) -&gt; {methods};</code>
	 * @see lv.emes.libraries.communication.tcp_ip.server.IFuncOnIncomingClientMessage
	 */
	public MS_ServerCommand(String code, IFuncOnIncomingClientMessage lambda) {
		this.code = code;
		this.doOnCommand = lambda;
	}
}
