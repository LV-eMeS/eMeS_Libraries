package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_JSONUtils;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MS_HttpResponseTest {

    @Test
    public void testToString() {
        MS_HttpResponse response = new MS_HttpResponse()
                .withMethod(MS_HttpRequestMethod.GET)
                .withResponseCode(200)
                .withHeader("SessionId", "value")
                .withBodyAsString("Some response data (not necessarily JSON) here");

        JSONObject expected = MS_JSONUtils.newOrderedJSONObject();
        expected.put("method", response.getMethod().name());
        expected.put("statusCode", response.getStatusCode());
        expected.put("headers", MS_JSONUtils.mapToJSONObject(response.getHeaders()));
        expected.putOpt("body", response.getBodyAsString());
        expected.put("timestamp", MS_DateTimeUtils.dateTimeToStr(response.getTimestamp(), MS_DateTimeUtils._DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET));
        assertEquals(expected.toString(), response.toString());
    }

    @Test(expected = MS_BadSetupException.class)
    public void testBodyNotJson() {
        new MS_HttpResponse().getBody();
    }
}