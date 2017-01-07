package lv.emes.libraries.communication.http.html;

/**
 * HTML part for web page header.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartPageHeader extends AbstractHTMLPart {
    public static final String PATH_TO_CSS_FILE = "PATH_TO_CSS_FILE";
    public static final String PATH_TO_PAGE_TITLE_ICON = "PATH_TO_PAGE_TITLE_ICON";
    public static final String CHARSET_UTF_8 = "utf-8";
    public static final String CHARSET_LATVIAN = "windows-1257";

    private String fTitle = "";
    private String fCharset = CHARSET_UTF_8;

    public MS_HTML_PartPageHeader() {}
    public MS_HTML_PartPageHeader(String title) {
        this.fTitle = title;
    }

    public MS_HTML_PartPageHeader title(String newTitle) {
        fTitle = newTitle;
        return this;
    }

    public MS_HTML_PartPageHeader charset(String charset) {
        fCharset = charset;
        return this;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        lb.add("<!DOCTYPE html>");
        lb.add("<html>");
        lb.add("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
        lb.add("<link rel=\"stylesheet\" href=\"http://www.w3schools.com/lib/w3.css\">");
        lb.add("<meta http-equiv=\"Content-Type\" content=\"text/html; charset="+fCharset+"\">");
        lb.add("<link href=\"" + PATH_TO_CSS_FILE + "\" rel=\"stylesheet\" type=\"text/css\">");
        lb.add("<link rel=\"icon\" href=\""+ PATH_TO_PAGE_TITLE_ICON +"\" type=\"image/x-icon\">");
        lb.add("<title>" + fTitle + "</title>");
        lb.add("<body>");
        return lb;
    }
}
