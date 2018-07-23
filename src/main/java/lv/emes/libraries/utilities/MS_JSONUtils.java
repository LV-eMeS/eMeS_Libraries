package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utilities providing operations related to JSON data structures.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_JSONUtils {

    private static boolean jsonFieldOrderingSetupDone = false;
    private static Field JSONObjectMapField = null;

    private MS_JSONUtils() {
    }

    public static JSONObject newOrderedJSONObject() {
        setupJSONFieldAccessor();
        JSONObject result = new JSONObject();
        try {
            if (JSONObjectMapField != null) {
                JSONObjectMapField.set(result, new LinkedHashMap<>());
            }
        }catch (IllegalAccessException ignored) {}
        return result;
    }

    /**
     * Produces new array which consists of first array elements followed by second array elements.
     *
     * @param first  first JSON array.
     * @param second second JSON array.
     * @return JSON array which consists of first + second array contents.
     */
    public static JSONArray concatArrays(JSONArray first, JSONArray second) {
        JSONArray concatenatedArray = new JSONArray();
        if (first == null && second == null) {
            return concatenatedArray;
        } else {
            if (first == null) return second;
            if (second == null) return first;

            first.forEach(concatenatedArray::put);
            second.forEach(concatenatedArray::put);
            return concatenatedArray;
        }
    }

    /**
     * Converts list to an array of strings.
     *
     * @param list list of string elements.
     * @return array of list elements like: <code>["el1", "el2"]</code>
     */
    public static String listToArrayOfStrings(List<String> list) {
        if (list == null || list.size() == 0) return "[]";
        StringBuilder arrayOfStrings = new StringBuilder("[");
        for (int i = 0; i < list.size() - 2; i++) {
            String element = list.get(i);
            arrayOfStrings.append("\"").append(element).append("\", ");
        }
        arrayOfStrings.append("\"").append(list.get(list.size() - 1)).append("\"]");
        return arrayOfStrings.toString();
    }

    /**
     * Converts map into JSON object. If map is null then empty object is returned.
     *
     * @param map key-value map, where keys represents JSON fields, and values - JSON values to corresponding keys.
     * @return non-null JSON object representing given map.
     */
    public static JSONObject mapToJSONObject(Map<String, String> map) {
        JSONObject json = new JSONObject();
        if (map != null) map.forEach(json::put);
        return json;
    }

    /**
     * Puts a <b>value</b> associated with presented <b>key</b> into <b>jsonObject</b>.
     * Should be used only for optional fields.
     *
     * @param jsonObject JSON object where value will be put.
     * @param key        a key of JSON field.
     * @param value      valid object representing data to be put.
     */
    public static void putIfNotNull(JSONObject jsonObject, String key, Object value) {
        if (value != null) jsonObject.put(key, value);
    }

    public static boolean isJsonNotNull(Object jsonObject) {
        return jsonObject != null && !jsonObject.toString().equals("null");
    }

    public static boolean isJsonObject(Object jsonObject) {
        return isJsonNotNull(jsonObject) && jsonObject instanceof JSONObject;
    }

    public static boolean isJsonArray(Object jsonObject) {
        return isJsonNotNull(jsonObject) && jsonObject instanceof JSONArray;
    }

    /**
     * Iterates through JSON objects in JSON array and performs given action <b>action</b>.
     * <p><u>Example</u>:<br>
     * <code>
     * JSONUtils.forEachArrayJSONObject(jsonArray, jsonObject -&gt; {<br>
     * //do something with jsonObject <br>
     * });<br>
     * </code>
     *
     * @param array  NonNull collection of JSON objects.
     * @param action NonNull consumer, which accepts JSON objects.
     */
    public static void forEachArrayJSONObject(JSONArray array, Consumer<JSONObject> action) {
        if (array == null) throw new MS_BadSetupException("JSON array mustn't be null");
        array.forEach((object) -> action.accept((JSONObject) object));
    }

    /**
     * Iterates through JSON objects in JSON array and performs given action <b>action</b> while <b>breakLoop</b> flag,
     * which is passed as second argument of <b>action</b> bi-consumer is <b>false</b>.
     * <p><u>Example</u>:<br>
     * <code>
     * JSONUtils.forEachArrayJSONObject(jsonArray, (jsonObject, breakLoop) -&gt; {<br>
     * //do something with jsonObject <br>
     * breakLoop.set(true); //break is done right after first iteration<br>
     * });<br>
     * </code>
     *
     * @param array  NonNull collection of JSON objects.
     * @param action NonNull bi-consumer, which accepts JSON objects and flag of type {@link AtomicBoolean}, with
     *               initial value <b>false</b>. Iterating will continue unless the value of this flag will be set to
     *               <b>true</b>, which will be signal to break iterating and thus next JSON object will not be iterated.
     */
    public static void forEachArrayJSONObject(JSONArray array, BiConsumer<JSONObject, AtomicBoolean> action) {
        if (array == null) throw new MS_BadSetupException("JSON array mustn't be null");
        AtomicBoolean breakLoop = new AtomicBoolean(false);
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext() && !breakLoop.get()) {
            action.accept((JSONObject) iterator.next(), breakLoop);
        }
    }

    //*** Private methods ***

    private static void setupJSONFieldAccessor() {
        if( !jsonFieldOrderingSetupDone) {
            jsonFieldOrderingSetupDone = true;
            try {
                JSONObjectMapField = JSONObject.class.getDeclaredField("map");
                JSONObjectMapField.setAccessible(true);
            } catch (NoSuchFieldException ignored) {
            }
        }
    }
}