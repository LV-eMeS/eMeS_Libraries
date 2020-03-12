package lv.emes.libraries.communication.json;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_JSONUtilsTest {

    @Test
    public void testDetectStringJSONType() {
        assertThat(MS_JSONUtils.detectStringJSONType(new MS_JSONObject().toString())).isEqualTo(JSONTypeEnum.OBJECT);
        assertThat(MS_JSONUtils.detectStringJSONType(new MS_JSONArray().toString())).isEqualTo(JSONTypeEnum.ARRAY);
        assertThat(MS_JSONUtils.detectStringJSONType("obfhofhjybep")).isEqualTo(JSONTypeEnum.STRING);
    }

    @Test
    public void isEmptyArray() {
        assertThat(MS_JSONUtils.isEmpty(null)).isTrue();
        assertThat(MS_JSONUtils.isEmpty(new MS_JSONArray())).isTrue();
        assertThat(MS_JSONUtils.isEmpty(new MS_JSONArray())).isTrue();
        assertThat(MS_JSONUtils.isEmpty(new MS_JSONArray().put(1))).isFalse();
    }

    @Test
    public void isOrgJsonObjectOrArray() {
        assertThat(MS_JSONUtils.isOrgJsonObject(new JSONObject())).isTrue();
        assertThat(MS_JSONUtils.isOrgJsonArray(new JSONArray())).isTrue();
        assertThat(MS_JSONUtils.isOrgJsonObject(new MS_JSONObject())).isFalse();
        assertThat(MS_JSONUtils.isOrgJsonArray(new MS_JSONArray().put(1))).isFalse();

        assertThat(MS_JSONUtils.isOrgJsonObject(new JSONArray())).isFalse();
        assertThat(MS_JSONUtils.isOrgJsonArray(new JSONObject())).isFalse();
        assertThat(MS_JSONUtils.isOrgJsonObject(new MS_JSONArray())).isFalse();
        assertThat(MS_JSONUtils.isOrgJsonArray(new MS_JSONObject())).isFalse();

        assertThat(MS_JSONUtils.isOrgJsonObject(new Object())).isFalse();
        assertThat(MS_JSONUtils.isOrgJsonArray(new Object())).isFalse();
    }

    @Test
    public void testResolveJSONPathNode() {
        assertThat(MS_JSONUtils.resolveJSONPathNode("jsonObjectFieldName")).isEqualTo(Pair.of("jsonObjectFieldName", null));
        assertThat(MS_JSONUtils.resolveJSONPathNode("[0]")).isEqualTo(Pair.of(null, 0));
        assertThat(MS_JSONUtils.resolveJSONPathNode("[1]")).isEqualTo(Pair.of(null, 1));
        assertThat(MS_JSONUtils.resolveJSONPathNode("[22]")).isEqualTo(Pair.of(null, 22));
        assertThat(MS_JSONUtils.resolveJSONPathNode("arrayFieldName[11]")).isEqualTo(Pair.of("arrayFieldName", 11));
    }

    @Test
    public void testGetJSONNode() {
        MS_JSONObject object = new MS_JSONObject()
                .put("obj", new MS_JSONObject().put("1", 1))
                .put("arr", new MS_JSONArray().put(new MS_JSONObject().put("2", 2)).put(new MS_JSONObject().put("3", 3)));
        assertThat(MS_JSONUtils.getJSONNode(object, "obj")).isEqualTo(new MS_JSONObject().put("1", 1));
        assertThat(MS_JSONUtils.getJSONNode(object, "arr[0]")).isEqualTo(new MS_JSONObject().put("2", 2));
        assertThat(MS_JSONUtils.getJSONNode(object, "arr[1]")).isEqualTo(new MS_JSONObject().put("3", 3));
        assertThatThrownBy(() -> MS_JSONUtils.getJSONNode(object, "lol[-1]")).isInstanceOf(JSONException.class);

        MS_JSONArray array = new MS_JSONArray()
                .put(new MS_JSONObject().put("11", 11))
                .put(new MS_JSONObject().put("12", 12))
                .put(new MS_JSONArray().put(13));
        assertThat(MS_JSONUtils.getJSONNode(array, "[0]")).isEqualTo(new MS_JSONObject().put("11", 11));
        assertThat(MS_JSONUtils.getJSONNode(array, "[1]")).isEqualTo(new MS_JSONObject().put("12", 12));
        assertThat(MS_JSONUtils.getJSONNode(array, "[2]")).isEqualTo(new MS_JSONArray().put(13));
    }
}
