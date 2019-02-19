package lv.emes.libraries.communication.http;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * HTTP connection configuration builders and defaults.
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
     * @param timeout timeout in seconds for connecting reading and writing.
     * @return specific HTTP connection request timeout settings.
     */
    public static OkHttpClient.Builder newTimeoutConfig(long timeout) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS);
    }

    /**
     * @param connectTimeout milliseconds for connection timeout.
     * @param readTimeout milliseconds for read timeout.
     * @param writeTimeout milliseconds for write timeout.
     * @return specific HTTP connection request timeout settings.
     */
    public static OkHttpClient.Builder newTimeoutConfig(long connectTimeout, long readTimeout, long writeTimeout) {
        return new OkHttpClient().newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
    }
}
