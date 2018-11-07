package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.utilities.MS_DateTimeUtils;
import lv.emes.libraries.utilities.MS_JSONUtils;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Response of REST call made by {@link MS_HttpCallHandler}.
 *
 * @author maris.salenieks
 * @since 2.0.
 * @since 2.1.9
 */
public class MS_HttpResponse {

    private String url;
    private MS_HttpRequestMethod method;
    private Map<String, String> headers = new HashMap<>();
    private JSONObject body;
    private String bodyAsString;
    private Response response;
    private int statusCode;
    private Date timestamp;

    public MS_HttpResponse() {
        this.timestamp = new Date();
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

    MS_HttpResponse withBodyAsString(String bodyString) {
        this.bodyAsString = bodyString;
        return this;
    }

    MS_HttpResponse withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    MS_HttpResponse withBody(JSONObject body) {
        this.body = body;
        return this;
    }

    MS_HttpResponse withHeaders(Map<String, String> headers) {
        this.headers = headers;
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

    public String getBodyAsString() {
        return bodyAsString;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public JSONObject getBody() {
        if (body == null)
            throw new MS_BadSetupException("Response body is not JSON object; Try getBodyAsString()!");
        return body;
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
        JSONObject res = MS_JSONUtils.newOrderedJSONObject();
        res.putOpt("method", method != null ? method.name() : JSONObject.NULL);
        res.put("statusCode", statusCode);
        res.put("url", url);
        res.put("headers", MS_JSONUtils.mapToJSONObject(headers));
        res.put("body", body == null ? bodyAsString : body);
        res.put("timestamp", MS_DateTimeUtils.dateTimeToStr(timestamp, MS_DateTimeUtils._DATE_TIME_FORMAT_MILLISEC_ZONE_OFFSET));
        return res.toString();
    }
}
