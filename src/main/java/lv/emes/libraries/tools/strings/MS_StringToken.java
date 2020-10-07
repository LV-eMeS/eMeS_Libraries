package lv.emes.libraries.tools.strings;

/**
 * Some token-like part of string that can occur in string multiple times and serves some specific purpose.
 * <p>Public methods:
 * <ul>
 *     <li>getName</li>
 *     <li>getLeftSide</li>
 *     <li>getMiddlePart</li>
 *     <li>getRightSide</li>
 *     <li>getWholeToken</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public interface MS_StringToken {

    String getName();
    String getLeftSide();
    String getMiddlePart();
    String getRightSide();

    default String getWholeToken() {
        return getLeftSide() + getMiddlePart() + getRightSide();
    }
}
