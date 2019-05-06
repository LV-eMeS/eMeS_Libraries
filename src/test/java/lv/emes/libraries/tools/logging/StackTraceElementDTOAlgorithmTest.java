package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.json.MS_JSONObject;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StackTraceElementDTOAlgorithmTest {

    @Test
    public void testSerializeAndDeserialize() {
        StackTraceElementDTOAlgorithm algo = new StackTraceElementDTOAlgorithm();
        StackTraceElement trace = new StackTraceElement("class", "method", null, -2);
        MS_JSONObject serialized = algo.serialize(trace);
        assertThat(serialized).isEqualTo(new MS_JSONObject()
                .put("declaringClass", "class")
                .put("methodName", "method")
                .put("fileName", JSONObject.NULL)
                .put("lineNumber", -2)
        );
        assertThat(algo.deserialize(serialized)).isEqualTo(trace);
    }
}