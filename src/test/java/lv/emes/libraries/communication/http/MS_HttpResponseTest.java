package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.json.JSONTypeEnum;
import lv.emes.libraries.tools.json.MS_JSONObject;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MS_HttpResponseTest {

    @Test
    public void testToString() {
        MS_HttpResponse response = new MS_HttpResponse()
                .withMethod(MS_HttpRequestMethod.GET)
                .withResponseCode(200)
                .withUrl("http://test.url")
                .withHeader("SessionId", "value");
        response.initJSONBody("Some response data (not necessarily JSON) here");

        MS_JSONObject expected = new MS_JSONObject();
        expected.put("method", response.getMethod().name());
        expected.put("statusCode", response.getStatusCode());
        expected.putOpt("url", response.getUrl());
        expected.put("headers", new MS_JSONObject(response.getHeaders()));
        expected.putOpt("bodyType", JSONTypeEnum.STRING.name());
        expected.putOpt("body", response.getBodyString());
        expected.put("timestamp", MS_DateTimeUtils.dateTimeToStr(response.getTimestamp(), MS_DateTimeUtils._DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET));
        assertThat(response.toString()).isEqualTo(expected.toString());
    }

    @Test
    public void testBodyNotJson() {
        assertThatThrownBy(() -> new MS_HttpResponse().getBodyObject()).isInstanceOf(MS_BadSetupException.class);
        assertThatThrownBy(() -> new MS_HttpResponse().getBodyArray()).isInstanceOf(MS_BadSetupException.class);
    }
}