package lv.emes.libraries.communication.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Immutable JSON array.
 * All the put and removal methods are unsupported for this type of JSON array.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.0.
 */
public class MS_ReadOnlyJSONArray extends MS_JSONArray {

    private boolean initiated = true; // gets its value assigned after constructor, therefore makes methods from
    // super used in constructors to run without an exception

    @Override
    public MS_JSONObject newJSONObjectInstance(Object object) {
        return new MS_ReadOnlyJSONObject(object);
    }

    @Override
    public MS_JSONObject newJSONObjectInstance(JSONObject object) {
        return new MS_ReadOnlyJSONObject(object);
    }

    @Override
    public MS_JSONObject newJSONObjectInstance(Map<?, ?> map) {
        return new MS_ReadOnlyJSONObject(map);
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

    // *** Constructors ***

    public MS_ReadOnlyJSONArray(JSONArray arr) {
        super(arr);
    }

    public MS_ReadOnlyJSONArray(Object array) throws JSONException {
        super(array);
    }

    public MS_ReadOnlyJSONArray(List<? extends JSONObject> objects) {
        super(objects);
    }

    public MS_ReadOnlyJSONArray() {
        super();
    }

    public MS_ReadOnlyJSONArray(Collection<?> collection) {
        super(collection);
    }

    // *** Restricted modification methods ***

    @Override
    public MS_JSONArray put(int index, Object value) throws JSONException {
        if (initiated)
            throw new UnsupportedOperationException();
        else
            return super.put(index, value);
    }

    @Override
    public MS_JSONArray put(Object value) {
        if (initiated)
            throw new UnsupportedOperationException();
        else
            return super.put(value);
    }

    @Override
    public MS_JSONArray put(boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(Collection<?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(double value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(long value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(Map<?, ?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, boolean value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, Collection<?> value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, double value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, int value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, long value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray put(int index, Map<?, ?> value) throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MS_JSONArray concat(JSONArray tail) {
        throw new UnsupportedOperationException();
    }
}
