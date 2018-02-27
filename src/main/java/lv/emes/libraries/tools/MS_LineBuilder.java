package lv.emes.libraries.tools;

import lv.emes.libraries.utilities.MS_StringUtils;

/**
 * String builder that is designed to append lines or composite texts.
 * <p>
 * <p>Methods:
 * <ul>
 * <li>add</li>
 * <li>append</li>
 * <li>getStringBuilder</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.1.
 */
public class MS_LineBuilder {

    private StringBuilder sb;

    public MS_LineBuilder() {
        sb = new StringBuilder();
    }

    /**
     * @return actual {@link StringBuilder}, which is used under the hood of this line builder.
     */
    public StringBuilder getStringBuilder() {
        return sb;
    }

    /**
     * Adds <b>str</b> to new line and adds "Newline symbol" after end of line.
     *
     * @param str text to be added.
     * @return reference to builder itself.
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
     * @param compositeText a composite text which will be added to this line builder.
     * @return reference to builder itself.
     */
    public MS_LineBuilder add(MS_AbstractCompositeText compositeText) {
        if (compositeText != null)
            compositeText.prepareContent(this);
        return this;
    }

    /**
     * Adds <b>str</b> to new line <u>without</u> adding "Newline symbol" after end of line.
     *
     * @param str text to be added.
     * @return reference to builder itself.
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
