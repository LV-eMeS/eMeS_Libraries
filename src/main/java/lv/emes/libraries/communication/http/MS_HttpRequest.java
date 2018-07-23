package lv.emes.libraries.communication.http;

import lv.emes.libraries.utilities.MS_JSONUtils;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP request object that acts as input to {@link MS_HttpCallHandler#call(MS_HttpRequest)} in order to make HTTP call.
 *
 * @author maris.salenieks
 * @version 1.0.
 */
public class MS_HttpRequest {

    private OkHttpClient clientConfigurations;
    private String url;
    private MS_HttpRequestMethod method;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String bodyAsString;
    private JSONObject body;
    private JSONArray bodyAsArray;

    //*** Setters ***

    public MS_HttpRequest withClientConfigurations(OkHttpClient clientConfigurations) {
        this.clientConfigurations = clientConfigurations;
        return this;
    }

    public MS_HttpRequest withMethod(MS_HttpRequestMethod method) {
        this.method = method;
        return this;
    }

    public MS_HttpRequest withUrl(String url) {
        this.url = url;
        return this;
    }

    public MS_HttpRequest withHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
        return this;
    }

    public MS_HttpRequest withParameter(String parameterName, String parameterValue) {
        parameters.put(parameterName, parameterValue);
        return this;
    }

    public MS_HttpRequest withBody(JSONObject body) {
        this.body = body;
        return this;
    }

    public MS_HttpRequest withBody(JSONArray bodyArray) {
        this.bodyAsArray = bodyArray;
        return this;
    }

    public MS_HttpRequest withBody(String body) {
        this.bodyAsString = body;
        return this;
    }

    public MS_HttpRequest withHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public MS_HttpRequest withParameters(Map<String, String> parameters) {
        this.parameters = parameters;
        return this;
    }

    public OkHttpClient getClientConfigurations() {
        return clientConfigurations;
    }

    //*** Getters ***

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public JSONObject getBody() {
        return body;
    }

    public JSONArray getBodyAsArray() {
        return bodyAsArray;
    }

    public String getBodyAsString() {
        String res = null;
        if (body != null) {
            res = body.toString();
        } else if (bodyAsArray != null) {
            res = bodyAsArray.toString();
        } else if (bodyAsString != null) {
            res = bodyAsString;
        }
        return res;
    }

    public MS_HttpRequestMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    //*** Equals, toString, hashCode ***

    @Override
    public String toString() {
        JSONObject res = MS_JSONUtils.newOrderedJSONObject();
        res.putOpt("method", method != null ? method.name() : JSONObject.NULL);
        res.put("url", url);
        res.put("parameters", MS_JSONUtils.mapToJSONObject(parameters));
        if (body != null) {
            res.put("body", body);
        } else if (bodyAsArray != null) {
            res.put("body", bodyAsArray);
        } else if (bodyAsString != null) {
            res.put("body", bodyAsString);
        } else {
            res.putOpt("body", JSONObject.NULL);
        }
        res.put("headers", MS_JSONUtils.mapToJSONObject(headers));
        return res.toString();
    }
}
