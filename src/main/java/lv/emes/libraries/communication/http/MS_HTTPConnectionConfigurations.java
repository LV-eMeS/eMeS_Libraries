package lv.emes.libraries.communication.http;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced
 * to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 2.0.
 */
public class MS_HTTPConnectionConfigurations {

    //some default input methods
    public static final int DEFAULT_TIMEOUT_SECONDS = 8;

    /**
     * A default configuration for HTTP connection to define timeouts.
     */
    public static final OkHttpClient.Builder DEFAULT_HTTP_CONFIG_FOR_CONNECTION = new OkHttpClient().newBuilder()
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);

    /**
     * @return specific HTTP connection request timeout settings.
     */
    public static OkHttpClient.Builder newTimeoutConfig(long timeout) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
