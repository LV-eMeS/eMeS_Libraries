package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML part for web page header.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_HTML_PartInternalURL extends MS_HTML_PartOfTag {
    private String fTargetWindow = "";
    private String fPrefix = "";
    private String fPostfix = "";

    public MS_HTML_PartInternalURL() {
        super("a");
    }

    public MS_HTML_PartInternalURL url(String newURL) {
        return attribute("href", newURL);
    }

    @Override
    public MS_HTML_PartInternalURL content(AbstractHTMLPart tagContent) {
        super.content(tagContent);
        return this;
    }

    @Override
    public MS_HTML_PartInternalURL content(String tagContentAsString) {
        super.content(tagContentAsString);
        return this;
    }

    @Override
    public MS_HTML_PartInternalURL content(FuncContentPrepareAction actionToPrepareContent) {
        super.content(actionToPrepareContent);
        return this;
    }

    public MS_HTML_PartInternalURL text(String newText) {
        super.content(newText);
        return this;
    }

    public MS_HTML_PartInternalURL openInNewTab() {
        return attribute("target", "_blank");
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
    public MS_HTML_PartInternalURL attribute(String name, String value) {
        super.attribute(name, value);
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        if (!fPrefix.equals("")) {
            lb.add(fPrefix);
        }
        lb = super.prepareContent(lb);
        if (! fPostfix.equals(""))
            lb.add(fPostfix);
        return lb;
    }
}
