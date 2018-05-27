package lv.emes.libraries.communication.http;

import lv.emes.libraries.file_system.MS_BinaryTools;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class can be used while working with HTTP requests.
 * Under the hood it's using
 * <a href="http://hc.apache.org/httpcomponents-client-4.3.x/quickstart.html">Apache HTTP components</a>.
 * <p>Good examples of Apache HTTP components use are provided by
 * <a href="http://www.baeldung.com/httpclient-guide">Baeldung</a>, but this class is something different.
 * <p>Static methods:
 * <ul>
 * <li>get</li>
 * <li>post</li>
 * <li>put</li>
 * <li>delete</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.2.
 */
public class MS_HttpClient {

    /**
     * Encoding of entity parameter names and values.
     */
    public static final String ENCODING = "UTF-8";

    /**
     * Does HTTP "GET" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param headers    map of key-value headers to pass for this request.
     * @param connConfig initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                   For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult get(String requestURL, Map<String, String> params,
                                           Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> {
            String url = formatRequestUrl(requestURL);
            if (params != null)
                url += "?" + formParamURL(params);
            return new HttpGet(url);
        };
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "GET" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param headers    map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult get(String requestURL, Map<String, String> params, Map<String, String> headers) {
        return get(requestURL, params, headers, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "GET" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult get(String requestURL, Map<String, String> params) {
        return get(requestURL, params, null, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "POST" request.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data as whole entity of request body.
     * @param headers     map of key-value headers to pass for this request.
     * @param connConfig  initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                    For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult post(String requestURL, HttpEntity requestBody, Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> {
            HttpPost request = new HttpPost(formatRequestUrl(requestURL));
            request.setEntity(requestBody);
            return request;
        };
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "POST" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param headers    map of key-value headers to pass for this request.
     * @param connConfig initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                   For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult post(String requestURL, Map<String, String> params, Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> {
            HttpPost request = new HttpPost(formatRequestUrl(requestURL));
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                params.forEach((name, value) -> paramList.add(new BasicNameValuePair(name, value)));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, ENCODING);
                request.setEntity(entity);
            }
            return request;
        };
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "POST" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param headers    map of key-value headers to pass for this request.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult post(String requestURL, Map<String, String> params, Map<String, String> headers) {
        return post(requestURL, params, headers, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "PUT" request.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data as whole entity of request body.
     * @param headers     map of key-value headers to pass for this request.
     * @param connConfig  initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                    For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, HttpEntity requestBody,
                                           Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> {
            HttpPut request = new HttpPut(formatRequestUrl(requestURL));
            request.setEntity(requestBody);
            return request;
        };
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "PUT" request.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data to be sent by request.
     * @param headers     map of key-value headers to pass for this request.
     * @param connConfig  initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                    For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, String requestBody,
                                           Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> {
            HttpPut request = new HttpPut(formatRequestUrl(requestURL));
            if (requestBody != null) {
                StringEntity entity = new StringEntity(requestBody, ENCODING);
                request.setEntity(entity);
            }
            return request;
        };
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "PUT" request.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data to be sent by request.
     * @param connConfig  initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                    For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, String requestBody, RequestConfig connConfig) {
        return put(requestURL, requestBody, null, connConfig);
    }

    /**
     * Does HTTP "PUT" request.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data to be sent by request.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, String requestBody) {
        return put(requestURL, requestBody, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "DELETE" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param headers    map of key-value headers to pass for this request.
     * @param connConfig initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                   For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, Map<String, String> headers, RequestConfig connConfig) {
        IFuncHttpRequestCreator newRequest = () -> new HttpDelete(formatRequestUrl(requestURL));
        return httpRequest(newRequest, headers, connConfig);
    }

    /**
     * Does HTTP "DELETE" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param headers    map of key-value headers to pass for this request.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, Map<String, String> headers) {
        return delete(requestURL, headers, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "DELETE" request.
     *
     * @param requestURL an URL to HTTP server.
     * @param connConfig initial configuration of connection. For most of the cases that's enough just to set timeouts.
     *                   For that {@link MS_HTTPConnectionConfigurations#DEFAULT_CONFIG_FOR_CONNECTION} can be used as well.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, RequestConfig connConfig) {
        return delete(requestURL, null, connConfig);
    }

    /**
     * Does HTTP "DELETE" request.
     *
     * @param requestURL an URL to HTTP server.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL) {
        return delete(requestURL, MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    //*** PRIVATE METHODS ***

    /**
     * In given URL replaces all spaces (if any) with "%20" symbols in order to correctly send it using HTTP components.
     *
     * @param url full request URL.
     * @return same URL, just formatted in correct format.
     */
    private static String formatRequestUrl(String url) {
        return MS_StringUtils.replaceInString(url, " ", "%20");
    }

    private static String formParamURL(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null) //if no parameters are assigned then there is no need to build parameters as string
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), ENCODING));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), ENCODING));
        }

        return result.toString();
    }

    private static MS_HttpRequestResult httpRequest(IFuncHttpRequestCreator requestAndParameters,
                                                    Map<String, String> headers, RequestConfig connConfig) {
        MS_HttpRequestResult res = new MS_HttpRequestResult();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(connConfig).build()) {
            HttpRequestBase request = requestAndParameters.createRequestAndSetParameters();

            if (headers != null) headers.forEach((request::addHeader));

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                res.reponseCode = response.getStatusLine().getStatusCode();
                for (Header h : response.getAllHeaders()) //save response headers
                    res.getHeaders().put(h.getName(), h.getValue());
                // Get hold of the response entity and process it
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    res.message = MS_BinaryTools.inputToUTF8(entity.getContent());
                    EntityUtils.consume(entity);
                }
            }
        } catch (SocketTimeoutException | HttpHostConnectException e) {
            res.message = e.getMessage() != null ? e.getMessage() : "";
            res.exception = e;
            res.reponseCode = 504;
        } catch (IOException ioex) {
            res.exception = ioex;
            res.reponseCode = 0;
        }
        return res;
    }

    @FunctionalInterface
    private interface IFuncHttpRequestCreator {
        HttpRequestBase createRequestAndSetParameters() throws UnsupportedEncodingException;
    }

    private MS_HttpClient() {
    }
}
