package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.communication.json.MS_ReadOnlyJSONObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * Immutable JSON object structure that holds content of some Yaml properties file.
 *
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.0.
 */
public class MS_YamlContentJSONObject extends MS_ReadOnlyJSONObject {

    public MS_YamlContentJSONObject(JSONObject obj) {
        super(obj);
    }

    public MS_YamlContentJSONObject(Map<?, ?> map) {
        super(map);
    }

    public MS_YamlContentJSONObject(JSONObject jo, String... names) {
        super(jo, names);
    }

    public MS_YamlContentJSONObject(JSONTokener x) throws JSONException {
        super(x);
    }

    public MS_YamlContentJSONObject(Object bean) {
        super(bean);
    }

    public MS_YamlContentJSONObject(Object object, String[] names) {
        super(object, names);
    }

    public MS_YamlContentJSONObject(String source) throws JSONException {
        super(source);
    }

    // *** Value getters ***

    @Override
    public <T> T getNested(String jsonPath, Class<T> typeOfObject) {
        return super.getNested(jsonPath, typeOfObject);
    }
}
