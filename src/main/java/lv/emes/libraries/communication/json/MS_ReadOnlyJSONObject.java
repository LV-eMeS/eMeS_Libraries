package lv.emes.libraries.communication.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Collection;
import java.util.Map;

/**
 * Immutable JSON object.
 * All the put methods are unsupported for this type of JSON object.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.0.
 */
public class MS_ReadOnlyJSONObject extends MS_JSONObject {

    private boolean initiated = true; // gets its value assigned after constructor, therefore makes methods from
    // super used in constructors to run without an exception

    public MS_ReadOnlyJSONObject(JSONObject obj) {
        super(obj);
    }

    public MS_ReadOnlyJSONObject(Map<?, ?> map) {
        super(map);
    }

    public MS_ReadOnlyJSONObject(JSONObject jo, String... names) {
        super(jo, names);
    }

    public MS_ReadOnlyJSONObject(JSONTokener x) throws JSONException {
        super(x);
    }

    public MS_ReadOnlyJSONObject(Object bean) {
        super(bean);
    }

    public MS_ReadOnlyJSONObject(Object object, String[] names) {
        super(object, names);
    }

    public MS_ReadOnlyJSONObject(String source) throws JSONException {
        super(source);
    }

    @Override
    public MS_JSONObject put(String key, Object value) throws JSONException {
        if (initiated)
            throw new UnsupportedOperationException();
        else
            return super.put(key, value);
    }

    @Override
    public MS_JSONObject putOnce(String key, Object value) throws JSONException {
        if (initiated)
            throw new UnsupportedOperationException();
        else
            return super.putOnce(key, value);
    }

    @Override
    public MS_JSONObject putOpt(String key, Object value) throws JSONException {
        if (initiated)
            throw new UnsupportedOperationException();
        else
            return super.putOpt(key, value);
    }

    @Override
    public MS_JSONObject accumulate(String key, Object value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject append(String key, Object value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, boolean value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, Collection<?> value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, double value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, int value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, long value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONObject put(String key, Map<?, ?> value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject increment(String key) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject put(String key, float value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(String key) {
        throw new UnsupportedOperationException();
    }
}
