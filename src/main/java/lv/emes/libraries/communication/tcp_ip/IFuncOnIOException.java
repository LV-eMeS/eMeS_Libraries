package lv.emes.libraries.communication.tcp_ip;

import java.io.IOException;

/**
 * This functional interface is for server exception handling purposes.
 * Set it to define behavior of after client unsuccessfully reads message from server.
 *
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnIOException {

    void doOnEvent(IOException exception);
}