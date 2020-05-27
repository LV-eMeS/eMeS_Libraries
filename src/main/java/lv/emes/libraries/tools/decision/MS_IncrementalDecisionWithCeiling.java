package lv.emes.libraries.tools.decision;

/**
 * Decision that is affirmative only in the beginning.
 * Ceiling value passed in the constructor controls, when decision is becoming negative.
 * E.g. If <b>ceilingValue == 3</b> then first 3 times decision will evaluate to <tt>true</tt>, bet rest of the times
 * it will evaluate to <tt>false</tt> unless reset is performed.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.3.
 */
public class MS_IncrementalDecisionWithCeiling extends MS_AbstractConditionalDecision<Long> {

    private final Long ceilingValue;

    public MS_IncrementalDecisionWithCeiling(Long ceilingValue) {
        this.ceilingValue = ceilingValue;
    }

    @Override
    protected Long initiateState() {
        return 0L;
    }

    @Override
    protected Long evaluateNextValue(Long current) {
        return ++current;
    }

    @Override
    public boolean getCurrentDecision() {
        return getCurrentState() <= ceilingValue;
    }
}
