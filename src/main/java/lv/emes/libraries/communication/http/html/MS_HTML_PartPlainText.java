package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML part for storing nothing else but text.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_HTML_PartPlainText extends AbstractHTMLPart {
    private String fText = "";

    public MS_HTML_PartPlainText(String value) {
        fText = value;
    }

    @Override
    public MS_LineBuilder prepareContent(MS_LineBuilder lb) {
        lb.add(fText);
        return lb;
    }
}
