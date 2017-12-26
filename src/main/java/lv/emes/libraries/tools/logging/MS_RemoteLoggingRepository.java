package lv.emes.libraries.tools.logging;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import lv.emes.libraries.communication.http.MS_HttpClient;
import lv.emes.libraries.communication.http.MS_HttpRequestResult;
import lv.emes.libraries.tools.lists.MS_Repository;
import lv.emes.libraries.tools.lists.RepositoryDataExchangeException;
import lv.emes.libraries.utilities.MS_CodingUtils;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Remote repository to store logging events.
 * All data exchange is done by HTTP requests.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_RemoteLoggingRepository extends MS_Repository<MS_LoggingEvent, ZonedDateTime> implements MS_LoggingRepository {

    private LoggingRemoteServerProperties serverProperties;

    /**
     * Constructs instance of remote logging repository that will connect to server with properties <b>props</b>.
     *
     * @param productOwner product owner name that can be company or product maintainer name,
     *                     which is responsible for event logging for that specific product.
     * @param productName  product name that logging is for.
     * @param props        remote server properties that identifies specific logging service,
     *                     which accepts outbound connections and provides specific simple HTTP call support.
     */
    public MS_RemoteLoggingRepository(String productOwner, String productName, LoggingRemoteServerProperties props) {
        super(productOwner, productName);
        if (props.getHost() == null || props.getHost().length() < 3)
            throw new IllegalArgumentException("Invalid hostname [" + props.getHost() + "] passed as argument to create MS_RemoteLoggingRepository");
        this.serverProperties = props;
    }

    @Override
    public void logEvent(MS_LoggingEvent event) {
        add(event.getTime(), event);
    }

    @Override
    protected boolean isInitialized() {
        return MS_HttpClient
                .get(getRemoteServerRoot(serverProperties) + serverProperties.getEndpointStatus(), null)
                .getReponseCode() == 200;
    }

    @Override
    protected void doAdd(ZonedDateTime identifier, MS_LoggingEvent item) {
        if (item == null || LoggingEventTypeEnum.UNSPECIFIED.equals(item.getType()))
            return; //do nothing for lines, because it doesn't make sense to store them as items with IDs

        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointLogEvent();
        MS_HttpRequestResult httpResult = MS_HttpClient.post(url, MS_CodingUtils.newSingletonMap("event", JsonWriter.objectToJson(item)));

        if (httpResult.getReponseCode() == 400) { //this is the case when server responds with status
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Serialization error while performing add(" + identifier + ", item). Item data:\n" + item, httpResult.getException());
            throw new RepositoryDataExchangeException("Serialization error happened while trying to log new event");
        }

        checkResponseAndThrowExceptionIfNeeded(httpResult, "Log new event operation failed with HTTP status code " +
                httpResult.getReponseCode());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<ZonedDateTime, MS_LoggingEvent> doFindAll() {
        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointGetAllEvents();
        MS_HttpRequestResult httpResult = MS_HttpClient.get(url, null);
        checkResponseAndThrowExceptionIfNeeded(httpResult, "Finding all events failed with HTTP status code " +
                httpResult.getReponseCode());
        try {
            Object res = JsonReader.jsonToJava(httpResult.getMessage());
            return (Map<ZonedDateTime, MS_LoggingEvent>) res;
        } catch (Exception e) { //most probably cast exception, but shouldn't happen unless somebody doesn't understand, how to use this
            String message = "Deserialization error while trying to convert found logged events in JSON format to Java objects.";
            String detailedMessage = " JSON:\n" + httpResult.getMessage();
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class).error(message + detailedMessage, e);
            throw new RepositoryDataExchangeException(message, e);
        }
    }

    @Override
    protected void doRemoveAll() {
        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointClearAllEvents();
        MS_HttpRequestResult httpResult = MS_HttpClient.delete(url);
        checkResponseAndThrowExceptionIfNeeded(httpResult, "Removing all events failed with HTTP status code " +
                httpResult.getReponseCode());
    }

    /**
     * No preparations needed for remote repository to be accessible, that's, why this method does nothing.
     * Repository is accessible if remote logging service is up and running.
     * This can be checked with {@link MS_RemoteLoggingRepository#isInitialized()} method.
     */
    @Override
    protected void doInitialize() {
        //nothing to do here
    }

    @Override
    public MS_LoggingEvent find(ZonedDateTime identifier) throws UnsupportedOperationException, RepositoryDataExchangeException {
        throw new UnsupportedOperationException("Finding single event operation is not supported for Remote logging repository");
    }

    @Override
    protected MS_LoggingEvent doFind(ZonedDateTime identifier) {
        //nothing to do here
        return null;
    }

    @Override
    public void remove(ZonedDateTime identifier) throws UnsupportedOperationException, RepositoryDataExchangeException {
        throw new UnsupportedOperationException("Removing single event operation is not supported for Remote logging repository");
    }

    @Override
    protected void doRemove(ZonedDateTime identifier) {
        //nothing to do here
    }

    /**
     * This operation is considered as inefficient for Remote logging repository.
     * Please, use {@link MS_RemoteLoggingRepository#findAll()} method in order to get logged record count!
     *
     * @throws UnsupportedOperationException this operation is not supported for Remote logging repository.
     */
    @Override
    protected int doGetSize() {
        throw new UnsupportedOperationException("Getting logged record count operation is not supported, because it's inefficient for Remote logging repository");
    }

    //*** Getters ***

    /**
     * A synonym for {@link MS_RemoteLoggingRepository#getRepositoryRoot()}.
     *
     * @return product owner name that can be company or product maintainer name,
     * which is responsible for event logging for that specific product.
     */
    public String getProductOwner() {
        return getRepositoryRoot();
    }

    /**
     * A synonym for {@link MS_RemoteLoggingRepository#getRepositoryCategoryName()}.
     *
     * @return product name that logging is for.
     */
    public String getProductName() {
        return getRepositoryCategoryName();
    }

    //*** PRIVATE METHODS ***

    private String getRemoteServerRoot(LoggingRemoteServerProperties props) {
        return props.getHost() + ":" + props.getPort() + "/" + props.getEndpointRootName() + "/";
    }

    private String getRemoteServerBasePath(LoggingRemoteServerProperties props) {
        return getRemoteServerRoot(props) + this.getProductOwner() + "/" + this.getProductName() + "/";
    }

    private void checkResponseAndThrowExceptionIfNeeded(MS_HttpRequestResult httpResult, String message) {
        if (httpResult.getReponseCode() != 200) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error(message + ". Message:\n" + httpResult.getMessage(), httpResult.getException());
            throw new RepositoryDataExchangeException(message, httpResult.getException());
        }
    }
}
