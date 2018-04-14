package lv.emes.libraries.tools.flow;

import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_RuntimeExecutionFailureException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Event flow mechanism, which allows to execute many actions in one chain with predefined action execution order in
 * order to get some output from given input.
 * <p>Public methods:
 * <ul>
 * <li>execute</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>withEvent</li>
 * <li>getOutput</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.1.5
 */
public class MS_Flow {

    private List<IFuncEventFlowAction> actions = new ArrayList<>();
    private Object output = null;

    /**
     * Adds new event with given action at the end of action list.
     *
     * @param action action to add to list.
     * @return reference to flow itself to chain methods together.
     */
    public MS_Flow withEvent(IFuncEventFlowAction action) {
        actions.add(action);
        return this;
    }

    /**
     * Executes all actions in chain and puts produced output value into <b>output</b> object, which can be accessed by
     * getter {@link MS_Flow#getOutput()}.
     * <p>If no event actions are added to the action list, resulting output becomes <b>input</b>.
     * <p>If only one event action is added to the action list, resulting output becomes an output of action, but
     * execution of the same event can continue if action requests to execute same action once again, by changing
     * value of <b>nextEventIndex</b> to 0 on some conditions.
     * <p>If more than one event action is added to the action list, resulting output becomes an output of last
     * executed action in action chain.
     * <p>In case of unhandled runtime exceptions, while performing execution,
     * this method throws {@link RuntimeException} right away.
     *
     * @param input input for first action that will be triggered in action chain on this method call.
     * @return reference to flow itself to chain methods together.
     * @throws MS_RuntimeExecutionFailureException any unhandled exception arisen on execution time.
     *                                             It's recommended to not to chain {@link MS_Flow#getOutput()} in same method
     *                                             chain, but call it separately instead, after this exception
     *                                             is handled (if handling necessary because not done inside event flow).
     */
    public MS_Flow execute(Object input) throws MS_RuntimeExecutionFailureException {
        if (actions.size() == 0) {
            this.output = input;
            return this;
        } else {
            Object resultingOutput = input;
            Integer prevEventIndex = null;
            Integer currentEventIndex = 0;
            AtomicReference<Integer> nextEventIndex = new AtomicReference<>(1);

            while (currentEventIndex != null &&
                    MS_CodingUtils.inRange(currentEventIndex, 0, actions.size() - 1)) {

                Integer nextIndexBeforeExec = nextEventIndex.get();
                try {
                    resultingOutput = actions.get(currentEventIndex)
                            .execute(resultingOutput, prevEventIndex, nextEventIndex);
                } catch (RuntimeException e) {
                    throw new MS_RuntimeExecutionFailureException(String.format(
                            "Event at index [%d] triggered by event at index [%d] failed to execute with input:\n%s",
                            currentEventIndex, prevEventIndex, resultingOutput
                    ), e);
                }
                prevEventIndex = currentEventIndex;
                //if pointer to next event index changed in execution time, lets set changed value,
                //otherwise go to next event
                if (Objects.equals(nextIndexBeforeExec, nextEventIndex.get())) {
                    currentEventIndex++;
                    if (nextIndexBeforeExec != null) {
                        if (++nextIndexBeforeExec < actions.size()) {
                            nextEventIndex.set(nextIndexBeforeExec);
                        } else {
                            nextEventIndex.set(null);
                        }
                    }
                } else {
                    currentEventIndex = nextEventIndex.get();
                    if (nextEventIndex.get() != null) nextEventIndex.set(nextEventIndex.get() + 1);
                }
            }

            this.output = resultingOutput;
            return this;
        }
    }

    public Object getOutput() {
        return output;
    }
}
