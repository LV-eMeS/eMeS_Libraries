package lv.emes.libraries.communication.json;

import org.assertj.core.api.SoftAssertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_JSONTest {

    @Test
    public void testGetNestedValidationErrors() {
        MS_JSON json = new MS_JSONObject();
        assertThatThrownBy(() -> json.getNested(null, null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> json.getNested("", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testGetNestedInJsonObject() {
        MS_JSON json = new MS_JSONObject()
                .put("obj", new MS_JSONObject().put("1", 1))
                .put("arr", new MS_JSONArray()
                        .put(new MS_JSONObject().put("2", 2))
                        .put(new MS_JSONObject()
                                .put("3", 3)
                                .put("4", 4)
                                .put("5", new MS_JSONArray().put(5))
                                .put("6", new MS_JSONObject().put("66", 66))
                        )
                );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(json.getNested("obj.1", Integer.class)).isEqualTo(1);
            softly.assertThat(json.getNested("arr[0].2", Integer.class)).isEqualTo(2);
            softly.assertThat(json.getNested("arr[1].3", Integer.class)).isEqualTo(3);
            softly.assertThat(json.getNested("arr[1].4", Integer.class)).isEqualTo(4);
            softly.assertThat(json.getNested("arr[1].5[0]", Integer.class)).isEqualTo(5);
            softly.assertThat(json.getNested("arr[1].6.66", Integer.class)).isEqualTo(66);
        });
    }

    @Test
    public void testGetNestedInJsonArray() {
        MS_JSON json = new MS_JSONArray()
                .put(1)
                .put(new MS_JSONObject().put("11", 11))
                .put(new MS_JSONObject().put("12", 12))
                .put(new MS_JSONArray().put(13).put(new MS_JSONObject().put("14", 14)));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(json.getNested("[0]", Integer.class)).isEqualTo(1);
            softly.assertThat(json.getNested("[1]", MS_JSONObject.class)).isEqualTo(new MS_JSONObject().put("11", 11));
            softly.assertThat(json.getNested("[1].11", Integer.class)).isEqualTo(11);
            softly.assertThat(json.getNested("[2].12", Integer.class)).isEqualTo(12);
            softly.assertThat(json.getNested("[3].[0]", Integer.class)).isEqualTo(13);
            softly.assertThat(json.getNested("[3].[1]", MS_JSONObject.class)).isEqualTo(new MS_JSONObject().put("14", 14));
            softly.assertThat(json.getNested("[3].[1].14", Integer.class)).isEqualTo(14);
        });
    }

    @Test
    public void testGetJsonPathNestedJSONObject() {
        MS_JSONObject objectWithNestedObjectAndArray = new MS_JSONObject()
                .put("obj", new JSONObject()
                        .put("key", "abc")
                        .put("arrOfObj", new MS_JSONArray()
                                .put("efg")
                                .put(new JSONObject().put("test", "value"))
                        )
                )
                .put("arr", new MS_JSONArray().put(1).put(2).put(3).put(new JSONObject().put("key", "value")))
                .put("primitive", true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(objectWithNestedObjectAndArray.getNested("obj.key", String.class)).isEqualTo("abc");
            softly.assertThat(objectWithNestedObjectAndArray.getNested("obj.arrOfObj", MS_JSONArray.class))
                    .isEqualTo(new MS_JSONArray()
                            .put("efg")
                            .put(new MS_JSONObject().put("test", "value"))
                    );
            softly.assertThat(objectWithNestedObjectAndArray.getNested("obj.arrOfObj[0]", String.class))
                    .isEqualTo("efg");
            softly.assertThat(objectWithNestedObjectAndArray.getNested("obj.arrOfObj[1]", MS_JSONObject.class))
                    .isEqualTo(new MS_JSONObject().put("test", "value"));
            softly.assertThat(objectWithNestedObjectAndArray.getNested("obj.arrOfObj[1].test", String.class))
                    .isEqualTo("value");

            softly.assertThat(objectWithNestedObjectAndArray.getNested("arr[0]", Integer.class)).isEqualTo(1);
            softly.assertThat(objectWithNestedObjectAndArray.getNested("arr[1]", Integer.class)).isEqualTo(2);
            softly.assertThat(objectWithNestedObjectAndArray.getNested("arr[2]", Integer.class)).isEqualTo(3);
            softly.assertThat(objectWithNestedObjectAndArray.getNested("arr[3].key", String.class)).isEqualTo("value");

            // Erroneous cases
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested("primitive", null))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested(null, Integer.class))
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested("primitive", Integer.class))
                    .isInstanceOf(ClassCastException.class);
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested("primitive.imaginaryBoolHere", Boolean.class))
                    .isInstanceOf(JSONException.class)
                    .hasMessage("MS_JSONObject[\"primitive\"] is not a MS_JSON.");
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested("primitive[3].imaginaryDoubleHere", Double.class))
                    .isInstanceOf(JSONException.class)
                    .hasMessage("MS_JSONObject[\"primitive\"] is not a MS_JSONArray.");
            softly.assertThatThrownBy(() -> objectWithNestedObjectAndArray.getNested("arr[55]", Double.class))
                    .isInstanceOf(JSONException.class)
                    .hasMessage("JSONArray[55] not found.");

        });
    }
}