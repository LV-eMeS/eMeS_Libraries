package lv.emes.libraries.communication.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A HTTP method's request result that includes not only return message from server,
 * but connection object and response status.
 * <p>Getters:
 * <ul>
 * <li>getMessage</li>
 * <li>getReponseCode</li>
 * <li>getException</li>
 * <li>getHeaders</li>
 * </ul>
 *
 * @author eMeS
 * @version 2.1.
 */
public class MS_HttpRequestResult {

    String message = "";
    int reponseCode = 503; //default value if service is unavailable for some reason
    IOException exception;
    private Map<String, String> headers = new HashMap<>();

    /**
     * @return message from response in string UTF-8 format, no matter what kind of response it actually is.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return HTTP response status code. 503 if server didn't response.
     */
    public int getReponseCode() {
        return reponseCode;
    }

    /**
     * @return I/O exception occurred while performing request. If null, no exception occurred.
     * If this exception is not null then {@link MS_HttpRequestResult#getReponseCode()} should return 0, which signals
     * that request is failed, but it's unclear, if request failed on client or server side, because
     * it might happen due to misconfiguration, incorrect use of request / response, etc.
     */
    public IOException getException() {
        return exception;
    }

    /**
     * @return list of HTTP response headers. Empty list if none header is returned by response or request failed.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
