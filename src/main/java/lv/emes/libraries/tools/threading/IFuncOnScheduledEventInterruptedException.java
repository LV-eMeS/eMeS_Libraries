package lv.emes.libraries.tools.threading;

import java.time.ZonedDateTime;

/**
 * This functional interface is meant for scheduled event interruption error handling purposes.
 * Set it to define what happens if you run scheduler, but then some event is interrupted before it finished execution.
 * @author eMeS
 */
public interface IFuncOnScheduledEventInterruptedException {
    void doOnError(ZonedDateTime eventExecutionTime);
}
