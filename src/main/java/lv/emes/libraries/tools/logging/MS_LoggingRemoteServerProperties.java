package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_TakenPorts;
import lv.emes.libraries.communication.http.MS_HTTPConnectionConfigurations;
import org.apache.http.client.config.RequestConfig;

/**
 * Properties for Remote logging server.
 * Those include logging server hostname and port number to make HTTP requests.
 * Also default and configurable values are provided for endpoint names, which are part of full path to make HTTP requests
 * to put new events into repository and retrieve existing ones from it.
 * If event logging server is configured properly, that is enough to set host property value <b>host</b> and <b>secret</b>.
 * In case eMeS logging server is good enough for storing logging information, even <b>host</b> property can be left
 * out in order to use default one <a href="http://emeslv.sytes.net">emeslv.sytes.net</a>.
 * <p>Setters and getters:
 * <ul>
 * <li>getHost</li>
 * <li>getPort</li>
 * <li>getEndpointRootName</li>
 * <li>getEndpointLogEvent</li>
 * <li>getEndpointGetSingleEvent</li>
 * <li>getEndpointGetAllEvents</li>
 * <li>getEndpointClearAllEvents</li>
 * <li>getSecret</li>
 * <li>withHost</li>
 * <li>withPort</li>
 * <li>withEndpointRootName</li>
 * <li>withEndpointLogEvent</li>
 * <li>withEndpointGetSingleEvent</li>
 * <li>withEndpointGetAllEvents</li>
 * <li>withEndpointClearAllEvents</li>
 * <li>withSecret</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_LoggingRemoteServerProperties {

    /**
     * Secret key to be used in order to encrypt secret coming altogether with logging event to authorize logging.
     */
    public static final String SECRET_TO_ENCRYPT_SECRET = "Default remote logger secret 2018";

    private String host = "emeslv.sytes.net";
    private int port = MS_TakenPorts._REMOTE_LOGGING_SERVER_PORT;
    private String secret = SECRET_TO_ENCRYPT_SECRET;
    private String endpointRootName = "RemoteLogger";
    private String endpointStatus = "status";
    private String endpointLogEvent = "event";
    private String endpointGetAllEvents = "all";
    private String endpointClearAllEvents = "clear";

    private RequestConfig httpRequestConfig = MS_HTTPConnectionConfigurations.DEFAULT_CONFIG_FOR_CONNECTION;
    private int httpConnectionTimeout = MS_HTTPConnectionConfigurations.DEFAULT_TIMEOUT_MILISECONDS;

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getEndpointRootName() {
        return endpointRootName;
    }

    public String getEndpointLogEvent() {
        return endpointLogEvent;
    }

    public String getEndpointGetAllEvents() {
        return endpointGetAllEvents;
    }

    public String getEndpointClearAllEvents() {
        return endpointClearAllEvents;
    }

    public String getEndpointStatus() {
        return endpointStatus;
    }

    public String getSecret() {
        return secret;
    }

    public int getHttpConnectionTimeout() {
        return httpConnectionTimeout;
    }

    public RequestConfig getHttpRequestConfig() {
        return httpRequestConfig;
    }

    /**
     * Sets host name of server.
     *
     * @param host host name.
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets port number of server.
     *
     * @param port port number. <b>DEFAULT</b>: {@link MS_TakenPorts#_REMOTE_LOGGING_SERVER_PORT}.
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets root name for HTTP requests to endpoints.
     *
     * @param endpointRootName name of all endpoint path root (X) [GET X/productOwner/productName/...].
     *                         <p><b>DEFAULT</b>: "RemoteLogger".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withEndpointRootName(String endpointRootName) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointRootName = endpointRootName;
        return this;
    }

    /**
     * Sets root name for HTTP requests to endpoints.
     *
     * @param status name of endpoint to get status of server (X) [GET endpointRootName/X].
     *               <p><b>DEFAULT</b>: "status".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withEndpointStatus(String status) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointStatus = status;
        return this;
    }

    /**
     * Sets endpoint name to log new event to repository.
     *
     * @param endpointPutEvent name of endpoint (X) [PUT endpointRootName/productOwner/productName/X].
     *                         <p><b>DEFAULT</b>: "event".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withEndpointLogEvent(String endpointPutEvent) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointLogEvent = endpointPutEvent;
        return this;
    }

    /**
     * Sets endpoint name to get all existing events in repository.
     *
     * @param endpointGetAllEvents name of endpoint (X) [GET endpointRootName/productOwner/productName/X].
     *                             <p><b>DEFAULT</b>: "all".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withEndpointGetAllEvents(String endpointGetAllEvents) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointGetAllEvents = endpointGetAllEvents;
        return this;
    }

    /**
     * Sets endpoint name to clear / delete all existing events in repository.
     *
     * @param endpointDeleteAllEvents name of endpoint (X) [DELETE endpointRootName/productOwner/productName/X].
     *                                <p><b>DEFAULT</b>: "clear".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withEndpointClearAllEvents(String endpointDeleteAllEvents) {
        if (endpointDeleteAllEvents != null && !endpointDeleteAllEvents.equals(""))
            this.endpointClearAllEvents = endpointDeleteAllEvents;
        return this;
    }

    /**
     * Sets value of the secret that in combination with <b>productOwner</b> and <b>productName</b> gives
     * access to read and write events to repository.
     *
     * @param secret secret value that is saved to Remote server on very first request that is made for
     *               specific <b>productOwner</b> and <b>productName</b>.
     *               For further requests check is made against this <b>secret</b> value and in case of mismatch
     *               of the value that is provided and the one that is stored in Remote server's repository,
     *               authorization error (HTTP: 401) occurs.
     *               <p><b>DEFAULT</b>: "Default secret 123".
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withSecret(String secret) {
        if (secret != null && !secret.equals(""))
            this.secret = secret;
        return this;
    }

    /**
     * Sets common connection timeout value for any request that is made against remote
     * logging server (even for initialization check request). All requests that will take time longer than
     * this timeout will fail with error code 504.
     *
     * @param httpConnectionTimeout desired connection timeout.
     *                              <p><b>DEFAULT</b>: {@link MS_HTTPConnectionConfigurations#DEFAULT_TIMEOUT_MILISECONDS}.
     * @return reference to properties.
     */
    public MS_LoggingRemoteServerProperties withHttpConnectionTimeout(int httpConnectionTimeout) {
        if (this.httpConnectionTimeout != httpConnectionTimeout) {
            this.httpConnectionTimeout = httpConnectionTimeout;
            this.httpRequestConfig = MS_HTTPConnectionConfigurations.newTimeoutConfig(httpConnectionTimeout);
        }
        return this;
    }
}
