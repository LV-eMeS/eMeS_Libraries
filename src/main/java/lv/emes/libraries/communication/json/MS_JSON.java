package lv.emes.libraries.communication.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * An interface representing JSON structure.
 * <p>Public methods:
 * <ul>
 *     <li>get</li>
 *     <li>getNested</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.3.0.
 */
public interface MS_JSON {

    default Object wrapInternal(Object object) {
        try {
            if (object == null) {
                return JSONObject.NULL;
            }

            if (object instanceof MS_JSONObject || object instanceof MS_JSONArray
                    || JSONObject.NULL.equals(object) || object instanceof JSONString
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal || object instanceof Enum) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return newJSONArrayInstance(coll);
            }

            if (object.getClass().isArray()) {
                return newJSONArrayInstance(object);
            }

            //org.JSON ignores those map null values, but we will treat those as objects
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return newJSONObjectInstance(map);
            }

            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage
                    .getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }

            //upgrade org.JSON objects and arrays to ours
            if (MS_JSONUtils.isOrgJsonObject(object)) {
                return newJSONObjectInstance((JSONObject) object);
            } else if (MS_JSONUtils.isOrgJsonArray(object)) {
                return newJSONArrayInstance((JSONArray) object);
            }

            return newJSONObjectInstance(object);
        } catch (Exception exception) {
            return null;
        }
    }

    default MS_JSONObject newJSONObjectInstance(Object object) {
        return new MS_JSONObject(object);
    }

    default MS_JSONObject newJSONObjectInstance(JSONObject object) {
        return new MS_JSONObject(object);
    }

    default MS_JSONObject newJSONObjectInstance(Map<?, ?> map) {
        return new MS_JSONObject(map);
    }

    default MS_JSONArray newJSONArrayInstance(JSONArray array) {
        return new MS_JSONArray(array);
    }

    default MS_JSONArray newJSONArrayInstance(Object array) {
        return new MS_JSONArray(array);
    }

    default MS_JSONArray newJSONArrayInstance(Collection<?> coll) {
        return new MS_JSONArray(coll);
    }

    /**
     * Gets nested object or primitive from objects that this object holds.
     * It supports only navigation through types of {@link MS_JSONObject} and {@link MS_JSONArray} to get till target.
     * <p><u>Examples</u>:<ol>
     *     <li><code>assertThat(new MS_JSONObject().put("obj", new MS_JSONObject().put("1", 1)).getNested("obj.1", Integer.class)).isEqualTo(1)</code></li>
     *     <li><code>assertThat(new MS_JSONObject().put("arr", new MS_JSONArray().put(new MS_JSONObject().put("2", 2))).getNested("arr[0].2", Integer.class)).isEqualTo(2);</code></li>
     *     <li><code>assertThat(new MS_JSONArray().put(1).put(2).put(3).put(new MS_JSONArray().put(13).put(new MS_JSONObject().put("14", 14))).getNested("[3].[1].14", Integer.class)).isEqualTo(14);</code></li>
     * </ol>
     *
     * @param jsonPath     JSON path to target object.
     * @param typeOfObject class representing type of return value.
     * @param <T>          type of return value.
     * @return object value of type <b>typeOfObject</b> according to given <b>jsonPath</b>.
     * @throws NullPointerException     if <b>jsonPath</b> or <b>typeOfObject</b> is <tt>null</tt>.
     * @throws IllegalArgumentException if <b>jsonPath</b> is empty or invalid.
     * @throws JSONException            if some of nodes in <b>jsonPath</b> are invalid.
     * @throws ClassCastException       if return value is of different type than given <b>typeOfObject</b>.
     * @since 2.3
     */
    default <T> T getNested(String jsonPath, Class<T> typeOfObject) {
        Objects.requireNonNull(jsonPath);
        Objects.requireNonNull(typeOfObject);
        if (!jsonPath.matches("(\\[\\d+])+(.(\\w+)?(\\[\\d+])?)*|\\w+(\\[\\d+])?(.\\w+(\\[\\d+])?)*"))
            throw new IllegalArgumentException("Empty or invalid path. Path must be non-empty string delimited with dots" +
                    " and in case of arrays having square brackets and desired element index. Example of correct path:" +
                    " 'root.object.array[0].integerElement')");

        String[] path = jsonPath.split("\\.");
        MS_JSON node;

        if (path.length == 1) {
            return MS_JSONUtils.getJSONNodeElement(this, typeOfObject, path[0]);
        } else {
            node = MS_JSONUtils.getJSONNode(this, path[0]);
        }

        for (int i = 1; i < path.length - 1; i++) {
            node = MS_JSONUtils.getJSONNode(node, path[i]);
        }
        return MS_JSONUtils.getJSONNodeElement(node, typeOfObject, path[path.length - 1]);
    }
}
