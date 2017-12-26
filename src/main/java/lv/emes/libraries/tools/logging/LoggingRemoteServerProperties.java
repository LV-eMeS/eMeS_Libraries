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
 * <li>getEndpointPutEvent</li>
 * <li>getEndpointGetSingleEvent</li>
 * <li>getEndpointGetAllEvents</li>
 * <li>getEndpointDeleteAllEvents</li>
 * <li>withHost</li>
 * <li>withPort</li>
 * <li>withEndpointRootName</li>
 * <li>withEndpointPutEvent</li>
 * <li>withEndpointGetSingleEvent</li>
 * <li>withEndpointGetAllEvents</li>
 * <li>withEndpointDeleteAllEvents</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class LoggingRemoteServerProperties {

    private String host;
    private int port = MS_TakenPorts._REMOTE_LOGGING_SERVER_PORT;
    private String endpointRootName = "RemoteLogger";
    private String endpointPutEvent = "event";
    private String endpointGetAllEvents = "all";
    private String endpointDeleteAllEvents = "clear";

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getEndpointRootName() {
        return endpointRootName;
    }

    public String getEndpointPutEvent() {
        return endpointPutEvent;
    }

    public String getEndpointGetAllEvents() {
        return endpointGetAllEvents;
    }

    public String getEndpointDeleteAllEvents() {
        return endpointDeleteAllEvents;
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
     * Sets endpoint name to log new event to repository.
     *
     * @param endpointPutEvent name of endpoint (X) [PUT endpointRootName/productOwner/productName/X].
     *                         <p><b>DEFAULT</b>: "event"
     * @return reference to properties.
     */
    public LoggingRemoteServerProperties withEndpointPutEvent(String endpointPutEvent) {
        if (endpointGetAllEvents != null && !endpointGetAllEvents.equals(""))
            this.endpointPutEvent = endpointPutEvent;
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
    public LoggingRemoteServerProperties withEndpointDeleteAllEvents(String endpointDeleteAllEvents) {
        if (endpointDeleteAllEvents != null && !endpointDeleteAllEvents.equals(""))
            this.endpointDeleteAllEvents = endpointDeleteAllEvents;
        return this;
    }
}
