package lv.emes.libraries.tools.logging;

import com.cedarsoftware.util.io.JsonReader;
import lv.emes.libraries.communication.cryptography.MS_CryptographyUtils;
import lv.emes.libraries.communication.http.MS_HttpCallHandler;
import lv.emes.libraries.communication.http.MS_HttpRequest;
import lv.emes.libraries.communication.http.MS_HttpRequestMethod;
import lv.emes.libraries.communication.http.MS_HttpResponse;
import lv.emes.libraries.storage.MS_Repository;
import lv.emes.libraries.storage.MS_RepositoryDataExchangeException;
import lv.emes.libraries.tools.MS_ObjectWrapperHelper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Remote repository to store logging events.
 * All data exchange is done by HTTP requests.
 *
 * @author eMeS
 * @version 1.1.
 * @since 2.0.4
 */
public class MS_RemoteLoggingRepository extends MS_Repository<MS_LoggingEvent, Instant> implements MS_LoggingRepository {

    public static int MAX_SECRET_LENGTH = 250;

    private MS_LoggingRemoteServerProperties serverProperties;

    /**
     * Constructs instance of remote logging repository that will connect to server with properties <b>props</b>.
     *
     * @param productOwner product owner name that can be company or product maintainer name,
     *                     which is responsible for event logging for that specific product.
     * @param productName  product name that logging is for.
     * @param props        remote server properties that identifies specific logging service,
     *                     which accepts outbound connections and provides specific simple HTTP call support.
     */
    public MS_RemoteLoggingRepository(String productOwner, String productName, MS_LoggingRemoteServerProperties props) {
        super(productOwner, productName);
        if (props.getHost() == null || props.getHost().length() < 3)
            throw new IllegalArgumentException("Invalid hostname [" + props.getHost() + "] passed as argument to create MS_RemoteLoggingRepository");
        this.serverProperties = props;
    }

    /**
     * Constructs instance of remote logging repository that will connect to default logging server developed by eMeS.
     *
     * @param productOwner product owner name that can be company or product maintainer name,
     *                     which is responsible for event logging for that specific product.
     * @param productName  product name that logging is for.
     * @param secret       secret for this specific product to restrict public access of logs produced by this product.
     */
    public MS_RemoteLoggingRepository(String productOwner, String productName, String secret) {
        this(productOwner, productName, new MS_LoggingRemoteServerProperties().withSecret(secret));
    }

    @Override
    public void logEvent(MS_LoggingEvent event) {
        add(event.getTime().toInstant(), event);
    }

    @Override
    public boolean isInitialized() {
        try {
            return MS_HttpCallHandler.call(
                    new MS_HttpRequest().withMethod(MS_HttpRequestMethod.GET)
                            .withUrl(getRemoteServerRoot(serverProperties) + serverProperties.getEndpointStatus())
                            .withClientConfigurations(serverProperties.getHttpRequestConfig()))
                    .getStatusCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void doAdd(Instant identifier, MS_LoggingEvent item) {
        if (item == null || MS_LoggingEventTypeEnum.UNSPECIFIED.equals(item.getType()))
            return; //do nothing for lines, because it doesn't make sense to store them as items with IDs

        //serialize event and send it to remote logging server
        MS_SerializedLoggingEvent serializedEvent = MS_ObjectWrapperHelper.wrap(item, MS_SerializedLoggingEvent.class);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("type", serializedEvent.getType());
        parameters.put("time", serializedEvent.getTime());
        parameters.put("message", serializedEvent.getMessage());
        parameters.put("error", serializedEvent.getError());

        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointLogEvent();
        MS_HttpRequest req = new MS_HttpRequest().withMethod(MS_HttpRequestMethod.POST).withUrl(url)
                .withParameters(parameters).withHeader("Authorization", getEncryptedSecret(serverProperties))
                .withClientConfigurations(serverProperties.getHttpRequestConfig());
        MS_HttpResponse httpResult;
        try {
            httpResult = MS_HttpCallHandler.call(req);
        } catch (IOException e) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Connectivity error while performing add(" + identifier + ", item). Item data:\n" + item, e);
            throw new MS_RepositoryDataExchangeException("Connectivity error happened while trying to log new event");
        }

        if (httpResult.getStatusCode() == 400) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Serialization error while performing add(" + identifier + ", item). Item data:\n" + item);
            throw new MS_RepositoryDataExchangeException("Serialization error happened while trying to log new event");
        }

        checkResponseAndThrowExceptionIfNeeded(httpResult, "Log new event operation failed with HTTP status code " +
                httpResult.getStatusCode());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Map<Instant, MS_LoggingEvent> doFindAll() {
        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointGetAllEvents();
        MS_HttpRequest req = new MS_HttpRequest().withMethod(MS_HttpRequestMethod.GET).withUrl(url)
                .withHeader("Authorization", getEncryptedSecret(serverProperties))
                .withClientConfigurations(serverProperties.getHttpRequestConfig());
        MS_HttpResponse httpResult;
        try {
            httpResult = MS_HttpCallHandler.call(req);
        } catch (IOException e) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Connectivity error while performing findAll().", e);
            throw new MS_RepositoryDataExchangeException("Connectivity error happened while trying to find all events");
        }

        checkResponseAndThrowExceptionIfNeeded(httpResult, "Finding all events failed with HTTP status code " +
                httpResult.getStatusCode());
        try {
            Map<Instant, MS_LoggingEvent> res = new TreeMap<>(Collections.reverseOrder());
            Map<ZonedDateTime, MS_SerializedLoggingEvent> serializedEvents =
                    (Map<ZonedDateTime, MS_SerializedLoggingEvent>) JsonReader.jsonToJava(httpResult.getBodyAsString());
            serializedEvents.forEach((key, value) -> res.put(key.toInstant(), value.getWrappedObject()));
            return res;
        } catch (Exception e) { //most probably cast exception, but shouldn't happen unless somebody doesn't understand, how to use this
            String message = "Deserialization error while trying to convert found logged events in JSON format to Java objects.";
            String detailedMessage = " JSON:\n" + httpResult.getBodyAsString();
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class).error(message + detailedMessage, e);
            throw new MS_RepositoryDataExchangeException(message, e);
        }
    }

    @Override
    protected void doRemoveAll() {
        String url = getRemoteServerBasePath(serverProperties) + serverProperties.getEndpointClearAllEvents();
        MS_HttpRequest req = new MS_HttpRequest().withMethod(MS_HttpRequestMethod.DELETE).withUrl(url)
                .withHeader("Authorization", getEncryptedSecret(serverProperties))
                .withClientConfigurations(serverProperties.getHttpRequestConfig());
        MS_HttpResponse httpResult;
        try {
            httpResult = MS_HttpCallHandler.call(req);
        } catch (IOException e) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Connectivity error while performing removeAll().", e);
            throw new MS_RepositoryDataExchangeException("Connectivity error happened while trying to delete/clear all events");
        }
        checkResponseAndThrowExceptionIfNeeded(httpResult, "Removing all events failed with HTTP status code " +
                httpResult.getStatusCode());
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
    public MS_LoggingEvent find(Instant identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        throw new UnsupportedOperationException("Finding single event operation is not supported for Remote logging repository");
    }

    @Override
    protected MS_LoggingEvent doFind(Instant identifier) {
        //nothing to do here
        return null;
    }

    @Override
    public void remove(Instant identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException {
        throw new UnsupportedOperationException("Removing single event operation is not supported for Remote logging repository");
    }

    @Override
    protected void doRemove(Instant identifier) {
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

    private String getRemoteServerRoot(MS_LoggingRemoteServerProperties props) {
        return props.getHost() + ":" + props.getPort() + "/" + props.getEndpointRootName() + "/";
    }

    private String getRemoteServerBasePath(MS_LoggingRemoteServerProperties props) {
        return getRemoteServerRoot(props) + this.getProductOwner() + "/" + this.getProductName() + "/";
    }

    private String getEncryptedSecret(MS_LoggingRemoteServerProperties props) {
        try {
            if (props.getSecret().length() > MAX_SECRET_LENGTH)
                throw new MS_RepositoryDataExchangeException("Failed to encrypt secret for this logging product - length of secret exceeds 255 characters");
            return MS_CryptographyUtils.encrypt(props.getSecret(), MS_LoggingRemoteServerProperties.SECRET_TO_ENCRYPT_SECRET);
        } catch (GeneralSecurityException e) {
            throw new MS_RepositoryDataExchangeException("Failed to encrypt secret for this logging product", e);
        }
    }

    private void checkResponseAndThrowExceptionIfNeeded(MS_HttpResponse httpResult, String message) {
        if (httpResult.getStatusCode() == 401) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error("Authentication error while performing repository operation.");
            throw new MS_RepositoryDataExchangeException("Authentication error happened while performing repository operation.\n" +
                    "Each owner of product should use unique secret key to access product's logging repository.");
        } else if (httpResult.getStatusCode() != 200) {
            MS_Log4Java.getLogger(MS_RemoteLoggingRepository.class)
                    .error(message + ". Message:\n" + httpResult.getBodyAsString());
            throw new MS_RepositoryDataExchangeException(message);
        }
    }
}
