package lv.emes.libraries.communication.json;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Utilities providing operations related to JSON data structures.
 *
 * @author eMeS
 * @version 2.1.
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
     *
     * @param stringOfUnknownType given string.
     * @return JSON type of given string.
     */
    public static JSONTypeEnum detectStringJSONType(String stringOfUnknownType) {
        JSONTokener tokenizer = new JSONTokener(stringOfUnknownType);
        switch (tokenizer.nextClean()) {
            case '{':
                return JSONTypeEnum.OBJECT;
            case '[':
                return JSONTypeEnum.ARRAY;
            default:
                return JSONTypeEnum.STRING;
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

    /**
     * Get node in JSON object of type {@link MS_JSONObject} or {@link MS_JSONArray} by field name in specific format.
     * <p><u>Example</u>: <code>
     * MS_JSONObject obj = MS_JSONUtils.getJSONNode(new MS_JSONObject().put("obj", new MS_JSONObject()), "obj");
     * MS_JSONObject one = MS_JSONUtils.getJSONNode(new MS_JSONObject().put("arr", new MS_JSONArray().put(new MS_JSONObject()).put(new MS_JSONObject())), "arr[0]");
     * MS_JSONObject two = MS_JSONUtils.getJSONNode(new MS_JSONObject().put("arr", new MS_JSONArray().put(new MS_JSONObject()).put(new MS_JSONObject())), "arr[1]");
     * </code>
     *
     * @param json      JSON object or array, which contains {@link MS_JSONObject} field with name <b>fieldName</b>.
     * @param fieldName name of concrete field in JSON path - like representation.
     * @return JSON node.
     */
    public static MS_JSON getJSONNode(MS_JSON json, String fieldName) {
        Pair<String, Integer> typeOfNode = resolveJSONPathNode(fieldName);
        if (typeOfNode.getRight() == null) { // This is an object, so nested object needs to be returned
            return ((MS_JSONObject) json).getJSON(fieldName);
        } else if (typeOfNode.getLeft() == null) { // This is an array itself => object at index needs to be returned
            return ((MS_JSONArray) json).getJSON(typeOfNode.getRight());
        } else { // An array within object
            return ((MS_JSONObject) json).getJSONArray(typeOfNode.getLeft()).getJSON(typeOfNode.getRight());
        }
    }

    public static <T> T getJSONNodeElement(MS_JSON node, Class<T> typeOfObject, String nodeField) {
        Pair<String, Integer> typeOfNode = MS_JSONUtils.resolveJSONPathNode(nodeField);
        if (typeOfNode.getRight() == null) { // This is an object, so we can get the value directly from it
            return typeOfObject.cast(((MS_JSONObject) node).get(nodeField));
        } else if (typeOfNode.getLeft() == null) { // This is an array itself => element at index needs to be returned
            return typeOfObject.cast(((MS_JSONArray) node).get(typeOfNode.getRight()));
        } else { // An array within object
            return typeOfObject.cast(((MS_JSONObject) node).getJSONArray(typeOfNode.getLeft()).get(typeOfNode.getRight()));
        }
    }

    /**
     * Resolves JSON node in JSON path - like representation.
     *
     * @param node like: "jsonObjectFieldName", "[0]", "arrayFieldName[11]".
     * @return "('jsonObjectFieldName', <tt>null</tt>)", "(<tt>null</tt>, 0)", "'arrayFieldName', 11"
     */
    static Pair<String, Integer> resolveJSONPathNode(String node) {
        int bracketBeginning = node.indexOf('[');
        if (bracketBeginning < 0) {
            return Pair.of(node, null);
        } else if (bracketBeginning == 0) {
            return Pair.of(null, Integer.parseInt(node.substring(1, node.length() - 1)));
        } else {
            return Pair.of(node.substring(0, bracketBeginning), Integer.parseInt(node.substring(bracketBeginning + 1, node.length() - 1)));
        }
    }
}