package lv.emes.libraries.utilities;

import lv.emes.libraries.storage.IFuncObjectRetrievalOperation;
import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.logging.MS_Log4Java;
import lv.emes.libraries.tools.logging.MS_LoggingRepository;
import lv.emes.libraries.tools.logging.MS_MultiLoggingSetup;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Utilities for operations with repositories.
 * <p>Static methods:
 * <ul>
 * <li>retrieveObject</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_RepositoryUtils {

    /**
     * Retrieves new or existing instance of object of type <b>T</b> by <b>id</b>,
     * using chain of different <b>retrievalOperations</b>.
     * If first operation in <b>retrievalOperations</b> fails to retrieve object, it goes to next one and so on.
     * <p>This method acts as performance saver in case there is a way to retrieve object in less expensive
     * manner rather than doing expensive operations like, for example, querying database or parsing large file.
     * That is recommended to execute less expensive operations earlier in <b>retrievalOperations</b> chain and
     * try to guarantee that in next method calls retrieval will be faster than in first call.
     * For example, if we need to retrieve information about user, which can be:
     * <ul>
     * <li>found in file in file system;</li>
     * <li>cached in memory;</li>
     * <li>calculated somehow from given data,</li>
     * </ul>
     * then best <b>retrievalOperations</b> order would be:
     * <ol>
     * <li>try to find this information from memory cache;</li>
     * <li>parse file to find information from there - after successful parsing return retrieved object,
     * also do in-memory caching of this object (in new thread);</li>
     * <li>calculate and create new instance if nothing else worked -
     * after this also do saving information to file in new thread and maybe even in another thread
     * try to cache this object in memory, if it's not there yet.</li>
     * </ol>
     *
     * @param <T>                 type of object to retrieve.
     * @param <ID>                type of object identifier.
     * @param id                  non-null identifier of object (if null is passed then method will return null).
     * @param continueOnError     flag to move on to next operation is current operation failed due to an exception.
     * @param retrievalOperations operation, which going to create new instance of object or will get existing one.
     * @return object retrieved by at least one <b>retrievalOperations</b> or null if that was impossible to
     * retrieve object by any of given <b>retrievalOperations</b> methods.
     * @throws MS_ObjectRetrievalFailureException if any exception occurred while performing any of object's retrieval operations.
     */
    @SafeVarargs //promise that we will use only for few  operations as retrievalOperations
    public static <T, ID> T retrieveObject(ID id, boolean continueOnError, IFuncObjectRetrievalOperation<T, ID>... retrievalOperations) {
        if (id == null) return null;
        return retrieveObject(id, continueOnError, MS_CodingUtils.arrayToList(retrievalOperations));
    }

    /**
     * Prepares logging repository to work with standard output console.
     * Should be used to add new repository to {@link lv.emes.libraries.tools.logging.MS_MultiLogger} repository
     * list with {@link MS_MultiLoggingSetup#withRepository(MS_LoggingRepository)}.
     *
     * @return new instance of console {@link MS_LoggingRepository}.
     */
    public static MS_LoggingRepository newConsoleLoggerRepository() {
        return event -> {
            switch (event.getType()) {
                case UNSPECIFIED:
                case INFO:
                case WARN:
                case ERROR:
                    System.out.println(event.getMessage());
            }
            if (event.getError() != null)
                event.getError().printStackTrace();
        };
    }

    /**
     * Prepares logging repository to work with {@link MS_Log4Java}.
     * Should be used to add new repository to {@link lv.emes.libraries.tools.logging.MS_MultiLogger} repository
     * list with {@link MS_MultiLoggingSetup#withRepository(MS_LoggingRepository)}.
     *
     * @param logger log4j logger retrieved with, for example, {@link MS_Log4Java#getLogger(String)},
     *               {@link MS_Log4Java#getLogger(Class)} or other logger factory method.
     * @return new instance of log4j {@link MS_LoggingRepository}.
     * @throws MS_BadSetupException if given <b>logger</b> is null.
     */
    public static MS_LoggingRepository newLog4JavaLoggerRepository(final Logger logger) throws MS_BadSetupException {
        if (logger == null)
            throw new MS_BadSetupException("Logger cannot be null. It must be retrieved by MS_Log4Java.getLogger method.");

        return event -> {
            switch (event.getType()) {
                case UNSPECIFIED:
                case INFO:
                    logger.info(event.getMessage());
                    break;
                case WARN:
                    logger.warn(event.getMessage());
                    break;
                case ERROR:
                    logger.error(event.getMessage(), event.getError());
            }
        };
    }

    //*** Private (static) methods ***

    private static <T, ID> T retrieveObject(ID id, boolean continueOnError, List<IFuncObjectRetrievalOperation<T, ID>> retrievalOperations) {
        T retrievedObject;
        if (retrievalOperations.isEmpty()) return null;
        IFuncObjectRetrievalOperation<T, ID> currentRetrievalOperation = retrievalOperations.get(0);
        if (continueOnError) {
            try {
                retrievedObject = currentRetrievalOperation.get(id);
            } catch (Exception e) {
                retrievedObject = null;
            }
        } else {
            try {
                retrievedObject = currentRetrievalOperation.get(id);
            } catch (Exception e) {
                throw new MS_ObjectRetrievalFailureException("Object retrieval failed by all retrieval operation alternatives. " +
                        "Last failure exception's stack trace: ", e);
            }
        }

        if (retrievedObject == null) {
            //get rid of head operation, cause it's already checked
            retrievalOperations.remove(0);
            retrievedObject = retrieveObject(id, continueOnError, retrievalOperations);
        }
        return retrievedObject;
    }
}
