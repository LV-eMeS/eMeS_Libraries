package lv.emes.libraries.tools;

/**
 * Any text that can be divided into portions which are mutable.
 * This class can server as a utility to form different composite texts like SQL queries or HTML elements or even pages.
 * <p>Methods to override:
 * <ul>
 * <li>prepareContent</li>
 * </ul>
 * <p>Public methods:
 * <ul>
 * <li>resetContent</li>
 * <li>toString</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public abstract class MS_AbstractCompositeText {

    private MS_LineBuilder fBuilder;
    private boolean contentReady = false;

    /**
     * Prepare content of HTML page that will be returned.
     * This method fills string Builder <b>lb</b> with HTML text that will be returned using toString method.
     * Descendants of this abstract class do not need to implement toString method, because it is already implemented here
     * using String builder.
     *
     * @param lb String builder to contain HTML text.
     * @return must return the same passed <b>lb</b> for better looking code and reuse this line builder in optimization.
     */
    protected abstract MS_LineBuilder prepareContent(MS_LineBuilder lb);

    /**
     * As content of HTML part is designed to form only once per object this method is the only way to make exception to this approach.
     * <br>By using this flag that marks that content is ready is set to false again.
     *
     * @return reference to HTML part itself.
     */
    public MS_AbstractCompositeText resetContent() {
        contentReady = false;
        return this;
    }

    @Override
    public String toString() {
        if (!contentReady) {
            fBuilder = new MS_LineBuilder();
            prepareContent(fBuilder);
            contentReady = true;
        }
        return fBuilder.toString();
    }
}
