package lv.emes.libraries.communication.http;

import lv.emes.libraries.file_system.MS_BinaryTools;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Inspired from <a href="http://stackoverflow.com/questions/2938502/sending-post-data-in-android">Stackoverflow</a>.
 * This class can be used while working with HTTP requests.
 * <p>Static methods:
 * <ul>
 * <li>get</li>
 * <li>post</li>
 * <li>put</li>
 * <li>delete</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_HttpClient {

    /**
     * Does HTTP "GET" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param connConfig initial configuration of connection.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult get(String requestURL, Map<String, String> params, MS_IFuncConnectionConfig connConfig) {
        URL url;
        MS_HttpRequestResult res = new MS_HttpRequestResult();
        try {
            if (params != null)
                requestURL += "?" + formParamURL(params);
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connConfig.initializeConnection(conn);
            res.connection = conn;
            res.reponseCode = conn.getResponseCode();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            res.message = MS_BinaryTools.inputToUTF8(in);
        } catch (IOException e) {
            res.message = "";
            res.exception = e;
        }
        return res;
    }

    /**
     * Does HTTP "GET" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult get(String requestURL, Map<String, String> params) {
        return get(requestURL, params, MS_IFuncConnectionConfigDefaults.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "POST" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param connConfig initial configuration of connection.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult post(String requestURL, Map<String, String> params, MS_IFuncConnectionConfig connConfig) {
        return httpRequest("POST", requestURL, params, connConfig);
    }

    /**
     * Does HTTP "POST" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult post(String requestURL, Map<String, String> params) {
        return post(requestURL, params, MS_IFuncConnectionConfigDefaults.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "PUT" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data to be sent by request.
     * @param connConfig  initial configuration of connection.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, String requestBody, MS_IFuncConnectionConfig connConfig) {
        MS_HttpRequestResult res = new MS_HttpRequestResult();
        URL url;
        StringBuilder response = new StringBuilder();

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connConfig.initializeConnection(conn);
            conn.setRequestMethod("PUT");

            if (requestBody != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(requestBody);
                writer.flush();
                writer.close();
                os.close();
            }

            res.connection = conn;
            int responseCode = conn.getResponseCode();
            res.reponseCode = responseCode;

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    response.append(line);
            } else {
                res.message = "";
            }
        } catch (IOException e) {
            res.message = "";
            res.exception = e;
        }

        if (res.message == null)
            res.message = response.toString();
        return res;
    }

    /**
     * Does HTTP "PUT" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL  an URL to HTTP server.
     * @param requestBody data to be sent by request.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult put(String requestURL, String requestBody) {
        return put(requestURL, requestBody, MS_IFuncConnectionConfigDefaults.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "DELETE" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>
     * and connection configuration <b>connConfig</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @param connConfig initial configuration of connection.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, Map<String, String> params, MS_IFuncConnectionConfig connConfig) {
        return httpRequest("DELETE", requestURL, params, connConfig);
    }

    /**
     * Does HTTP "DELETE" request to presented URL <b>requestURL</b> altogether with presented parameters <b>params</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param params     map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, Map<String, String> params) {
        return delete(requestURL, params, MS_IFuncConnectionConfigDefaults.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP "DELETE" request to presented URL <b>requestURL</b>
     * altogether with presented connection configuration <b>connConfig</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @param connConfig initial configuration of connection.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL, MS_IFuncConnectionConfig connConfig) {
        return delete(requestURL, null, connConfig);
    }

    /**
     * Does HTTP "DELETE" request to presented URL <b>requestURL</b>.
     *
     * @param requestURL an URL to HTTP server.
     * @return HTTP response from server.
     */
    public static MS_HttpRequestResult delete(String requestURL) {
        return delete(requestURL, MS_IFuncConnectionConfigDefaults.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    //*** PRIVATE METHODS ***

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

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private static MS_HttpRequestResult httpRequest(String method, String requestURL, Map<String, String> params, MS_IFuncConnectionConfig connConfig) {
        MS_HttpRequestResult res = new MS_HttpRequestResult();
        URL url;
        StringBuilder response = new StringBuilder();

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connConfig.initializeConnection(conn);
            conn.setRequestMethod(method);

            if (params != null) {
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(formParamURL(params));
                writer.flush();
                writer.close();
                os.close();
            }

            res.connection = conn;
            int responseCode = conn.getResponseCode();
            res.reponseCode = responseCode;

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null)
                    response.append(line);
            } else {
                res.message = "";
            }
        } catch (IOException e) {
            res.message = "";
            res.exception = e;
        }

        if (res.message == null)
            res.message = response.toString();
        return res;
    }
}
