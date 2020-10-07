package lv.emes.libraries.tools.strings;

/**
 * Token of a template: <tt>"name" + "openingBracket" + "variable" + "closingBracket"</tt>, where <b>variable</b> is
 * arbitrary string and can be retrieved by calling {@link MS_VariableStringToken#getMiddlePart()}.
 * <p>Public methods:
 * <ul>
 *     <li>getName</li>
 *     <li>getLeftSide</li>
 *     <li>getMiddlePart</li>
 *     <li>getRightSide</li>
 *     <li>getWholeToken</li>
 * </ul>
 * <p>Getters and setters:
 * <ul>
 *     <li>setMiddlePart</li>
 *     <li>withValue</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.0.
 */
public class MS_VariableStringToken implements MS_StringToken {

    private final char openingBracket;
    private final char closingBracket;
    private final String name;
    private String middlePart;

    public MS_VariableStringToken(char openingBracket, char closingBracket, String name) {
        this.openingBracket = openingBracket;
        this.closingBracket = closingBracket;
        this.name = name;
    }

    public MS_VariableStringToken(String name) {
        this('(', ')', name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String getLeftSide() {
        return getName() + openingBracket;
    }

    @Override
    public String getMiddlePart() {
        return middlePart;
    }

    @Override
    public String getRightSide() {
        return String.valueOf(closingBracket);
    }

    public void setMiddlePart(String middlePart) {
        this.middlePart = middlePart;
    }

    public MS_VariableStringToken withValue(String middlePart) {
        this.middlePart = middlePart;
        return this;
    }
}
