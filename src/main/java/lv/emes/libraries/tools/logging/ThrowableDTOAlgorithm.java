package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_DTOMappingAlgorithm;
import lv.emes.libraries.communication.MS_DTOMappingHelper;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;

import java.util.List;

/**
 * Algorithm for Throwable mapping.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.4.
 */
public class ThrowableDTOAlgorithm extends MS_DTOMappingAlgorithm<Throwable, MS_JSONObject> {

    @Override
    public MS_JSONObject serialize(Throwable error) {
        MS_JSONArray stackTrace;
        if (error.getStackTrace() == null) {
            stackTrace = null;
        } else {
            stackTrace = new MS_JSONArray();
            for (StackTraceElement stackTraceElement : error.getStackTrace()) {
                stackTrace.put(MS_DTOMappingHelper.serialize(stackTraceElement, StackTraceElementDTOAlgorithm.class));
            }
        }
        return new MS_JSONObject()
                .put("name", error.getClass().getName())
                .put("message", error.getMessage())
                .put("cause", MS_DTOMappingHelper.serialize(error.getCause(), ThrowableDTOAlgorithm.class))
                .put("stackTrace", stackTrace)
                ;
    }

    @Override
    public Throwable deserialize(MS_JSONObject error) {
        Throwable res = newThrowable(error.optString("message", null), MS_DTOMappingHelper.deserialize(error.optJSONObject("cause"), ThrowableDTOAlgorithm.class));

        if (error.isNotNull("stackTrace")) {
            MS_JSONArray stackTrace = error.getJSONArray("stackTrace");
            List<StackTraceElement> listOfTraces = MS_DTOMappingHelper.deserializeList(stackTrace.toJSONObjectList(), StackTraceElementDTOAlgorithm.class);
            res.setStackTrace(listOfTraces.toArray(new StackTraceElement[0]));
        }
        return res;
    }

    protected Throwable newThrowable(String message, Throwable cause) {
        return new Throwable(message, cause);
    }
}
