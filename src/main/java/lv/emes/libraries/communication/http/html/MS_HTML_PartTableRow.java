package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.tools.MS_LineBuilder;
import lv.emes.libraries.tools.lists.MS_List;

/**
 * HTML part for table column design.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartTableRow extends MS_HTML_PartOfTag {
    protected MS_List<MS_HTML_PartTableColumn> columns = new MS_List<>();

    public static MS_HTML_PartTableRow newJustifiedRow() {
        return new MS_HTML_PartTableRow().attribute("align", "justify");
    }

    public MS_HTML_PartTableRow() {
        super("tr");
    }

    public MS_HTML_PartTableRow column(MS_HTML_PartTableColumn col) {
        if (col != null)
            columns.add(col);
        return this;
    }

    @Override
    public MS_HTML_PartTableRow attribute(String name, String value) {
        super.attribute(name, value);
        return this;
    }

    /**
     * <br><u>Warning</u>: this method is disabled for this class. Use <b>column</b> method instead!
     *
     * @param tagContent can be anything, pointless anyways..
     * @return reference to table row itself.
     */
    public MS_HTML_PartTableRow content(AbstractHTMLPart tagContent) {
        return this;
    }

    /**
     * <br><u>Warning</u>: this method is disabled for this class. Use <b>column</b> method instead!
     *
     * @param actionToPrepareContent can be anything, pointless anyways..
     * @return reference to table row itself.
     */
    @Override
    public MS_HTML_PartTableRow content(IFuncContentPrepareAction actionToPrepareContent) {
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        //set the content of all the columns
        if (pContent == null)
            pContent = new AbstractHTMLPart() {
                @Override
                public MS_LineBuilder prepareContent(MS_LineBuilder tmp) {
                    columns.forEachItem((col, ind) -> {
                        col.prepareContent(tmp);
                    });
                    return null;
                }
            };
        return super.prepareContent(lb);
    }
}
