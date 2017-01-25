package lv.emes.libraries.communication.http.html;

import lv.emes.libraries.utilities.MS_LineBuilder;

/**
 * Use this to form HTML code for particular object.
 *
 * @author eMeS
 * @version 1.0.
 */
@FunctionalInterface
public interface FuncContentPrepareAction {
    void executeAction(MS_LineBuilder lb);
}
