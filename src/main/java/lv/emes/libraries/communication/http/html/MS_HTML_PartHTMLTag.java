package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.tools.lists.MS_StringList;

/**
 * HTML tag with attributes and content.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartHTMLTag extends AbstractHTMLPart {
    private MS_StringList fAttributes = new MS_StringList();
    protected AbstractHTMLPart pTagContent = null;

    public MS_HTML_PartHTMLTag(String tagName) {
        fAttributes.add(tagName); //first attribute always will be tag name
        fAttributes.delimiter = ' ';
    }

    /**
     * Adds new attribute for tag (no checking for duplicates).
     * @param name name of attribute.
     * @param value value of attribute.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartHTMLTag attribute(String name, String value) {
        fAttributes.add(name.concat("=\"").concat(value).concat("\""));
        return this;
    }

    /**
     * Puts HTML part inside the tag.
     * @param tagContent a some HTML part that can be text or some more complex HTML structure.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartHTMLTag content(AbstractHTMLPart tagContent) {
        pTagContent = tagContent;
        return this;
    }

    /**
     * Puts HTML part inside the tag.
     * @param actionToPrepareContent an action that will be performed when HTML string will be made for this kind of object.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartHTMLTag content(FuncContentPrepareAction actionToPrepareContent) {
        pTagContent = new AbstractHTMLPart() {
            @Override
            protected MS_LineBuilder prepareContent(MS_LineBuilder lb) {
                actionToPrepareContent.executeAction(lb);
                return lb;
            }
        };
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        lb.add("<"+fAttributes.toText()+">");
        if (pTagContent != null)
            pTagContent.prepareContent(lb);
        lb.add("</" + fAttributes.get(0) + ">");
        return lb;
    }
}
