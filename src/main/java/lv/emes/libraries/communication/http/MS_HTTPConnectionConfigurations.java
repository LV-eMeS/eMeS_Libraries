package lv.emes.libraries.communication.http;

import org.apache.http.client.config.RequestConfig;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced
 * to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_HTTPConnectionConfigurations {

    //some default input methods
    public static final int DEFAULT_TIMEOUT_MILISECONDS = 8000;

    /**
     * A default configuration for HTTP connection to define timeouts.
     */
    public static final RequestConfig DEFAULT_CONFIG_FOR_CONNECTION = RequestConfig.custom()
            .setConnectTimeout(DEFAULT_TIMEOUT_MILISECONDS)
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT_MILISECONDS)
            .setSocketTimeout(DEFAULT_TIMEOUT_MILISECONDS).build();

    /**
     * @return specific HTTP connection request timeout settings.
     */
    public static RequestConfig newTimeoutConfig(int timeout) {
        return RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).build();
    }
}
