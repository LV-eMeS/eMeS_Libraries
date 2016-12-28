package lv.emes.libraries.communication.http;

import java.net.HttpURLConnection;

/**
 * Use this in <b>MS_HttpClient</b> methods to define initial connection parameters before request is made!
 * This interface also provides a default behavior <b>DEFAULT_CONFIG_FOR_CONNECTION</b> to define timeouts and
 * use <b>HttpURLConnection</b> for input and output.
 *
 * @author eMeS
 * @version 1.0.
 * @see HttpURLConnection
 * @see MS_HttpClient
 */
@FunctionalInterface
public interface IFuncConnectionConfig {
    void initializeConnection(HttpURLConnection conn);

    int DEFAULT_TIMEOUT_MILISECONDS = 10000;
    /**
     * A default behavior for <b>HttpURLConnection</b> to define timeouts and
     * use <b>HttpURLConnection</b> for input and output.
     */
    IFuncConnectionConfig DEFAULT_CONFIG_FOR_CONNECTION = (conn) -> {
        conn.setReadTimeout(DEFAULT_TIMEOUT_MILISECONDS);
        conn.setConnectTimeout(DEFAULT_TIMEOUT_MILISECONDS);
        conn.setDoInput(true);
        conn.setDoOutput(true);
    };
}
