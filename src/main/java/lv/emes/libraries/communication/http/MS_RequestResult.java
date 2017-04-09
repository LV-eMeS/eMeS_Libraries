package lv.emes.libraries.communication.http;

import java.io.IOException;
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
 * @version 1.1.
 */
public class MS_RequestResult {
    String message;
    int reponseCode;
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
