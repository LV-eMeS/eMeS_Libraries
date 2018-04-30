package lv.emes.libraries.tools;

/**
 * Boolean flag that saves previous state and can be used to set flag's value just for once
 * and on first {@link MS_BooleanFlag#get()} call returns value to previous one.
 * <p>Public methods:
 * <ul>
 * <li>get</li>
 * <li>setForOnce</li>
 * <li>getUntouched</li>
 * </ul>
 * <p>Properties:
 * <ul>
 * <li>value</li>
 * <li>previousValue</li>
 * </ul>
 * <p>Getters and setters:
 * <ul>
 * <li>get</li>
 * <li>set</li>
 * <li>setTrue</li>
 * <li>setFalse</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.1.6
 */
public class MS_BooleanFlag {

    private boolean value;
    private boolean previousValue;

    public MS_BooleanFlag(boolean initialValue) {
        this.set(initialValue);
    }

    /**
     * Returns flag's value and afterwards sets value back to previous one if it was different.
     *
     * @return flag's value - true or false.
     */
    public boolean get() {
        boolean res = value;
        if (value != previousValue) value = previousValue;
        return res;
    }

    /**
     * Returns flag's value without checks and touching previous value.
     *
     * @return flag's value - true or false.
     */
    public boolean getUntouched() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
        this.previousValue = value;
    }

    /**
     * Sets flag's value just once, which means that after calling {@link MS_BooleanFlag#get()} for second time,
     * value might differ, as it's intended so.
     * <p>Although calling {@link MS_BooleanFlag#getUntouched()} doesn't  affect returned value,
     * as it won't do checks against previous set value, so it can be used to get raw value at any time.
     *
     * @param value desired boolean value to set to this flag.
     */
    public void setForOnce(boolean value) {
        this.value = value;
    }

    public void setTrue() {
        this.set(true);
    }

    public void setFalse() {
        this.set(false);
    }
}
