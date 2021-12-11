package lv.emes.libraries.communication.http;

import okhttp3.OkHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * HTTP connection configuration builders and defaults.
 *
 * @author eMeS
 * @version 2.1.
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

    /**
     *
     * @return specific HTTP connection request settings that will bypass untrusted SSL checks.
     */
    public static OkHttpClient.Builder newUnsafeOkHttpClientBuilder() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = newNaiveTrustManager();

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an SSL socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static OkHttpClient.Builder newUnsafeOkHttpClientBuilder(long timeout) {
        return newUnsafeOkHttpClientBuilder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS);
    }

    public static OkHttpClient.Builder newUnsafeOkHttpClientBuilder(long connectTimeout, long readTimeout, long writeTimeout) {
        return newUnsafeOkHttpClientBuilder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
    }

    private static TrustManager[] newNaiveTrustManager() {
        return new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
    }
}
