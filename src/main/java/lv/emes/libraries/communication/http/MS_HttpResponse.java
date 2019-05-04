package lv.emes.libraries.communication.http;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lv.emes.libraries.communication.json.JSONTypeEnum;
import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.communication.json.MS_JSONUtils;
import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import okhttp3.Response;

import java.util.*;

/**
 * Immutable response of REST call made by {@link MS_HttpCallHandler}.
 *
 * @author maris.salenieks
 * @version 2.1.
 * @since 2.1.9
 */
@Immutable
public class MS_HttpResponse {

    private String url;
    private MS_HttpRequestMethod method;
    private Map<String, List<String>> headers = new HashMap<>();
    private String bodyString;
    private MS_JSONObject bodyObject;
    private MS_JSONArray bodyArray;
    private Response response;
    private int statusCode;
    private Date timestamp;
    private JSONTypeEnum bodyJsonType = JSONTypeEnum.STRING;

    public MS_HttpResponse() {
        this.timestamp = new Date();
    }

    /**
     * Initializes <b>bodyString</b>, <b>bodyObject</b> - if <b>body</b> is a JSON object or
     * <b>bodyArray</b> - if <b>body</b> is a JSON array.
     * Unset object values remain <tt>null</tt>.
     * <b>bodyString</b> will be always set, unless given string is <tt>null</tt>.
     *
     * @param body HTTP response body as string.
     */
    public void initJSONBody(String body) {
        this.bodyString = body;
        this.bodyJsonType = MS_JSONUtils.detectStringJSONType(body);
        switch (bodyJsonType) {
            case OBJECT:
                this.bodyObject = new MS_JSONObject(body);
                break;
            case ARRAY:
                this.bodyArray = new MS_JSONArray(body);
                break;
            default:
                this.bodyString = body;
        }
    }

    //*** Setters and getters

    MS_HttpResponse withMethod(MS_HttpRequestMethod method) {
        this.method = method;
        return this;
    }

    MS_HttpResponse withUrl(String url) {
        this.url = url;
        return this;
    }

    MS_HttpResponse withHeader(String headerName, String headerValue) {
        List<String> headerValues = headers.get(headerName);
        if (headerValues == null) {
            headerValues = new ArrayList<>(1);
            headerValues.add(headerValue);
            headers.put(headerName, headerValues);
        } else {
            headerValues.add(headerValue);
        }
        return this;
    }

    MS_HttpResponse withHeader(String headerName, List<String> headerValues) {
        headers.put(headerName, headerValues);
        return this;
    }

    MS_HttpResponse withHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    MS_HttpResponse withHeaders(String headerName, List<String> headerValues) {
        headers.put(headerName, headerValues);
        return this;
    }

    MS_HttpResponse withResponse(Response response) {
        this.response = response;
        return this;
    }

    MS_HttpResponse withResponseCode(int responseCode) {
        this.statusCode = responseCode;
        return this;
    }

    public MS_HttpRequestMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getBodyString() {
        return bodyString;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public MS_JSONObject getBodyObject() {
        if (bodyObject == null)
            throw new MS_BadSetupException("Response body is not JSON object; Try getBodyString() or getBodyArray()!");
        return bodyObject;
    }

    public MS_JSONArray getBodyArray() {
        if (bodyArray == null)
            throw new MS_BadSetupException("Response body is not JSON array; Try getBodyString() or getBodyObject()!");
        return bodyArray;
    }

    public JSONTypeEnum getBodyJsonType() {
        return bodyJsonType;
    }

    public Response getResponse() {
        return response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        MS_JSONObject res = new MS_JSONObject();
        res.putOpt("method", method != null ? method.name() : MS_JSONObject.NULL);
        res.put("statusCode", statusCode);
        res.put("url", url);
        res.put("headers", new MS_JSONObject(headers));
        res.putOpt("bodyType", bodyJsonType != null ? bodyJsonType.name() : MS_JSONObject.NULL);
        res.put("body", bodyString);
        res.put("timestamp", MS_DateTimeUtils.dateTimeToStr(timestamp, MS_DateTimeUtils._DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET));
        return res.toString();
    }
}
