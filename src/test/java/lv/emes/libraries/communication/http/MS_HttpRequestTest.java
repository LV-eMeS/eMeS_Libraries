package lv.emes.libraries.communication.http;

import lv.emes.libraries.utilities.MS_JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MS_HttpRequestTest {

    private MS_HttpRequest req = new MS_HttpRequest()
            .withUrl("http://google.com")
            .withMethod(MS_HttpRequestMethod.GET)
            .withParameter("env", "TEST")
            .withParameter("version", "3");

    @Test
    public void testToString() {
        MS_HttpRequest request = req.withBody(new JSONObject("{\"data\":\"bae08ddc-5001-453d-9d95-a124a352e152\"}"));

        JSONObject expected = MS_JSONUtils.newOrderedJSONObject();
        expected.put("method", request.getMethod().name());
        expected.put("url", request.getUrl());
        expected.put("parameters", MS_JSONUtils.mapToJSONObject(request.getParameters()));
        expected.putOpt("body", request.getBody());
        expected.put("headers", MS_JSONUtils.mapToJSONObject(request.getHeaders()));
        assertEquals(expected.toString(), request.toString());
    }

    @Test
    public void testToStringWhenBodyIsArray() {
        JSONArray body = new JSONArray();
        JSONObject data = new JSONObject();
        data.put("key", "value");
        body.put(data);
        req.withBody(body);

        JSONObject expected = MS_JSONUtils.newOrderedJSONObject();
        expected.put("method", req.getMethod().name());
        expected.put("url", req.getUrl());
        expected.put("parameters", MS_JSONUtils.mapToJSONObject(req.getParameters()));
        expected.put("body", req.getBodyAsArray());
        expected.put("headers", MS_JSONUtils.mapToJSONObject(req.getHeaders()));
        assertEquals(expected.toString(), req.toString());
    }

    @Test
    public void testToStringWhenBodyIsString() {
        String body = "Some request data as string";
        req.withBody(body);

        JSONObject expected = MS_JSONUtils.newOrderedJSONObject();
        expected.put("method", req.getMethod().name());
        expected.put("url", req.getUrl());
        expected.put("parameters", MS_JSONUtils.mapToJSONObject(req.getParameters()));
        expected.put("body", req.getBodyAsString());
        expected.put("headers", MS_JSONUtils.mapToJSONObject(req.getHeaders()));
        assertEquals(expected.toString(), req.toString());
    }
}