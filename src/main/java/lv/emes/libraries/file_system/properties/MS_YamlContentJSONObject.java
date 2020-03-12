package lv.emes.libraries.file_system.properties;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.communication.json.MS_ReadOnlyJSONArray;
import lv.emes.libraries.communication.json.MS_ReadOnlyJSONObject;
import lv.emes.libraries.utilities.MS_CodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

/**
 * Immutable JSON object structure that holds content of some Yaml properties file.
 * While performing get operations from this instance if there is some system property or environment variable with
 * same path as property that is requested, the property itself is returned instead of returning property from Yaml.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.0.
 */
class MS_YamlContentJSONObject extends MS_ReadOnlyJSONObject {

    @Override
    public MS_JSONObject newJSONObjectInstance(Object object) {
        return new MS_YamlContentJSONObject(object);
    }

    @Override
    public MS_JSONObject newJSONObjectInstance(JSONObject object) {
        return new MS_YamlContentJSONObject(object);
    }

    @Override
    public MS_JSONObject newJSONObjectInstance(Map<?, ?> map) {
        return new MS_YamlContentJSONObject(map);
    }

    @Override
    public MS_JSONArray newJSONArrayInstance(JSONArray array) {
        return new MS_ReadOnlyJSONArray(array);
    }

    @Override
    public MS_JSONArray newJSONArrayInstance(Object array) {
        return new MS_ReadOnlyJSONArray(array);
    }

    @Override
    public MS_JSONArray newJSONArrayInstance(Collection<?> coll) {
        return new MS_ReadOnlyJSONArray(coll);
    }

    public MS_YamlContentJSONObject(Object bean) {
        super(bean);
    }

    public MS_YamlContentJSONObject(JSONObject obj) {
        super(obj);
    }

    public MS_YamlContentJSONObject(Map<?, ?> map) {
        super(map);
    }

    // *** Value getters ***


    @Override
    public MS_JSONArray getJSONArray(String key) throws JSONException {
        return getNested(key, MS_JSONArray.class);
    }

    @Override
    public MS_JSONObject getJSONObject(String key) throws JSONException {
        return getNested(key, MS_JSONObject.class);
    }

    @Override
    public boolean getBoolean(String key) throws JSONException {
        return getNested(key, Boolean.class);
    }

    @Override
    public BigInteger getBigInteger(String key) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal getBigDecimal(String key) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getDouble(String key) throws JSONException {
        return getNested(key, Double.class);
    }

    @Override
    public float getFloat(String key) throws JSONException {
        return getNested(key, Float.class);
    }

    @Override
    public Number getNumber(String key) throws JSONException {
        return getNested(key, Number.class);
    }

    @Override
    public int getInt(String key) throws JSONException {
        return getNested(key, Integer.class);
    }

    @Override
    public long getLong(String key) throws JSONException {
        return getNested(key, Long.class);
    }

    @Override
    public String getString(String key) throws JSONException {
        return getNested(key, String.class);
    }

    @Override
    public <T> T getNested(String jsonPath, Class<T> typeOfObject) {
        return MS_CodingUtils.getFromSysOrEnvProperty(jsonPath, typeOfObject)
                .orElseGet(() -> super.getNested(jsonPath, typeOfObject));
    }
}
