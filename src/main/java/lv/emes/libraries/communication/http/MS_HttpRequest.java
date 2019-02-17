package lv.emes.libraries.communication.http;

import lv.emes.libraries.tools.json.MS_JSONArray;
import lv.emes.libraries.tools.json.MS_JSONObject;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP request object that acts as input to {@link MS_HttpCallHandler#call(MS_HttpRequest)} in order to make HTTP call.
 *
 * @author maris.salenieks
 * @version 2.1.
 * @since 2.1.9
 */
public class MS_HttpRequest {

    private OkHttpClient clientConfigurations;
    private String url;
    private MS_HttpRequestMethod method;
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, String> parameters = new HashMap<>();
    private String bodyAsString;
    private MS_JSONObject body;
    private MS_JSONArray bodyAsArray;

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

    public MS_HttpRequest withHeader(String headerName, List<String> headerValues) {
        headers.put(headerName, headerValues);
        return this;
    }

    public MS_HttpRequest withParameter(String parameterName, String parameterValue) {
        parameters.put(parameterName, parameterValue);
        return this;
    }

    public MS_HttpRequest withBody(MS_JSONObject body) {
        this.body = body;
        return this;
    }

    public MS_HttpRequest withBody(MS_JSONArray bodyArray) {
        this.bodyAsArray = bodyArray;
        return this;
    }

    public MS_HttpRequest withBody(String body) {
        this.bodyAsString = body;
        return this;
    }

    public MS_HttpRequest withHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public MS_HttpRequest withHeaders(String headerName, List<String> headerValues) {
        headers.put(headerName, headerValues);
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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public MS_JSONObject getBody() {
        return body;
    }

    public MS_JSONArray getBodyAsArray() {
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
        MS_JSONObject res = new MS_JSONObject();
        res.putOpt("method", method != null ? method.name() : MS_JSONObject.NULL);
        res.put("url", url);
        res.put("parameters", new MS_JSONObject(parameters));
        if (body != null) {
            res.put("body", body);
        } else if (bodyAsArray != null) {
            res.put("body", bodyAsArray);
        } else if (bodyAsString != null) {
            res.put("body", bodyAsString);
        } else {
            res.putOpt("body", MS_JSONObject.NULL);
        }
        res.put("headers", new MS_JSONObject(headers));
        return res.toString();
    }
}
