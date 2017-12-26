package lv.emes.libraries.tools.logging;

import lv.emes.libraries.communication.MS_TakenPorts;

/**
 * Properties for Remote logging server.
 * Those include hostname and port number to make HTTP requests.
 * Also default and configurable values are provided for endpoint names, which are part of full path to make HTTP requests
 * to put new events into repository and retrieve existing ones from it.
 * If event logging server is configured properly, that is enough to set host property value <b>host</b>.
 * <p>Setters and getters:
 * <ul>
 * <li>getHost</li>
 * <li>getPort</li>
 * <li>getEndpointRootName</li>
 * <li>getEndpointLogEvent</li>
 * <li>getEndpointGetSingleEvent</li>
 * <li>getEndpointGetAllEvents</li>
 * <li>getEndpointClearAllEvents</li>
 * <li>withHost</li>
 * <li>withPort</li>
 * <li>withEndpointRootName</li>
 * <li>withEndpointLogEvent</li>
 * <li>withEndpointGetSingleEvent</li>
 * <li>withEndpointGetAllEvents</li>
 * <li>withEndpointClearAllEvents</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class LoggingRemoteServerProperties {

    private String host;
    private int port = MS_TakenPorts._REMOTE_LOGGING_SERVER_PORT;
    private String endpointRootName = "RemoteLogger";
    private String endpointStatus = "status";
    private String endpointLogEvent = "event";
    private String endpointGetAllEvents = "all";
    private String endpointClearAllEvents = "clear";

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

    /**
     * Sets host name of server.
     *
     * @param host host name.
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Sets port number of server.
     *
     * @param port port number. <b>DEFAULT</b>: {@link MS_TakenPorts#_REMOTE_LOGGING_SERVER_PORT}
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Sets root name for HTTP requests to endpoints.
     *
     * @param endpointRootName name of all endpoint path root (X) [GET X/productOwner/productName/...].
     *                         <p><b>DEFAULT</b>: "RemoteLogger"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointRootName(String endpointRootName) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointRootName = endpointRootName;
        return this;
    }

    /**
     * Sets root name for HTTP requests to endpoints.
     *
     * @param status name of endpoint to get status of server (X) [GET endpointRootName/X].
     *                         <p><b>DEFAULT</b>: "status"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointStatus(String status) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointStatus = status;
        return this;
    }

    /**
     * Sets endpoint name to log new event to repository.
     *
     * @param endpointPutEvent name of endpoint (X) [PUT endpointRootName/productOwner/productName/X].
     *                         <p><b>DEFAULT</b>: "event"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointLogEvent(String endpointPutEvent) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointLogEvent = endpointPutEvent;
        return this;
    }

    /**
     * Sets endpoint name to get all existing events in repository.
     *
     * @param endpointGetAllEvents name of endpoint (X) [GET endpointRootName/productOwner/productName/X].
     *                             <p><b>DEFAULT</b>: "all"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointGetAllEvents(String endpointGetAllEvents) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointGetAllEvents = endpointGetAllEvents;
        return this;
    }

    /**
     * Sets endpoint name to clear / delete all existing events in repository.
     *
     * @param endpointDeleteAllEvents name of endpoint (X) [DELETE endpointRootName/productOwner/productName/X].
     *                             <p><b>DEFAULT</b>: "clear"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointClearAllEvents(String endpointDeleteAllEvents) {
        if (endpointDeleteAllEvents != null && !endpointDeleteAllEvents.equals(""))
            this.endpointClearAllEvents = endpointDeleteAllEvents;
        return this;
    }
}
