package lv.emes.libraries.utilities;

/**
 * Static class helps to make decision based on two boolean values. It has only one method that can be used in switch block to
 * <p>Static methods:
 * <ul>
 * <li>getCase</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_BooleanDecision {
    public static final int NONE = 0;
    public static final int FIRST = 1;
    public static final int SECOND = 2;
    public static final int BOTH = 3;

    private MS_BooleanDecision() {}

    public static int getCase(boolean first, boolean second) {
        if (first) {
            if (second) return BOTH;
            else return FIRST;
        } else {
            if (second) return SECOND;
            else return NONE;
        }
    }
}
