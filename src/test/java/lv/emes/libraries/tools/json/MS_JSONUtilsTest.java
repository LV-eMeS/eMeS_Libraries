package lv.emes.libraries.tools.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
