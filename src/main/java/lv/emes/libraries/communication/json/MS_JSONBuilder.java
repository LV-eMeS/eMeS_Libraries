package lv.emes.libraries.communication.json;

import lv.emes.libraries.tools.MS_Builder;

/**
 * Typical JSONObject builder.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3
 */
public class MS_JSONBuilder extends MS_Builder<MS_JSONObject> {

    @Override
    protected final MS_JSONObject newTemplateObject() {
        return new MS_JSONObject();
    }
}
