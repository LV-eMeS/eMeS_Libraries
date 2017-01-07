package lv.emes.libraries.communication.http.html;

/**
 * HTML part for web page header.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartInternalURL extends AbstractHTMLPart {
    protected String fURL = "";
    private String fText = "";
    private String fTargetWindow = "";
    private String fPrefix = "";
    private String fPostfix = "";

    public MS_HTML_PartInternalURL url(String newURL) {
        fURL = newURL;
        return this;
    }

    public MS_HTML_PartInternalURL text(String newText) {
        fText = newText;
        return this;
    }

    public MS_HTML_PartInternalURL openInCurrentTab() {
        fTargetWindow = "";
        return this;
    }

    public MS_HTML_PartInternalURL openInNewTab() {
        fTargetWindow = "target='_blank' ";
        return this;
    }

    /**
     * A text that will be printed before link.
     *
     * @param prefix any string.
     * @return reference to object itself.
     */
    public MS_HTML_PartInternalURL prefix(String prefix) {
        fPrefix = prefix;
        return this;
    }

    /**
     * A text that will be printed after link.
     *
     * @param postfix any string.
     * @return reference to object itself.
     */
    public MS_HTML_PartInternalURL postfix(String postfix) {
        fPostfix = postfix;
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        if (!fPrefix.equals("")) {
            lb.append(fPrefix);
        }
        String link = "<a " + fTargetWindow + "href=\"" + fURL + "\">" + fText + "</a>";
        if (fPostfix.equals("")) {
            lb.add(link);
        } else {
            lb.append(link);
            lb.add(fPostfix);
        }
        return lb;
    }
}
