package lv.emes.libraries.tools.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static lv.emes.libraries.testdata.TestData.OBJECT_MAP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_JSONArrayTest {

    @Test
    public void testGetJSONArray() {
        MS_JSONArray array = new MS_JSONArray()
                .put(new MS_JSONArray().put(OBJECT_MAP.get("json")))
                .put(new org.json.JSONArray().put(OBJECT_MAP.get("orgJson")));
        assertThat(array).hasSize(2);
        assertThat(array.getJSONArray(0)).isEqualTo(new MS_JSONArray().put(OBJECT_MAP.get("json")));
        assertThat(array.getJSONArray(1)).isEqualTo(new MS_JSONArray().put(OBJECT_MAP.get("json")));
        assertThat(array.optJSONArray(3)).isNull();
        assertThatThrownBy(() -> array.getJSONObject(1))
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONArray[1] is not a MS_JSONObject.");
    }

    @Test
    public void testGetJSONObject() {
        MS_JSONArray array = new MS_JSONArray()
                .put(OBJECT_MAP.get("json"))
                .put(OBJECT_MAP.get("orgJson"))
                .put(JSONObject.NULL);
        assertThat(array).hasSize(3);
        assertThat(array.getJSONObject(0)).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(array.getJSONObject(1)).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(array.get(2)).isEqualTo(JSONObject.NULL);
        assertThat(array.optJSONObject(3)).isNull();
        assertThatThrownBy(() -> array.getJSONArray(1))
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONArray[1] is not a MS_JSONArray.");
    }

    @Test
    public void testArrayFromStringParsedCorrectly() {
        JSONArray orgJsonArray = new JSONArray()
                .put(OBJECT_MAP.get("orgJson"))
                .put(OBJECT_MAP.get("primitive"))
                .put(OBJECT_MAP.get("bool"))
                .put(OBJECT_MAP.get("object")) //will be parsed as string anyways
                .put(OBJECT_MAP.get("listOfLists"));
        MS_JSONArray actual = new MS_JSONArray(orgJsonArray.toString());
        assertThat(actual.toString()).isEqualTo(orgJsonArray.toString());

        //additional check, by using forEach, which possibly is another way to get old org.JSON objects
        actual = new MS_JSONArray(orgJsonArray.toString());
        List<Object> objectsInArray = new ArrayList<>();
        actual.forEach(objectsInArray::add);
        assertThat(objectsInArray).hasSize(5);
        assertThat(objectsInArray.get(0)).isInstanceOf(JSONObject.class);
        assertThat(objectsInArray.get(1)).isInstanceOf(Integer.class);
        assertThat(objectsInArray.get(2)).isInstanceOf(Boolean.class);
        assertThat(objectsInArray.get(3)).isInstanceOf(String.class);
        //also check nested JSON Array
        Object arrayOfArraysObj = objectsInArray.get(4);
        assertThat(arrayOfArraysObj).isInstanceOf(MS_JSONArray.class);
        MS_JSONArray arrayOfArrays = MS_JSONArray.cast(arrayOfArraysObj);
        assertThat(arrayOfArrays).hasSize(2);
        assertThat(arrayOfArrays.get(0)).isInstanceOf(MS_JSONArray.class);
        assertThat(arrayOfArrays.get(1)).isInstanceOf(MS_JSONArray.class);
    }

    @Test
    public void testPutObject() {
        MS_JSONArray array = new MS_JSONArray()
                .put(OBJECT_MAP.get("bean"))
                .put(OBJECT_MAP.get("json"))
                .put(OBJECT_MAP.get("orgJson"));
        assertThat(array).hasSize(3);
        assertThat(array.getJSONObject(0)).isEqualTo(OBJECT_MAP.get("beanJSONRepresentation"));
        assertThat(array.getJSONObject(1)).isEqualTo(OBJECT_MAP.get("json"));
        assertThat(array.getJSONObject(2)).isEqualTo(OBJECT_MAP.get("json"));
    }

    @Test
    public void testNewJSONArrayFromListOfObjects() {
        List<org.json.JSONObject> listOfOrgObjects = new ArrayList<>();
        listOfOrgObjects.add((org.json.JSONObject) OBJECT_MAP.get("orgJson"));
        listOfOrgObjects.add(new org.json.JSONObject());

        List<MS_JSONObject> listOfObjects = new ArrayList<>();
        listOfObjects.add((MS_JSONObject) OBJECT_MAP.get("json"));
        listOfObjects.add(new MS_JSONObject());

        MS_JSONArray expected = new MS_JSONArray().put(OBJECT_MAP.get("json")).put(new MS_JSONObject());

        assertThat(new MS_JSONArray(listOfOrgObjects)).isEqualTo(expected);
        assertThat(new MS_JSONArray(listOfObjects)).isEqualTo(expected);
    }

    @Test
    public void testNewJSONArrayFromListOfArrays() {
        List<JSONArray> listOfOrgJSONArrays = new ArrayList<>();
        listOfOrgJSONArrays.add((JSONArray) OBJECT_MAP.get("orgArray"));
        listOfOrgJSONArrays.add(new JSONArray());
        listOfOrgJSONArrays.add(new JSONArray().put(OBJECT_MAP.get("map")));

        List<MS_JSONArray> listOfArrays = new ArrayList<>();
        listOfArrays.add((MS_JSONArray) OBJECT_MAP.get("array"));
        listOfArrays.add(new MS_JSONArray());
        listOfArrays.add(new MS_JSONArray().put(OBJECT_MAP.get("map")));

        MS_JSONArray expected = new MS_JSONArray(((MS_JSONArray) OBJECT_MAP.get("array")))
                .put(OBJECT_MAP.get("mapJSONRepresentation"));

        assertThat(MS_JSONArray.newJSONArray(listOfOrgJSONArrays)).isEqualTo(expected);
        assertThat(MS_JSONArray.newJSONArray(listOfArrays)).isEqualTo(expected);
    }

    @Test
    public void testToJSONObjectList() {
        MS_JSONArray array = new MS_JSONArray().put(OBJECT_MAP.get("bean")).put(OBJECT_MAP.get("map"));
        List<MS_JSONObject> objects = array.toJSONObjectList();
        assertThat(objects.size()).isEqualTo(array.length());
        assertThat(objects.get(0)).isEqualTo(OBJECT_MAP.get("beanJSONRepresentation"));
        assertThat(objects.get(1)).isEqualTo(OBJECT_MAP.get("mapJSONRepresentation"));

        assertThatThrownBy(() -> new MS_JSONArray().put(OBJECT_MAP.get("array")).put(OBJECT_MAP.get("primitive")).toJSONObjectList())
                .isInstanceOf(JSONException.class)
                .hasMessage("MS_JSONArray[0] is not a MS_JSONObject.");
    }

    @Test
    public void testToJSONObjectListIgnoreNonJsonObjects() {
        MS_JSONArray array = new MS_JSONArray().put(OBJECT_MAP.get("bean")).put(12).put(OBJECT_MAP.get("map"));
        List<MS_JSONObject> objects = array.toJSONObjectList(true);
        assertThat(objects.size()).isEqualTo(2);
        assertThat(objects.get(0)).isEqualTo(OBJECT_MAP.get("beanJSONRepresentation"));
        assertThat(objects.get(1)).isEqualTo(OBJECT_MAP.get("mapJSONRepresentation"));
    }

    @Test
    public void testCast() {
        assertThat(MS_JSONArray.cast(OBJECT_MAP.get("orgArray"))).isInstanceOf(MS_JSONArray.class);
        assertThat(MS_JSONArray.cast(OBJECT_MAP.get("array"))).isInstanceOf(MS_JSONArray.class);
        assertThatThrownBy(() -> MS_JSONArray.cast(OBJECT_MAP.get("object"))).isInstanceOf(ClassCastException.class);
    }

    @Test
    public void testForEachJSONObject() {
        MS_JSONObject json1 = new MS_JSONObject().put("number", 1);
        MS_JSONObject json2 = new MS_JSONObject().put("number", 2);
        MS_JSONArray MS_JSONArray = new MS_JSONArray().put(json1).put("Not JSON object").put(json2); //add 3 elements

        List<MS_JSONObject> iteratedElements = new ArrayList<>();
        MS_JSONArray.forEachElement(MS_JSONObject.class, (Consumer<MS_JSONObject>) iteratedElements::add);
        assertThat(iteratedElements).hasSize(2).containsExactly(json1, json2);
    }

    @Test
    public void testForEachJSONObjectWithBreak() {
        MS_JSONObject json1 = new MS_JSONObject().put("number", 1);
        MS_JSONObject json2 = new MS_JSONObject().put("number", 2);
        MS_JSONObject json3 = new MS_JSONObject().put("number", 3); //loop break will happen before this object will be reached
        MS_JSONArray MS_JSONArray = new MS_JSONArray().put(json1).put("Not JSON object").put(json2).put(json3); //add 4 elements

        List<MS_JSONObject> iteratedElements = new ArrayList<>();
        MS_JSONArray.forEachElement(MS_JSONObject.class, (jsonObject, breakLoop) -> {
            iteratedElements.add(jsonObject);
            breakLoop.set(jsonObject.getInt("number") == 2);
        });
        assertThat(iteratedElements).hasSize(2).containsExactly(json1, json2);
    }

    @Test
    public void testFilter() {
        MS_JSONArray array = new MS_JSONArray()
                .put(new MS_JSONObject().put("someIntField", 5))
                .put(7) //should be ignored in process
                .put(new MS_JSONObject().put("someIntField", 0))
                ;
        MS_JSONArray arrayWithJust1Element = array.filter(MS_JSONObject.class, (jsonObject) -> jsonObject.getInt("someIntField") > 1);
        assertThat(arrayWithJust1Element).hasSize(1).contains(new MS_JSONObject().put("someIntField", 5));
    }

    @Test
    public void testFilterWithLimit() {
        MS_JSONArray array = new MS_JSONArray()
                .put(new MS_JSONObject().put("someIntField", 3))
                .put(8) //should be ignored in process
                .put(new MS_JSONObject().put("someIntField", 2))
                ;
        MS_JSONArray arrayWithJust1Element = array.filter(MS_JSONObject.class, 1, (jsonObject) -> jsonObject.getInt("someIntField") > 1);
        assertThat(arrayWithJust1Element).hasSize(1).contains(new MS_JSONObject().put("someIntField", 3));
    }

    @Test
    public void testExtractFromArray() {
        MS_JSONArray MS_JSONArray = new MS_JSONArray();
        MS_JSONObject json1 = new MS_JSONObject().put("number", 14);
        MS_JSONObject json2 = new MS_JSONObject().put("number", 6);
        MS_JSONArray.put(json1);
        MS_JSONArray.put(json2);

        List<Integer> expected = new ArrayList<>();
        expected.add(14);
        expected.add(6);

        List<Integer> actual = MS_JSONArray.extract(MS_JSONObject.class, (numbers -> numbers.getInt("number")));
        assertThat(actual).isEqualTo(expected);
    }
}