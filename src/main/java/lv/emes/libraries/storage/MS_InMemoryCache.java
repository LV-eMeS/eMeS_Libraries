package lv.emes.libraries.storage;

import lv.emes.libraries.utilities.MS_ObjectRetrievalFailureException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository that is meant just for in-memory object caching or creating new instances with custom method and
 * afterwards saving newly created object to cache to retrieve it later on.
 * <p>Public methods:
 * <ul>
 * <li>get</li>
 * </ul>
 *
 * @param <T>  type of objects stored in cache.
 * @param <ID> type of object's identifier.
 * @author eMeS
 * @version 1.1.
 */
public class MS_InMemoryCache<T, ID> {

    //TODO add TTL and possibility to store objects in different caches (only memory will be default one)
    private Map<ID, T> objects = new ConcurrentHashMap<>();

    /**
     * Retrieves existing cached object or new instance created by <b>retrievalOperation</b>.
     * @param id                 identifier of object.
     * @param retrievalOperation alternative operation, which going to create new instance of object if it's not
     *                           found in cache.
     * @return object retrieved either from cache, either by <b>retrievalOperation</b>.
     * @throws NullPointerException if <b>id</b> or <b>retrievalOperation</b> is null.
     * @throws MS_ObjectRetrievalFailureException     if any exception occurred while performing object's retrieval operation.
     */
    public T get(ID id, IFuncObjectRetrievalOperation<T, ID> retrievalOperation) throws RuntimeException {
        if (id == null || retrievalOperation == null)
            throw new NullPointerException("Cannot retrieve object from cache. Either id or retrievalOperation is null.");
        T objectInCache = objects.get(id);
        if (objectInCache == null) {
            try {
                objectInCache = retrievalOperation.get(id);
            } catch (Exception e) {
                throw new MS_ObjectRetrievalFailureException("Object retrieval failed. " +
                        "It doesn't exist in cache yet and alternative retrieval operation failed due to an exception", e);
            }
            objects.put(id, objectInCache);
        }
        return objectInCache;
    }
}
