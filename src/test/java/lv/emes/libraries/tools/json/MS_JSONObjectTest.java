package lv.emes.libraries.tools.json;

import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lv.emes.libraries.testdata.TestData.OBJECT_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_JSONObjectTest {

    @Test
    public void testNoArgConstructorAndEquals() {
        MS_JSONObject obj1 = new MS_JSONObject().put("key1", "value").put("key2", "value2");
        MS_JSONObject obj2 = new MS_JSONObject().put("key2", "value2").put("key1", "value1");
        MS_JSONObject obj3 = new MS_JSONObject().put("key1", "value").put("key2", "value2");
        MS_JSONObject obj4 = new MS_JSONObject().put("key2", "value2").put("key1", "value"); // = obj1 = obj3; only different order of fields

        assertThat(obj1).isEqualTo(obj3);
        assertThat(obj1).isNotEqualTo(obj2);
        assertThat(obj4).isEqualTo(obj3); //different order
    }

    @Test
    public void testNewJSONObjectWithSpecificFieldsOnly() {
        MS_JSONObject source = MS_JSONObject.cast(OBJECT_MAP.get("beanJSONRepresentation"));
        MS_JSONObject target = new MS_JSONObject(source, "field2");
        assertThat(target).isEqualTo(new MS_JSONObject().put("field2", source.get("field2")));
    }

    @Test
    public void testGetJSONArray() {
        MS_JSONObject json = new MS_JSONObject()
                .put("list", OBJECT_MAP.get("list"))
                .put("json", OBJECT_MAP.get("json"));

        assertThat(json.length()).isEqualTo(2);
        assertThat(json.getJSONArray("list"))
                .isEqualTo(new MS_JSONArray().put(false).put(2L).put(((List) OBJECT_MAP.get("list")).get(2).toString()));
        assertThat(json.optJSONArray("blah, blah")).isNull();
        assertThatThrownBy(() -> json.getJSONArray("json"))
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONObject[\"json\"] is not a MS_JSONArray.");
    }

    @Test
    public void testGetJSONObject() {
        MS_JSONObject json = new MS_JSONObject()
                .put("json", OBJECT_MAP.get("json"))
                .put("orgJson", OBJECT_MAP.get("orgJson"))
                .put("list", OBJECT_MAP.get("list"));
        assertThat(json.length()).isEqualTo(3);
        assertThat(json.getJSONObject("json")).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(json.getJSONObject("orgJson")).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(json.optJSONObject("blah, blah")).isNull();
        assertThatThrownBy(() -> json.getJSONObject("list"))
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONObject[\"list\"] is not a MS_JSONObject.");
    }

    @Test
    public void testAccumulate() {
        MS_JSONObject json = new MS_JSONObject();
        json.accumulate("key", 1);
        assertThat(json.get("key")).isInstanceOf(Integer.class);
        assertThat(json.getInt("key")).isEqualTo(1);

        json.accumulate("key", 2);
        assertThat(json.get("key")).isInstanceOf(MS_JSONArray.class);
        assertThat(json.getJSONArray("key")).hasSize(2).isEqualTo(new MS_JSONArray().put(1).put(2));
    }

    @Test
    public void testAppend() {
        MS_JSONObject json = new MS_JSONObject();
        json.append("key", 1);
        assertThat(json.get("key")).isInstanceOf(MS_JSONArray.class);
        assertThat(json.getJSONArray("key")).hasSize(1).isEqualTo(new MS_JSONArray().put(1));

        json.append("key", 2);
        assertThat(json.get("key")).isInstanceOf(MS_JSONArray.class);
        assertThat(json.getJSONArray("key")).hasSize(2).isEqualTo(new MS_JSONArray().put(1).put(2));

        json.put("org.MS_JSONArray", new JSONArray());
        json.append("org.MS_JSONArray", 3); //array already exists, so value will be added to it
        assertThat(json.getJSONArray("org.MS_JSONArray")).hasSize(1).isEqualTo(new MS_JSONArray().put(3));

        json.put("fail", new MS_JSONObject());
        assertThatThrownBy(() -> json.append("fail", 4))
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONObject[\"fail\"] is not a MS_JSONArray.");
    }

    @Test
    public void testObjectFromStringParsedCorrectly() {
        JSONObject orgJsonObjct = new JSONObject()
                .put("primitive", OBJECT_MAP.get("primitive"))
                .put("bool", OBJECT_MAP.get("bool"))
                .put("map", OBJECT_MAP.get("map"))
                .put("json", OBJECT_MAP.get("json"))
                .put("orgJson", OBJECT_MAP.get("orgJson"))
                .put("array", OBJECT_MAP.get("array"))
                .put("orgArray", OBJECT_MAP.get("orgArray"));
        MS_JSONObject actual = new MS_JSONObject(orgJsonObjct.toString());
        assertThat(actual).isEqualTo(orgJsonObjct);
        assertThat(actual.getInt("primitive")).isEqualTo(123);
        assertThat(actual.getBoolean("bool")).isTrue();
        assertThat(actual.get("map")).isInstanceOf(MS_JSONObject.class).isEqualTo(OBJECT_MAP.get("mapJSONRepresentation"));
        assertThat(actual.get("json")).isInstanceOf(MS_JSONObject.class).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(actual.get("orgJson")).isInstanceOf(MS_JSONObject.class).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(actual.get("array")).isInstanceOf(MS_JSONArray.class).isEqualTo(OBJECT_MAP.get("array"));
        assertThat(actual.get("orgArray")).isInstanceOf(MS_JSONArray.class).isEqualTo(OBJECT_MAP.get("array"));
    }

    @Test
    public void testPut() {
        MS_JSONObject json = new MS_JSONObject()
                .put("map", ImmutableMap.<Integer, Object>builder()
                        .put(1, OBJECT_MAP.get("primitive"))
                        .put(2, OBJECT_MAP.get("bool"))
                        .put(3, OBJECT_MAP.get("json"))
                        .put(4, OBJECT_MAP.get("array"))
                        .build())
                .put("list", Arrays.asList(546654, "test", new MS_JSONArray().put("inner test")))
                .put("long", 45_000_000L)
                .put("bool", OBJECT_MAP.get("bool"));

        assertThat(json.get("map")).isInstanceOf(MS_JSONObject.class)
                .isEqualTo(new MS_JSONObject()
                        .put("1", OBJECT_MAP.get("primitive"))
                        .put("3", OBJECT_MAP.get("json"))
                        .put("2", OBJECT_MAP.get("bool"))
                        .put("4", OBJECT_MAP.get("array"))
                );
    }

    @Test
    public void testNewJSONObjectFromMap() {
        Map<String, Object> aMap = OBJECT_MAP;
        MS_JSONObject newJSONObject = new MS_JSONObject(aMap);

        assertThat(newJSONObject.length()).isEqualTo(aMap.size());
        assertThat(newJSONObject.get("null")).isEqualTo(MS_JSONObject.NULL);
        assertThat(newJSONObject.getInt("primitive")).isEqualTo(123);
        assertThat(newJSONObject.getBoolean("bool")).isTrue();
        assertThat(newJSONObject.get("object")).isEqualTo(aMap.get("object").toString());
        assertThat(newJSONObject.getJSONObject("bean")).isEqualTo(aMap.get("beanJSONRepresentation"));
        assertThat(newJSONObject.getJSONArray("list"))
                .isEqualTo(new MS_JSONArray().put(false).put(2L).put(((List) aMap.get("list")).get(2).toString()));
        assertThat(newJSONObject.getJSONArray("listOfLists"))
                .isEqualTo(new MS_JSONArray()
                        .put(new MS_JSONArray().put(999))
                        .put(new MS_JSONArray().put(998).put(997))
                );
        assertThat(newJSONObject.getJSONObject("json")).isEqualTo(aMap.get("json"));
        assertThat(newJSONObject.getJSONObject("orgJson")).isEqualTo(aMap.get("json"));
        assertThat(newJSONObject.getJSONObject("map")).isEqualTo(new MS_JSONObject().put("double", 3.14d));
    }

    @Test
    public void testNewJSONObjectFromMaps() {
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        root.put("requestId", "123456789");
        root.put("data", data);
        data.put("item", null);

        MS_JSONObject jsonRoot = new MS_JSONObject(root);
        MS_JSONObject jsonData = jsonRoot.getJSONObject("data");
        assertThat(jsonRoot.getString("requestId")).isEqualTo("123456789");
        assertThat(jsonData.length()).isEqualTo(1); //size of MS_JSONObject should be 1 item
        assertThat(jsonData.get("item")).isEqualTo(JSONObject.NULL);
    }

    @Test
    public void testNewJSONObjectFromMapInvalidStructure() {
        Map<String, Object> root = new HashMap<>();
        Map<Object, Object> data = new HashMap<>();
        Object customNode = new Object();

        root.put("data", data);
        data.put(9876, 3.14);
        data.put(customNode, customNode);

        MS_JSONObject jsonRoot = new MS_JSONObject(root);
        assertThat(jsonRoot.length()).isEqualTo(1);

        MS_JSONObject jsonData = jsonRoot.getJSONObject("data");
        assertThat(jsonData.length()).isEqualTo(2);
        assertThat(jsonData.getDouble("9876")).isEqualTo(3.14d);
        assertThat(jsonData.get(customNode.toString())).isEqualTo(customNode.toString());
    }

    @Test
    public void testCast() {
        assertThat(MS_JSONObject.cast(OBJECT_MAP.get("orgJson"))).isInstanceOf(MS_JSONObject.class);
        assertThat(MS_JSONObject.cast(OBJECT_MAP.get("json"))).isInstanceOf(MS_JSONObject.class);
        assertThatThrownBy(() -> MS_JSONObject.cast(OBJECT_MAP.get("array"))).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testIsNullObjectOrArray() {
        MS_JSONObject object = new MS_JSONObject()
                .put("null", (Map<?, ?>) null)
                .put("nullObject", JSONObject.NULL)
                .put("object", new MS_JSONObject())
                .put("array", new MS_JSONArray());

        assertThat(object.isNull("invalid key")).isTrue();
        assertThat(object.isNull("null")).isTrue();
        assertThat(object.isNull("nullObject")).isTrue();
        assertThat(object.isNull("object")).isFalse();
        assertThat(object.isNull("array")).isFalse();

        assertThat(object.isNotNull("invalid key")).isFalse();
        assertThat(object.isNotNull("null")).isFalse();
        assertThat(object.isNotNull("nullObject")).isFalse();
        assertThat(object.isNotNull("object")).isTrue();
        assertThat(object.isNotNull("array")).isTrue();

        assertThat(object.isJsonObject("invalid key")).isFalse();
        assertThat(object.isJsonObject("null")).isFalse();
        assertThat(object.isJsonObject("nullObject")).isFalse();
        assertThat(object.isJsonObject("object")).isTrue();
        assertThat(object.isJsonObject("array")).isFalse();

        assertThat(object.isJsonArray("invalid key")).isFalse();
        assertThat(object.isJsonArray("null")).isFalse();
        assertThat(object.isJsonArray("nullObject")).isFalse();
        assertThat(object.isJsonArray("object")).isFalse();
        assertThat(object.isJsonArray("array")).isTrue();
    }
}