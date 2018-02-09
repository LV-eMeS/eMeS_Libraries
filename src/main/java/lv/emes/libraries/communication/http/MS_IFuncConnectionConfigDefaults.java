package lv.emes.libraries.communication.http;

import org.apache.http.client.config.RequestConfig;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_IFuncConnectionConfigDefaults {

    //some default input methods
    private static final int DEFAULT_TIMEOUT_MILISECONDS = 8000;

    /**
     * A default behavior for HTTP connection to define timeouts.
     */
    public static final RequestConfig DEFAULT_CONFIG_FOR_CONNECTION = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_TIMEOUT_MILISECONDS)
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT_MILISECONDS)
            .setSocketTimeout(DEFAULT_TIMEOUT_MILISECONDS).build();
}
