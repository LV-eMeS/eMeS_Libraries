package lv.emes.libraries.communication.http;

import lv.emes.libraries.file_system.MS_BinaryTools;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * This class can be used while working with HTTP requests.
 * <p>Static methods:
 * <ul>
 * <li>get</li>
 * <li>post</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
//http://stackoverflow.com/questions/2938502/sending-post-data-in-android
public class MS_HttpClient {
    private static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null) //if no parameters is assigned then there is no need to build parameters as string
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

    /**
     * Does HTTP method "GET" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     *
     * @param requestURL     an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @param connConfig     initial configuration of connection.
     * @return HTTP response from server.
     */
    public static RequestResult get(String requestURL, Map<String, String> postDataParams, IFuncConnectionConfig connConfig) {
        URL url;
        RequestResult res = new RequestResult();
        try {
            if (postDataParams != null)
                requestURL += "?" + getPostDataString(postDataParams);
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
     * Does HTTP method "GET" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     *
     * @param requestURL     an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static RequestResult get(String requestURL, Map<String, String> postDataParams) {
        return get(requestURL, postDataParams, IFuncConnectionConfig.DEFAULT_CONFIG_FOR_CONNECTION);
    }

    /**
     * Does HTTP method "POST" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     *
     * @param requestURL     an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @param connConfig     initial configuration of connection.
     * @return HTTP response from server.
     */
    public static RequestResult post(String requestURL, Map<String, String> postDataParams, IFuncConnectionConfig connConfig) {
        RequestResult res = new RequestResult();
        URL url;
        StringBuilder response = new StringBuilder();

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            connConfig.initializeConnection(conn);
            conn.setRequestMethod("POST");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();

            res.connection = conn;
            int responseCode = conn.getResponseCode();
            res.reponseCode = responseCode;

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
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
     * Does HTTP method "POST" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     *
     * @param requestURL     an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static RequestResult post(String requestURL, Map<String, String> postDataParams) {
        return post(requestURL, postDataParams, IFuncConnectionConfig.DEFAULT_CONFIG_FOR_CONNECTION);
    }
}
