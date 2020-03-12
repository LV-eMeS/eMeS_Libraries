package lv.emes.libraries.communication.json;

import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_ReadOnlyJSONArrayTest {

    @Test
    public void testNoArgConstructor() {
        assertThat(new MS_ReadOnlyJSONArray()).isInstanceOf(MS_ReadOnlyJSONArray.class).isEqualTo(new JSONArray());
    }

    @Test
    public void testConstructFromJSONArray() {
        JSONArray arr = new JSONArray().put(5).put(7);
        assertThat(new MS_ReadOnlyJSONArray(arr)).isInstanceOf(MS_ReadOnlyJSONArray.class)
                .isEqualTo(new MS_JSONArray().put(5).put(7));
    }

    @Test
    public void testConstructFromObject() {
        Object arr = new Integer[] {5, 7};
        assertThat(new MS_ReadOnlyJSONArray(arr)).isInstanceOf(MS_ReadOnlyJSONArray.class)
                .isEqualTo(new MS_JSONArray().put(5).put(7));
    }

    @Test
    public void testConstructFromListOfJSONObjects() {
        List<MS_JSONObject> objects = new ArrayList<>();
        objects.add(new MS_JSONObject().put("key", "value").put("arr", new MS_ReadOnlyJSONArray()));
        assertThat(new MS_ReadOnlyJSONArray(objects)).isInstanceOf(MS_ReadOnlyJSONArray.class)
                .isEqualTo(new MS_JSONArray().put(new MS_JSONObject().put("key", "value").put("arr", new MS_JSONArray())));
    }

    @Test
    public void testConstructFromCollection() {
        List<Object> objects = new ArrayList<>();
        objects.add(1);
        objects.add("str");
        objects.add(new MS_JSONObject().put("key", "value"));
        assertThat(new MS_ReadOnlyJSONArray(objects)).isInstanceOf(MS_ReadOnlyJSONArray.class)
                .isEqualTo(new MS_JSONArray().put(1).put("str").put(new MS_JSONObject().put("key", "value")));
    }
}