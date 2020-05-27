package lv.emes.libraries.tools.decision;

/**
 * @author eMeS
 * @version 1.0.
 * @since 2.3.3.
 */
public abstract class MS_AbstractConditionalDecision<T> implements MS_ConditionalDecision<T> {

    private T state = initiateState();

    protected abstract T initiateState();

    protected abstract T evaluateNextValue(T current);

    @Override
    public boolean test() {
        state = evaluateNextValue(state);
        return getCurrentDecision();
    }

    @Override
    public T getCurrentState() {
        return state;
    }

    @Override
    public void reset() {
        state = initiateState();
    }
}
