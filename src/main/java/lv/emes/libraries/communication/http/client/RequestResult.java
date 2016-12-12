package lv.emes.libraries.communication.http.client;

import java.net.HttpURLConnection;

/**
 * A HTTP method's request result that includes not only return message from server,
 * but connection object and response status.
 * <p>Setters and getters:
 * <ul>
 * <li>getMessage</li>
 * <li>getConnection</li>
 * <li>getReponseCode</li>
 * </ul>
 * @author eMeS
 * @version 1.0.
 */
public class RequestResult {
    String message;
    int reponseCode;
    HttpURLConnection connection;

    public String getMessage() {
        return message;
    }
    public HttpURLConnection getConnection() {
        return connection;
    }
    public Integer getReponseCode() {
        return reponseCode;
    }
}
