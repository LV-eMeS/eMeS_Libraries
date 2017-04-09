package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML part for table column design.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_HTML_PartTableColumn extends MS_HTML_PartOfTag {
    public static MS_HTML_PartTableColumn newEmptyColumn() {
        return new MS_HTML_PartTableColumn();
    }

    public MS_HTML_PartTableColumn() {
        super("td");
    }

    @Override
    public MS_HTML_PartTableColumn attribute(String name, String value) {
        super.attribute(name, value);
        return this;
    }

    @Override
    public MS_HTML_PartTableColumn content(AbstractHTMLPart tagContent) {
        super.content(tagContent);
        return this;
    }

    @Override
    public MS_HTML_PartTableColumn content(String content) {
        super.content(content);
        return this;
    }

    @Override
    public MS_HTML_PartTableColumn content(IFuncContentPrepareAction actionToPrepareContent) {
        super.content(actionToPrepareContent);
        return this;
    }

    @Override
    public MS_HTML_PartTableColumn contentToAppend(IFuncContentPrepareAction actionToPrepareContent) {
        super.contentToAppend(actionToPrepareContent);
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        return super.prepareContent(lb);
    }
}
