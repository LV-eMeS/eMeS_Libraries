package lv.emes.libraries.tools.threading;

import java.time.ZonedDateTime;

/**
 * This functional interface is meant for some event execution.
 * In execution time there might occur some error, but its catching and handling should be left
 * to method from which this event is called.
 * Set it to define behavior of some event.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncScheduledEvent {
    void execute(ZonedDateTime eventExecutionTime) throws Exception;
}
