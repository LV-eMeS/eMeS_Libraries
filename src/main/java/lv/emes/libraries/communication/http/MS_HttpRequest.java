package lv.emes.libraries.communication.http;

import lv.emes.libraries.communication.json.MS_JSONArray;
import lv.emes.libraries.communication.json.MS_JSONObject;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * HTTP request object that acts as input to {@link MS_HttpCallHandler#call(MS_HttpRequest)} in order to make HTTP call.
 *
 * @author eMeS
 * @version 3.0.
 * @since 2.1.9
 */
public class MS_HttpRequest {

    private OkHttpClient clientConfigurations;
    private String url;
    private MS_HttpRequestMethod method;
    private Map<String, List<String>> headers = new LinkedHashMap<>();
    private Map<String, String> parameters = new LinkedHashMap<>();
    private Map<String, List<String>> urlQueryParameters = new LinkedHashMap<>();
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
            headerValues = new ArrayList<>(5);
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

    public MS_HttpRequest withUrlQueryParameters(Map<String, String> parameters) {
        Objects.requireNonNull(parameters);
        this.urlQueryParameters.clear();
        parameters.forEach((key, value) -> this.urlQueryParameters.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value));
        return this;
    }

    public MS_HttpRequest withUrlQueryMultiParameters(Map<String, List<String>> parameters) {
        this.urlQueryParameters = Objects.requireNonNull(parameters);
        return this;
    }

    public MS_HttpRequest withUrlQueryParameter(Pair<String, String> parameter) {
        this.urlQueryParameters.computeIfAbsent(parameter.getLeft(), (key) -> new ArrayList<>()).add(parameter.getRight());
        return this;
    }

    public MS_HttpRequest withUrlQueryParameter(String parameterKey, String parameterValue) {
        this.urlQueryParameters.computeIfAbsent(parameterKey, (key) -> new ArrayList<>()).add(parameterValue);
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

    public Map<String, List<String>> getUrlQueryParameters() {
        return urlQueryParameters;
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
        return url == null ? null : url + MS_HttpUtils.formMultiUrlQueryParams(getUrlQueryParameters());
    }

    public String getRawUrl() {
        return url;
    }

    //*** Equals, toString, hashCode ***


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MS_HttpRequest that = (MS_HttpRequest) o;

        return new EqualsBuilder()
                .append(clientConfigurations, that.clientConfigurations)
                .append(getUrl(), that.getUrl())
                .append(method, that.method)
                .append(headers, that.headers)
                .append(parameters, that.parameters)
                .append(getBodyAsString(), that.getBodyAsString())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(clientConfigurations)
                .append(url)
                .append(method)
                .append(headers)
                .append(parameters)
                .append(urlQueryParameters)
                .append(bodyAsString)
                .append(body)
                .append(bodyAsArray)
                .toHashCode();
    }

    @Override
    public String toString() {
        MS_JSONObject res = new MS_JSONObject();
        res.putOpt("method", method != null ? method.name() : MS_JSONObject.NULL);
        res.put("url", getUrl());
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
