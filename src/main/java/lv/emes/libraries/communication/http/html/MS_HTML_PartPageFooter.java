package lv.emes.libraries.communication.http.html;

/**
 * HTML part for web page footer.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartPageFooter extends AbstractHTMLPart {
    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        lb.add("</body>");
        lb.add("</html>");
        return lb;
    }
}
