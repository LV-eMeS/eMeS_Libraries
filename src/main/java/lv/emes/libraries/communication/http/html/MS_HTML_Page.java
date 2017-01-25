package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML page that can be customized. Extending this class there will be possibility to implement such methods as:
 * <ul>
 * <li>leftHeaderOfBody</li>
 * <li>centerHeaderOfBody</li>
 * <li>rightHeaderOfBody</li>
 * <li>leftFooterOfBody</li>
 * <li>centerFooterOfBody</li>
 * <li>rightFooterOfBody</li>
 * </ul>
 * <ul>
 * <li>configureHeaderRow</li>
 * <li>configureFooterRow</li>
 * <li>initBodyMainRows</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public abstract class MS_HTML_Page extends AbstractHTMLPart {
    private MS_HTML_PartPageHeader fHeaderOfPage;
    private MS_HTML_PartTable fBodyTable;

    /**
     * Create Web page.
     * Override this in descendant classes calling <code>super()</code> constructor!
     */
    public MS_HTML_Page() {
        fHeaderOfPage = new MS_HTML_PartPageHeader();
        fBodyTable = newTable();
    }

    /**
     * @return table with fixed layout formatting.
     */
    protected final MS_HTML_PartTable newTable() {
        return new MS_HTML_PartTable().attribute("style", "table-layout:fixed;width:100%");
    }

    protected final MS_HTML_PartTableColumn newBodyMainRow() {
        MS_HTML_PartTableColumn res = new MS_HTML_PartTableColumn();
        fBodyTable.row(new MS_HTML_PartTableRow().column(res));
        res.attribute("colspan", "3"); //this only column will fill entire page width
        return res;
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
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn leftHeader() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Override to fill content of center header of page body!
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn centerHeader() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Override to fill content of right header of page body!
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn rightHeader() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Override to fill content of left footer of page body!
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn leftFooter() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Override to fill content of center footer of page body!
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn centerFooter() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Override to fill content of right footer of page body!
     * @return new column with filled content.
     */
    protected MS_HTML_PartTableColumn rightFooter() {
        return MS_HTML_PartTableColumn.newEmptyColumn();
    }

    /**
     * Set header row's attributes
     *
     * @param headerRow header row of page body.
     */
    protected void configureHeaderRow(MS_HTML_PartTableRow headerRow) {
//        headerRow.attribute("align", "justify");
    }

    /**
     * Set footer row's attributes
     *
     * @param footerRow footer row of page body.
     */
    protected void configureFooterRow(MS_HTML_PartTableRow footerRow) {
        //footerRow.attribute("align", "justify");
    }

    /**
     * Implement this method to use MS_HTML_Page main field to display main features of page.
     * Just create new rows by calling method <b>newBodyMainRow</b> and fill the contents of column returned by it's result!
     */
    protected void initBodyMainRows() {    }

    @Override
    public final MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        MS_HTML_PartTableColumn leftHeaderCol = leftHeader().attribute("style", "width: 34%;");
        MS_HTML_PartTableColumn centerHeader = centerHeader().attribute("style", "width: 33%;");
        MS_HTML_PartTableColumn rightHeader = rightHeader().attribute("style", "width: 33%;");
        MS_HTML_PartTableRow headerRow = new MS_HTML_PartTableRow();
        headerRow.column(leftHeaderCol).column(centerHeader).column(rightHeader);
        configureHeaderRow(headerRow); //row can be configured here
        fBodyTable.row(headerRow);

        //append body rows to page
        initBodyMainRows();

        MS_HTML_PartTableColumn leftFooterCol = leftFooter();
        MS_HTML_PartTableColumn centerFooter = centerFooter();
        MS_HTML_PartTableColumn rightFooter = rightFooter();
        MS_HTML_PartTableRow footerRow = new MS_HTML_PartTableRow();
        footerRow.column(leftFooterCol).column(centerFooter).column(rightFooter);
        configureFooterRow(footerRow); //row can be configured here
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
