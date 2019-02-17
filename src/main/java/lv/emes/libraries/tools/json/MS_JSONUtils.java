package lv.emes.libraries.tools.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Utilities providing operations related to JSON data structures.
 *
 * @author eMeS
 * @version 2.0.
 * @since 2.1.9
 */
public class MS_JSONUtils {

    private MS_JSONUtils() {
    }

    /**
     * Produces new array which consists of first array elements followed by second array elements.
     *
     * @param first  first JSON array.
     * @param second second JSON array.
     * @return JSON array which consists of first + second array content.
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
     * Tests if some JSON array is empty or null.
     *
     * @param array any array of JSON elements.
     * @return true if array is null or has no elements; false, if there is at least 1 element in array.
     */
    public static boolean isEmpty(JSONArray array) {
        return array == null || array.length() == 0;
    }

    public static boolean isJsonNull(Object jsonObject) {
        return jsonObject == null || JSONObject.NULL.equals(jsonObject);
    }

    public static boolean isJsonNotNull(Object jsonObject) {
        return !isJsonNull(jsonObject);
    }

    public static boolean isJsonObject(Object jsonObject) {
        return isJsonNotNull(jsonObject) && jsonObject instanceof JSONObject;
    }

    public static boolean isJsonArray(Object jsonObject) {
        return isJsonNotNull(jsonObject) && jsonObject instanceof JSONArray;
    }

    /**
     * Detects type of string in scope of JSON objects, arrays or other strings.
     * @param stringOfUnknownType given string.
     * @return JSON type of given string.
     */
    public static JSONTypeEnum detectStringJSONType(String stringOfUnknownType) {
        JSONTokener tokenizer = new JSONTokener(stringOfUnknownType);
        switch (tokenizer.nextClean()) {
            case '{': return JSONTypeEnum.OBJECT;
            case '[': return JSONTypeEnum.ARRAY;
            default: return JSONTypeEnum.STRING;
        }
    }

    //*** Internal methods ***

    static boolean isOrgJsonObject(Object jsonObject) {
        //Object = false; org.json.MS_JSONObject = true; MS_JSONObject = false
        return jsonObject instanceof org.json.JSONObject && jsonObject.getClass().isAssignableFrom(org.json.JSONObject.class);
    }

    static boolean isOrgJsonArray(Object jsonObject) {
        //Object = false; org.json.MS_JSONArray = true; MS_JSONArray = false
        return jsonObject instanceof org.json.JSONArray && jsonObject.getClass().isAssignableFrom(org.json.JSONArray.class);
    }
}