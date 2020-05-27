package lv.emes.libraries.tools.decision;

/**
 * Algorithm that decides whether something needs to be done or applied.
 * Each time one of decision methods (<tt>test</tt>, <tt>needToDo</tt>, <tt>needToApply</tt>, <tt>verify</tt> or <tt>evaluate</tt>)
 * is called, an algorithm is triggered that evaluates if the state is correct for decision making.
 * <p>Public methods:
 * <ul>
 *     <li>test</li>
 *     <li>needToDo</li>
 *     <li>needToApply</li>
 *     <li>verify</li>
 *     <li>evaluate</li>
 *     <li>getCurrentDecision - does not perform next state decision, only returns current one</li>
 *     <li>reset</li>
 *     <li>getCurrentState</li>
 * </ul>
 *
 * @param <T> type of object that holds state, on which decision depends.
 * @author eMeS
 * @version 1.0.
 * @since 2.3.3.
 */
public interface MS_ConditionalDecision<T> {

    /**
     * Perform evaluation of decision and return affirmative or negative boolean value based on the state of decision.
     *
     * @return true if decision is affirmative, false otherwise.
     */
    boolean test();

    default boolean needToDo() {
        return test();
    }

    default boolean needToApply() {
        return test();
    }

    default boolean verify() {
        return test();
    }

    default boolean evaluate() {
        return test();
    }

    /**
     * @return current decision without evaluating.
     * Returns <tt>false</tt> if called first time without evaluation.
     */
    boolean getCurrentDecision();

    /**
     * Resets the state.
     */
    void reset();

    /**
     * @return current state value.
     */
    T getCurrentState();
}
