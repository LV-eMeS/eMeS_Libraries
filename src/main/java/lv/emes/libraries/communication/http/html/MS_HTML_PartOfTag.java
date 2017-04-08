package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML tag with attributes and content.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_HTML_PartOfTag extends AbstractHTMLPart {
    private MS_StringList fAttributes = new MS_StringList();
    AbstractHTMLPart pContent = null;
    private MS_List<FuncContentPrepareAction> afterContentPrepared;
    boolean fNoEndTag = false;

    public MS_HTML_PartOfTag(String tagName) {
        fAttributes.add(tagName); //first attribute always will be tag name
        fAttributes.delimiter = ' ';
    }

    /**
     * Adds new attribute for tag (no checking for duplicates).
     * @param name name of attribute.
     * @param value value of attribute.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag attribute(String name, String value) {
        fAttributes.add(name.concat("=\"").concat(value).concat("\""));
        return this;
    }

    /**
     * Puts HTML part inside the tag.
     * @param tagContent a some HTML part that can be text or some more complex HTML structure.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag content(AbstractHTMLPart tagContent) {
        pContent = tagContent;
        return this;
    }

    /**
     * Puts HTML part which is text inside the tag.
     * @param tagContentAsString a plain text.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag content(String tagContentAsString) {
        pContent = new MS_HTML_PartPlainText(tagContentAsString);
        return this;
    }

    /**
     * Puts HTML part inside the tag.
     * @param actionToPrepareContent an action that will be performed when HTML string will be made for this kind of object.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag content(FuncContentPrepareAction actionToPrepareContent) {
        pContent = new AbstractHTMLPart() {
            @Override
            protected MS_LineBuilder prepareContent(MS_LineBuilder lb) {
                actionToPrepareContent.executeAction(lb);
                return lb;
            }
        };
        return this;
    }

    /**
     * Puts HTML part inside the tag.
     * If something is already inside the tag then that part is appended by this additional content <b>actionToPrepareContent</b>.
     * @param actionToPrepareContent an action that will be performed when HTML string will be made for this kind of object.
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag contentToAppend(FuncContentPrepareAction actionToPrepareContent) {
        if (pContent == null) //if content of this tag is currently empty then simply save the new content
            return this.content(actionToPrepareContent);
        else {
            if (afterContentPrepared == null)
                afterContentPrepared = new MS_List<>();
            if (actionToPrepareContent != null)
                afterContentPrepared.add(actionToPrepareContent);
            return this;
        }
    }

    /**
     * Mark that this particular tag ends without content part and without ending tag.
     * <br>This means that this tag looks like: <code>&lt;<i>tagName</i> <i>attributes</i>... /&gt;</code>
     * @return reference to HTML tag object itself.
     */
    public MS_HTML_PartOfTag noEndTag() {
        fNoEndTag = true;
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        if (! fNoEndTag) {
            lb.add("<"+fAttributes.toText()+">");
            doPrepareContent(lb);
            lb.add("</" + fAttributes.get(0) + ">");
        } else {
            lb.add("<"+fAttributes.toText()+"/>");
            doPrepareContent(lb);
        }
        return lb;
    }

    private void doPrepareContent(MS_LineBuilder lb) {
        if (pContent != null)
            pContent.prepareContent(lb);

        if (afterContentPrepared != null)
            afterContentPrepared.forEachItem((action, i) -> {
                action.executeAction(lb);
            });
    }
}