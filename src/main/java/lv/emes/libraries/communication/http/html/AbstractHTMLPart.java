package lv.emes.libraries.communication.http.html;

/**
 * HTML part for web page.
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class AbstractHTMLPart {
    private MS_LineBuilder fBuilder;
    private boolean contentReady = false;

    /**
     * Prepare content of HTML page that will be returned.
     * This method fills string Builder <b>lb</b> with HTML text that will be returned using toString method.
     * Descendants of this abstract class do not need to implement toString method, because it is already implemented here
     * using String builder.
     * @param lb String builder to contain HTML text.
     * @return must return the same passed <b>lb</b> for better looking code and reuse this line builder in optimization.
     */
    protected abstract MS_LineBuilder prepareContent(MS_LineBuilder lb);

    @Override
    public String toString() {
        if (! contentReady) {
            fBuilder = new MS_LineBuilder();
            prepareContent(fBuilder);
            contentReady = true;
        }
        return fBuilder.toString();
    }
}
