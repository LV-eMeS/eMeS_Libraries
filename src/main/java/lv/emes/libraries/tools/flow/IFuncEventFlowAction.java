package lv.emes.libraries.tools.flow;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Action to do in {@link MS_Flow}.
 * Action produces output from given input. It also accepts previous and next event index, which starts with <b>0</b>
 * and both might be <code>null</code>. When this is first action in chain previous event index is <code>null</code>.
 * When this is last action in chain next event index is <code>null</code>.
 * <p>Previous event index is just informative value for action logic to know, which event triggered execution of
 * this action.
 * <p>Next event index points to event that will be executed next, but this value can be changed in event execution
 * time to skip some event execution on certain conditions or to return back to some event in chain.
 * <p>In any condition return value of current action must compatible with input of next action, because this
 * value will be passed to next action by {@link MS_Flow#execute(Object)} algorithms.
 * If this action was last in chain, result of action will be retrievable by {@link MS_Flow#getOutput()}.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.1.5
 */
@FunctionalInterface
public interface IFuncEventFlowAction {

    Object execute(Object input, Integer prevEventIndex, AtomicReference<Integer> nextEventIndex) throws RuntimeException;
}
