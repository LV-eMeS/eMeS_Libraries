package lv.emes.libraries.tools.threading;

import java.time.ZonedDateTime;

/**
 * This functional interface is for any kind of error in (scheduled event) handling purposes.
 * Set it to define what happens if some error occurs.
 * @author eMeS
 */
@FunctionalInterface
public interface IFuncOnSomeSchedulerException {
    void doOnError(Exception exception, ZonedDateTime eventExecutionTime);
}
