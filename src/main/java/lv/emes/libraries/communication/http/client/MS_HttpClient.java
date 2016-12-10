package lv.emes.libraries.communication.http.client;

import lv.emes.libraries.file_system.MS_BinaryTools;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
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
    //TODO http://blog.modulus.io/build-your-first-http-server-in-nodejs
    private static final int TIMEOUT_MILISECONDS = 15000;

    private static String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException{
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

    public static void main(String[] args) throws IOException {
        String urlStringGet = "http://emesserver.ddns.net/Test/test_get.php";
        String urlStringPost = "http://emesserver.ddns.net/Test/test_post.php";
        String urlStringNoParams = "http://emesserver.ddns.net/Test/test_no_params.php";

        Map<String, String> params = new HashMap<>();
        params.put("test", "vards");
        params.put("name", "vards");

        String response;
        response = get(urlStringGet, params);
        System.out.println(response);

        response = post(urlStringPost, params);
        System.out.println(response);

        response = get(urlStringNoParams, params);
        System.out.println(response);

        response = post(urlStringNoParams, params);
        System.out.println(response);
        //TODO write this as tests!
    }

    /**
     * Does HTTP method "GET" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     * @param requestURL an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static String get(String requestURL, Map<String, String> postDataParams) {
        try {
            requestURL += "?" + getPostDataString(postDataParams);
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return MS_BinaryTools.inputToUTF8(in);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * Does HTTP method "POST" to presented URL <b>requestURL</b> with presented parameters <b>postDataParams</b>.
     * @param requestURL an URL to HTTP server.
     * @param postDataParams map of parameters to pass for this URL.
     * @return HTTP response from server.
     */
    public static String post(String requestURL, Map<String, String> postDataParams) {
        URL url;
        StringBuilder response = new StringBuilder();
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
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response.append(line);
                }
            }
            else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }

        return response.toString();
    }
}
