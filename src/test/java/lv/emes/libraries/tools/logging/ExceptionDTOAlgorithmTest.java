package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_DTOMappingHelper;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExceptionDTOAlgorithmTest {

    @Test
    public void testExceptionSerializationAndBack() {
        final String CAUSE_MSG = "It's my fault";
        Exception real;
        Throwable cause = new MS_TestUtils.MS_UnCheckedException1(CAUSE_MSG);
        try {
            throw new MS_TestUtils.MS_CheckedException(null, cause);
        } catch (Exception e) {
            real = e;
        }
        MS_JSONObject serialized = MS_DTOMappingHelper.serialize(real, ExceptionDTOAlgorithm.class);
        assertThat(serialized.getString("name")).isEqualTo(real.getClass().getName());
        assertThat(serialized.isNull("message")).isTrue();
        assertThat(serialized.isJsonArray("stackTrace")).isTrue();
        assertThat(serialized.getJSONArray("stackTrace").length()).isEqualTo(real.getStackTrace().length);
        assertThat(serialized.isJsonObject("cause")).isTrue();

        MS_JSONObject causeJson = serialized.getJSONObject("cause");
        assertThat(causeJson.getString("name")).isEqualTo(cause.getClass().getName());
        assertThat(causeJson.getString("message")).isEqualTo(CAUSE_MSG);
        assertThat(causeJson.isJsonArray("stackTrace")).isTrue();
        assertThat(causeJson.getJSONArray("stackTrace").length()).isEqualTo(cause.getStackTrace().length);
        assertThat(causeJson.isNull("cause")).isTrue();

        // Now deserialize it back to Exception
        Exception deserialized = (Exception) MS_DTOMappingHelper.deserialize(serialized, ExceptionDTOAlgorithm.class);
        assertThat(deserialized.getMessage()).isNull();
        assertThat(deserialized.getStackTrace()).isEqualTo(real.getStackTrace());

        Throwable deserializedCause = deserialized.getCause();
        assertThat(deserializedCause.getMessage()).isEqualTo(CAUSE_MSG);
        assertThat(deserializedCause.getClass().getName()).isEqualTo("java.lang.Throwable");
        assertThat(deserializedCause.getStackTrace()).isEqualTo(cause.getStackTrace());
        assertThat(deserializedCause.getCause()).isNull();
    }
}