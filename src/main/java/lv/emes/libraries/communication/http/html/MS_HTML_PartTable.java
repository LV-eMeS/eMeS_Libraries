package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.tools.lists.MS_List;
import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML part for web page table that consists of rows and columns.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_HTML_PartTable extends MS_HTML_PartOfTag {
    protected MS_List<MS_HTML_PartTableRow> rows = new MS_List<>();

    public MS_HTML_PartTable() {
        super("table");
    }

    public MS_HTML_PartTable row(MS_HTML_PartTableRow row) {
        if (row != null)
            rows.add(row);
        return this;
    }

    @Override
    public MS_HTML_PartTable attribute(String name, String value) {
        super.attribute(name, value);
        return this;
    }

    /**
     * <br><u>Warning</u>: this method is disabled for this class. Use <b>column</b> method instead!
     *
     * @param tagContent can be anything, pointless anyways..
     * @return reference to table row itself.
     */
    public MS_HTML_PartTable content(AbstractHTMLPart tagContent) {
        return this;
    }

    /**
     * <br><u>Warning</u>: this method is disabled for this class. Use <b>column</b> method instead!
     *
     * @param actionToPrepareContent can be anything, pointless anyways..
     * @return reference to table row itself.
     */
    @Override
    public MS_HTML_PartTable content(FuncContentPrepareAction actionToPrepareContent) {
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        //set the content of all the rows
        if (pContent == null)
            pContent = new AbstractHTMLPart() {
                @Override
                public MS_LineBuilder prepareContent(MS_LineBuilder tmp) {
                    rows.forEachItem((row, ind) -> {
                        row.prepareContent(tmp);
                    });
                    return null;
                }
            };
        return super.prepareContent(lb);
    }
}