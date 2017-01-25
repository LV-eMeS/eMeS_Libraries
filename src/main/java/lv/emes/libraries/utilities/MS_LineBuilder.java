package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_StringTools;

/**
 * String builder that is designed only to append lines.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_LineBuilder {
    public StringBuilder getStringBuilder() {
        return sb;
    }

    private StringBuilder sb;

    public MS_LineBuilder() {
        sb = new StringBuilder();
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
        sb.append(MS_StringTools.C_LINE_BRAKE);
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
