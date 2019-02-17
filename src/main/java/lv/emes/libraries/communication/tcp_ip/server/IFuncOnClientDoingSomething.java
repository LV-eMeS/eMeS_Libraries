package lv.emes.libraries.communication.tcp_ip.server;

/**
 * This functional interface is for server event handling purposes.
 * Set it to define behavior of server after client connecting or going offline (disconnecting).
 *
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnClientDoingSomething {
    void doOnEvent(MS_ClientOfServer client);
}