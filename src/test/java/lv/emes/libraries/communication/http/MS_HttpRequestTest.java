package lv.emes.libraries.communication.http;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MS_HttpRequestTest {

    private MS_HttpRequest req = new MS_HttpRequest()
            .withUrl("http://google.com")
            .withMethod(MS_HttpRequestMethod.GET)
            .withParameter("env", "TEST")
            .withParameter("version", "3")
            .withHeader("header", "X")
            .withHeader("header", "Y");

    @Test
    public void testToString() {
        MS_HttpRequest request = req.withBody(new MS_JSONObject("{\"data\":\"bae08ddc-5001-453d-9d95-a124a352e152\"}"));

        MS_JSONObject expected = new MS_JSONObject();
        expected.put("method", request.getMethod().name());
        expected.put("url", request.getUrl());
        expected.put("parameters", new MS_JSONObject(request.getParameters()));
        expected.putOpt("body", request.getBody());
        expected.put("headers", new MS_JSONObject(request.getHeaders()));
        assertEquals(expected.toString(), request.toString());
    }

    @Test
    public void testToStringWhenBodyIsArray() {
        MS_JSONArray body = new MS_JSONArray();
        MS_JSONObject data = new MS_JSONObject();
        data.put("key", "value");
        body.put(data);
        req.withBody(body);

        MS_JSONObject expected = new MS_JSONObject();
        expected.put("method", req.getMethod().name());
        expected.put("url", req.getUrl());
        expected.put("parameters", new MS_JSONObject(req.getParameters()));
        expected.put("body", req.getBodyAsArray());
        expected.put("headers", new MS_JSONObject(req.getHeaders()));
        assertEquals(expected.toString(), req.toString());
    }

    @Test
    public void testToStringWhenBodyIsString() {
        String body = "Some request data as string";
        req.withBody(body);

        MS_JSONObject expected = new MS_JSONObject();
        expected.put("method", req.getMethod().name());
        expected.put("url", req.getUrl());
        expected.put("parameters", new MS_JSONObject(req.getParameters()));
        expected.put("body", req.getBodyAsString());
        expected.put("headers", new MS_JSONObject(req.getHeaders()));
        assertEquals(expected.toString(), req.toString());
    }
}