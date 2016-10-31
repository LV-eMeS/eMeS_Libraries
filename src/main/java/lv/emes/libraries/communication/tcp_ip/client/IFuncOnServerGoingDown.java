package lv.emes.libraries.communication.tcp_ip.client;

/**
 * This functional interface is for client event handling purposes. 
 * Set it to define behavior of after server going down (disconnecting).
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnServerGoingDown {
	void doOnEvent();
}