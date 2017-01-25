package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_AbstractCompositeText;
import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * HTML part for web page.
 *
 * @author eMeS
 * @version 1.2.
 */
public abstract class AbstractHTMLPart extends MS_AbstractCompositeText {
    protected abstract MS_LineBuilder prepareContent(MS_LineBuilder lb);
}
