package lv.emes.libraries.communication.http;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * A HTTP method's request result that includes not only return message from server,
 * but connection object and response status.
 * <p>Getters:
 * <ul>
 * <li>getMessage</li>
 * <li>getConnection</li>
 * <li>getReponseCode</li>
 * <li>getException</li>
 * </ul>
 * @author eMeS
 * @version 2.0.
 */
public class MS_HttpRequestResult {

    String message;
    int reponseCode = 503; //default value if service is unavailable for some reason
    HttpURLConnection connection;
    IOException exception;

    public String getMessage() {
        return message;
    }
    public HttpURLConnection getConnection() {
        return connection;
    }
    public Integer getReponseCode() {
        return reponseCode;
    }
    public IOException getException() {
        return exception;
    }
}
