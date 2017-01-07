package lv.emes.libraries.communication.http.html;

/**
 * HTML part for table column design.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartTableColumn extends MS_HTML_PartHTMLTag {
    public static final MS_HTML_PartTableColumn EMPTY_COLUMN = new MS_HTML_PartTableColumn();

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
    public MS_HTML_PartTableColumn content(FuncContentPrepareAction actionToPrepareContent) {
        super.content(actionToPrepareContent);
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        return super.prepareContent(lb);
    }
}
