package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_DTOMappingAlgorithm;
import lv.emes.libraries.communication.json.MS_JSONObject;

/**
 * Algorithm for Stack trace element mapping.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.4.
 */
public class StackTraceElementDTOAlgorithm extends MS_DTOMappingAlgorithm<StackTraceElement, MS_JSONObject> {

    @Override
    public MS_JSONObject serialize(StackTraceElement objectToSerialize) {
        return new MS_JSONObject()
                .put("declaringClass", objectToSerialize.getClassName())
                .put("methodName", objectToSerialize.getMethodName())
                .put("fileName", objectToSerialize.getFileName())
                .put("lineNumber", objectToSerialize.getLineNumber())
                ;
    }

    @Override
    public StackTraceElement deserialize(MS_JSONObject serializedObject) {
        return new StackTraceElement(serializedObject.getString("declaringClass"),
                serializedObject.getString("methodName"),
                serializedObject.optString("fileName", null),
                serializedObject.getInt("lineNumber")
        );
    }
}
