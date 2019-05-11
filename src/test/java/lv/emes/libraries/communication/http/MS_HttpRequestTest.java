package lv.emes.libraries.communication.http;

import com.google.common.collect.ImmutableMap;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class MS_HttpRequestTest {

    private static final String HTTP_GOOGLE_COM = "http://google.com";

    private MS_HttpRequest req = new MS_HttpRequest()
            .withUrl(HTTP_GOOGLE_COM)
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

    @Test
    public void testUrlQueryParameterSetting() {
        MS_HttpRequest req;
        req = new MS_HttpRequest().withUrlQueryParameters(ImmutableMap.of("keyA", "valueA", "keyB", "valueB"));
        assertThat(req.getUrlQueryParameters()).hasSize(2).containsExactly(
                MapEntry.entry("keyA", Collections.singletonList("valueA")), MapEntry.entry("keyB", Collections.singletonList("valueB"))
        );
        assertThat(MS_HttpUtils.formMultiUrlQueryParams(req.getUrlQueryParameters())).isEqualTo("?keyA=valueA&keyB=valueB");

        req = new MS_HttpRequest()
                .withUrlQueryParameter(Pair.of("keyA", "valueA1"))
                .withUrlQueryParameter(Pair.of("keyB", "valueB1"))
                .withUrlQueryParameter("keyA", "valueA2");
        assertThat(req.getUrlQueryParameters()).hasSize(2).containsExactly(
                MapEntry.entry("keyA", Arrays.asList("valueA1", "valueA2")), MapEntry.entry("keyB", Collections.singletonList("valueB1"))
        );
        assertThat(MS_HttpUtils.formMultiUrlQueryParams(req.getUrlQueryParameters())).isEqualTo("?keyA=valueA1&keyA=valueA2&keyB=valueB1");

        req = new MS_HttpRequest().withUrlQueryMultiParameters(ImmutableMap.<String, List<String>>builder()
                .put("keyA", Arrays.asList("valueA1", "valueA2", "valueA1"))
                .put("keyB", Collections.emptyList())
                .build());
        assertThat(req.getUrlQueryParameters()).hasSize(2).containsExactly(
                MapEntry.entry("keyA", Arrays.asList("valueA1", "valueA2", "valueA1")), MapEntry.entry("keyB", Collections.emptyList())
        );
        assertThat(MS_HttpUtils.formMultiUrlQueryParams(req.getUrlQueryParameters())).isEqualTo("?keyA=valueA1&keyA=valueA2&keyA=valueA1");
    }

    @Test
    public void testGetUrl() {
        MS_HttpRequest req = new MS_HttpRequest();
        assertThat(req.getUrl()).isNull();
        assertThat(req.withUrl(HTTP_GOOGLE_COM).withUrlQueryParameter("id", "12345").getUrl())
                .isEqualTo(HTTP_GOOGLE_COM + "?id=12345");
    }
}