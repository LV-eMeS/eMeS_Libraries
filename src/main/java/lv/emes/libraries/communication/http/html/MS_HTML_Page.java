package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.tools.lists.MS_List;

import static lv.emes.libraries.communication.http.html.MS_HTML_PartTableColumn.EMPTY_COLUMN;

/**
 * HTML page that can be customized. Extending this class there will be possibility to implement such methods as:
 * <ul>
 * <li>leftHeaderOfBody</li>
 * <li>centerHeaderOfBody</li>
 * <li>rightHeaderOfBody</li>
 * <li>leftFooterOfBody</li>
 * <li>centerFooterOfBody</li>
 * <li>rightFooterOfBody</li>
 * <br>
 * <li>configureHeaderRow</li>
 * <li>configureFooterRow</li>
 * <li>initBodyMainRows</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public abstract class MS_HTML_Page extends AbstractHTMLPart {
    private MS_HTML_PartPageHeader fHeaderOfPage;
    private MS_HTML_PartTable fBodyTable;
    private MS_List<MS_HTML_PartTableRow> fBodyRows = new MS_List<>();

    /**
     * Create Web page.
     * Override this in descendant classes calling <code>super()</code> constructor!
     */
    public MS_HTML_Page() {
        fHeaderOfPage = new MS_HTML_PartPageHeader("DEFAULT_WEB_PAGE_TITLE here");
        fBodyTable = new MS_HTML_PartTable().attribute("style", "width:100%");
    }

    /**
     * Set title for page!
     *
     * @param title title of page.
     * @return reference to page itself.
     */
    public MS_HTML_Page title(String title) {
        fHeaderOfPage.title(title);
        return this;
    }

    public MS_HTML_Page charset(String charset) {
        fHeaderOfPage.charset(charset);
        return this;
    }

    /**
     * Create Web page with desired title.
     * Override this in descendant classes calling <code>super()</code> constructor!
     *
     * @param title title of page will be set as property for <b>MS_HTML_PartPageHeader</b>.
     */
    public MS_HTML_Page(String title) {
        this();
        fHeaderOfPage.title(title);
    }

    /**
     * Override to fill content of left header of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn leftHeaderOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Override to fill content of center header of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn centerHeaderOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Override to fill content of right header of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn rightHeaderOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Override to fill content of left footer of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn leftFooterOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Override to fill content of center footer of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn centerFooterOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Override to fill content of right footer of page body!
     * <br><u>Note</u>: object <b>EMPTY_COLUMN</b> is ment to remain unchanged. Do not use it and do not fill it with some content!
     * @return
     */
    protected MS_HTML_PartTableColumn rightFooterOfBody() {
        return EMPTY_COLUMN;
    }

    /**
     * Set header row's attributes
     *
     * @param headerRow header row of page body.
     */
    protected void configureHeaderRow(MS_HTML_PartTableRow headerRow) {
        headerRow.attribute("align", "justify");
    }

    /**
     * Set footer row's attributes
     *
     * @param footerRow footer row of page body.
     */
    protected void configureFooterRow(MS_HTML_PartTableRow footerRow) {
        footerRow.attribute("align", "justify");
    }

    /**
     * Implement this method to use MS_HTML_Page main field to display main features of page.
     * Just create new rows and add to list <b>listOfRows</b>!
     *
     * @param listOfRows rows that will be appended to page structure table.
     */
    protected void initBodyMainRows(MS_List<MS_HTML_PartTableRow> listOfRows) {    }

    @Override
    public final MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        MS_HTML_PartTableColumn leftHeader = leftHeaderOfBody();
        MS_HTML_PartTableColumn centerHeader = centerHeaderOfBody();
        MS_HTML_PartTableColumn rightHeader = rightHeaderOfBody();
        MS_HTML_PartTableRow headerRow = new MS_HTML_PartTableRow();
        configureHeaderRow(headerRow); //row can be configured here
        headerRow.column(leftHeader).column(centerHeader).column(rightHeader);
        fBodyTable.row(headerRow);

        //append body rows to page
        initBodyMainRows(fBodyRows);
        fBodyRows.doWithEveryItem((r, i) -> {
            fBodyTable.row(r);
        });

        MS_HTML_PartTableColumn leftFooter = leftFooterOfBody();
        MS_HTML_PartTableColumn centerFooter = centerFooterOfBody();
        MS_HTML_PartTableColumn rightFooter = rightFooterOfBody();
        MS_HTML_PartTableRow footerRow = new MS_HTML_PartTableRow();
        configureFooterRow(footerRow); //row can be configured here
        footerRow.column(leftFooter).column(centerFooter).column(rightFooter);
        fBodyTable.row(footerRow);

        //Just put all the pieces together and form lines for use in toString method.
        fHeaderOfPage.prepareContent(lb);
        fBodyTable.prepareContent(lb); //forms body table with all assigned rows and columns
        new MS_HTML_PartPageFooter().prepareContent(lb);
        return lb;
    }

    /**
     * Returns page as HTML text. Synonym for toString method.
     *
     * @return full HTML text String of Web page.
     */
    public String html() {
        return toString();
    }
}
