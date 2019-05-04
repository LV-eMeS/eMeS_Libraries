package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_DTOMappingAlgorithm;
import lv.emes.libraries.communication.MS_DTOMappingHelper;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.utilities.MS_DateTimeUtils;

/**
 * DTA for logging events to be serialized / deserialized from JSON.
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.4
 */
public class MS_LoggingEventDTOAlgorithm extends MS_DTOMappingAlgorithm<MS_LoggingEvent, MS_JSONObject> {

    @Override
    public MS_JSONObject serialize(MS_LoggingEvent event) {
        return new MS_JSONObject()
                .put("time", MS_DateTimeUtils.formatDateTime(event.getTime(), MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET))
                .put("type", event.getType().name())
                .put("message", event.getMessage())
                .put("error", MS_DTOMappingHelper.serialize(event.getError(), ThrowableDTOAlgorithm.class))
                ;
    }

    @Override
    public MS_LoggingEvent deserialize(MS_JSONObject event) {
        return new MS_LoggingEvent()
                .withTime(MS_DateTimeUtils.formatDateTimeBackported(event.getString("time"), MS_DateTimeUtils._DATE_TIME_FORMAT_NANOSEC_ZONE_OFFSET))
                .withType(MS_LoggingEventTypeEnum.valueOf(event.getString("type")))
                .withMessage(event.getString("message"))
                //safe to cast, as long as it's serialized using this same algorithm
                .withError((Exception) MS_DTOMappingHelper.deserialize(event.optJSONObject("error"), ExceptionDTOAlgorithm.class))
                ;
    }
}
