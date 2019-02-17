package lv.emes.libraries.communication.tcp_ip.client;

/**
 * Mechanism to recognize messages sent by client and do common actions to respond.
 *
 * @author eMeS
 */
public class MS_ClientCommand {

    public String code = "";
    /**
     * (client, data, out) -&gt; {methods};
     */
    public IFuncOnIncomingServerMessage doOnCommand;

    /**
     * Just create new empty command. After this <b>code</b> and <b>doOnCommand</b> should be set.
     */
    public MS_ClientCommand() {
    }

    /**
     * Creates command with particular name.<br>
     * After this <b>doOnCommand</b> should be set.
     *
     * @param code unique name of command.
     */
    public MS_ClientCommand(String code) {
        this.code = code;
    }

    /**
     * Creates command with particular name and behavior.
     *
     * @param code   unique name of command.
     * @param lambda behavior of command. That is recommended to use lambda expression to fill this like:<p>
     *               (client, data, out) -&gt; {methods};
     * @see lv.emes.libraries.communication.tcp_ip.client.IFuncOnIncomingServerMessage
     */
    public MS_ClientCommand(String code, IFuncOnIncomingServerMessage lambda) {
        this.code = code;
        this.doOnCommand = lambda;
    }
}