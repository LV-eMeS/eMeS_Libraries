package lv.emes.libraries.tools.strings;

/**
 * Token that consists of whole string. It's left and right parts are empty strings, and main content is located
 * in the mod middle part.
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
public class MS_ArbitraryStringToken implements MS_StringToken {

    private final String name;
    private final String middlePart;

    public MS_ArbitraryStringToken(String name, String middlePart) {
        this.name = name;
        this.middlePart = middlePart;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getLeftSide() {
        return "";
    }

    @Override
    public String getMiddlePart() {
        return middlePart;
    }

    @Override
    public String getRightSide() {
        return "";
    }
}
