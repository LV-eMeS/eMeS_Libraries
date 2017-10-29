package lv.emes.libraries.communication.http;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_IFuncConnectionConfigDefaults {

    //some default input methods
    private static final int DEFAULT_TIMEOUT_MILISECONDS = 10000;
    /**
     * A default behavior for <b>HttpURLConnection</b> to define timeouts and
     * use <b>HttpURLConnection</b> for input and output.
     */
    public static final MS_IFuncConnectionConfig DEFAULT_CONFIG_FOR_CONNECTION = (conn) -> {
        conn.setReadTimeout(DEFAULT_TIMEOUT_MILISECONDS);
        conn.setConnectTimeout(DEFAULT_TIMEOUT_MILISECONDS);
        conn.setDoInput(true);
        conn.setDoOutput(true);
    };
}
