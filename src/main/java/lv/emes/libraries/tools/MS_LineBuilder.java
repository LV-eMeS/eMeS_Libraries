package lv.emes.libraries.tools;

import lv.emes.libraries.utilities.MS_StringUtils;

/**
 * String builder that is designed only to append lines.
 *
 * <p>Methods:
 * <ul>
 * <li>add</li>
 * <li>append</li>
 * <li>getStringBuilder</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_LineBuilder {

    private StringBuilder sb;

    public MS_LineBuilder() {
        sb = new StringBuilder();
    }

    public StringBuilder getStringBuilder() {
        return sb;
    }

    /**
     * Adds <b>str</b> to new line and adds "Newline symbol" after end of line.
     *
     * @param str text to be added.
     * @return reference to object itself.
     */
    public MS_LineBuilder add(String str) {
        if (!str.isEmpty())
            sb.append(str);
        sb.append(MS_StringUtils._LINE_BRAKE);
        return this;
    }

    /**
     * Adds <b>str</b> to new line and adds "Newline symbol" after end of line.
     *
     * @param htmlPart a whole HTML part implementation to be added.
     * @return reference to object itself.
     */
    public MS_LineBuilder add(MS_AbstractCompositeText htmlPart) {
        htmlPart.prepareContent(this);
        return this;
    }

    /**
     * Adds <b>str</b> to new line <u>without</u> adding "Newline symbol" after end of line.
     *
     * @param str text to be added.
     * @return reference to object itself.
     */
    public MS_LineBuilder append(String str) {
        if (!str.isEmpty())
            sb.append(str);
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
