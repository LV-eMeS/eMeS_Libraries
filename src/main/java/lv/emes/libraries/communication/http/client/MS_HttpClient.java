package lv.emes.libraries.communication.http.client;

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
 * @version 1.0.
 */
//http://stackoverflow.com/questions/2938502/sending-post-data-in-android
public class MS_HttpClient {
    private static final int TIMEOUT_MILISECONDS = 15000;

    private static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException{
        if (params == null) //if no parameters is assigned then there is no need to build parameters as string
            return "";

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
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
     * @param requestURL an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static RequestResult get(String requestURL, Map<String, String> postDataParams) {
        URL url;
        RequestResult res = new RequestResult();
        try {
            requestURL += "?" + getPostDataString(postDataParams);
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            res.connection = conn;
            res.reponseCode = conn.getResponseCode();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            res.message = MS_BinaryTools.inputToUTF8(in);
        } catch (IOException e) {
            res.message = "";
        }
        return res;
    }

    /**
     * Does HTTP method "POST" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     * @param requestURL an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static RequestResult post(String requestURL, Map<String, String> postDataParams) {
        URL url;
        StringBuilder response = new StringBuilder();
        RequestResult res = new RequestResult();

        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIMEOUT_MILISECONDS);
            conn.setConnectTimeout(TIMEOUT_MILISECONDS);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

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
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
            }
            else {
                res.message = "";
            }
        } catch (Exception e) {
            res.message = "";
        }

        if (res.message == null)
            res.message = response.toString();
        return res;
    }
}
