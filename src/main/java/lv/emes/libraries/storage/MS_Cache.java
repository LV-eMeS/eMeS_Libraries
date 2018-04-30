package lv.emes.libraries.storage;

import lv.emes.libraries.tools.logging.MS_MultiLogger;
import lv.emes.libraries.tools.threading.MS_FutureEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;

/**
 * An object cache that can hold objects of type <b>T</b>.
 * Those objects can be retrieved by their ID of type <b>ID</b>.
 * Also every stored object has time to live (TTL) in seconds, which is being set when performing
 * {@link MS_Cache#store(Object, Object, Long)} operation. Zero TTL will mean that objects will be cached forever.
 * If TTL is expired at the moment when retrieval operation is called,
 * object will be removed from cache, and retrieval operation will return <code>null</code>.
 * <p>All methods of {@link MS_Cache} are silent (no exceptions will be thrown), and write operations are asynchronous
 * that's why there is option to set multi logger {@link MS_MultiLogger}, which is supposed to
 * log any errors happened during caching or retrieval process.
 * <p>For ease of use overloaded method {@link MS_Cache#store(Object, Object)} without TTL is introduced to use
 * default TTL set by setter {@link MS_Cache#setDefaultTTL(long)}.
 * <p>Public methods:
 * <ul>
 * <li>store</li>
 * <li>cache (synonym to store)</li>
 * <li>retrieve</li>
 * <li>get (synonym to retrieve)</li>
 * <li>clear</li>
 * <li>removeAll (synonym to clear)</li>
 * </ul>
 * <p>Setters and getters:
 * <ul>
 * <li>setLogger</li>
 * <li>setDefaultTTL</li>
 * <li>getLogger</li>
 * <li>getDefaultTTL</li>
 * <li>getRepository</li>
 * </ul>
 *
 * @param <T>  type of objects that are going to be cached.
 * @param <ID> type of object identifiers.
 * @author eMeS
 * @version 1.0.
 */
public class MS_Cache<T, ID> {

    private MS_CachingRepository<T, ID> repository;
    private MS_MultiLogger logger;
    private long defaultTTL = 0L;

    /**
     * Constructs new cache, which is bind to caching repository <b>repository</b>.
     *
     * @param repository repository, which does actual storing and retrieval operations.
     * @throws NullPointerException if <b>repository</b> is <code>null</code>.
     */
    public MS_Cache(MS_CachingRepository<T, ID> repository) {
        if (repository == null)
            throw new NullPointerException("Cache cannot be constructed, because caching repository is null");
        this.repository = repository;
    }

    /**
     * Method runs new thread with purpose to store new object into cache.
     * @param object object to store.
     * @param id object's ID.
     * @param ttl object's Time To Live in cache.
     * @return reference to created storing operation thread.
     */
    public MS_FutureEvent store(T object, ID id, Long ttl) {
        if (ttl == null) ttl = defaultTTL;
        LocalDateTime expirationTime = ttl.intValue() == 0L ? null : LocalDateTime.now().plusSeconds(ttl);
        return new MS_FutureEvent()
                .withThreadName("MS_Cache object storage operation")
                .withAction(() -> repository.put(id, Pair.of(object, expirationTime)))
                .withActionOnException((e -> {
                    if (e instanceof UnsupportedOperationException) {
                        if (logger != null) {
                            logger.error("Caching operation is not supported", e);
                        }
                    } else if (e instanceof MS_RepositoryDataExchangeException) {
                        if (logger != null) {
                            String errorMess = e.getMessage() == null ? "" : "\nError message: " + e.getMessage();
                            logger.warn("Caching operation failed" + errorMess);
                        }
                    } else {
                        if (logger != null) {
                            logger.error("Unexpected error occurred while performing object storage", e);
                        }
                    }
                }))
                .schedule();
    }

    public MS_FutureEvent store(T object, ID id) {
        return store(object, id, null);
    }

    public T retrieve(ID id) {
        try {
            Pair<T, LocalDateTime> cachedObject = repository.get(id);
            if (cachedObject != null) {
                LocalDateTime ttl = cachedObject.getRight();
                if (ttl == null || LocalDateTime.now().isBefore(ttl)) {
                    return cachedObject.getLeft();
                } else {
                    //perform object removal from cache in new thread
                    performExpiredObjectRemoval(id);
                }
            }
        } catch (UnsupportedOperationException e) {
            if (logger != null) {
                logger.error("Object retrieval from cache operation is not supported", e);
            }
        } catch (MS_RepositoryDataExchangeException e) {
            if (logger != null) {
                String errorMess = e.getMessage() == null ? "" : "\nError message: " + e.getMessage();
                logger.warn("Object retrieval from cache operation failed" + errorMess);
            }
        }
        return null;
    }

    /**
     * Method runs new thread to clears all information stored in cache so far.
     * Some caching repositories might not support this operation.
     * In that case information about unsupported operation will be logged as warning.
     * @return reference to created clearing operation thread.
     */
    public MS_FutureEvent clear() {
        return new MS_FutureEvent()
                .withThreadName("MS_Cache all object cleanup operation")
                .withAction(() -> repository.removeAll())
                .withActionOnException((e -> {
                    if (e instanceof UnsupportedOperationException) {
                        if (logger != null) {
                            String errorMess = e.getMessage() == null ? "" : "\nError message: " + e.getMessage();
                            logger.warn("Cannot perform cache clearing operation because this operation is not supported " +
                                    "for this type of caching repository" + errorMess);
                        }
                    } else if (e instanceof MS_RepositoryDataExchangeException) {
                        if (logger != null) {
                            logger.error("Cache clearing operation failed due to data exchange exception", e);
                        }
                    } else {
                        if (logger != null) {
                            logger.error("Unexpected error occurred while performing all object cleanup", e);
                        }
                    }
                }))
                .schedule();
    }

    //*** Synonyms ***

    public MS_FutureEvent cache(T object, ID id, Long objectTTL) {
        return store(object, id, objectTTL);
    }

    public MS_FutureEvent cache(T object, ID id) {
        return store(object, id, null);
    }

    public T get(ID id) {
        return retrieve(id);
    }

    public MS_FutureEvent removeAll() {
        return clear();
    }

    //*** Setters and getters ***

    public MS_MultiLogger getLogger() {
        return logger;
    }

    public void setLogger(MS_MultiLogger logger) {
        this.logger = logger;
    }

    public long getDefaultTTL() {
        return defaultTTL;
    }

    public void setDefaultTTL(long defaultTTL) {
        this.defaultTTL = defaultTTL;
    }

    public MS_CachingRepository<T, ID> getRepository() {
        return repository;
    }

    //*** Private methods ***

    private void performExpiredObjectRemoval(ID objId) {
        new MS_FutureEvent()
                .withThreadName("MS_Cache expired object cleanup operation")
                .withAction(() -> repository.remove(objId))
                .withActionOnException((e -> {
                    if (e instanceof UnsupportedOperationException) {
                        if (logger != null) {
                            logger.error("Object removal from cache operation is not supported", e);
                        }
                    } else if (e instanceof MS_RepositoryDataExchangeException) {
                        if (logger != null) {
                            String errorMess = e.getMessage() == null ? "" : "\nError message: " + e.getMessage();
                            logger.warn("Object removal from cache operation failed" + errorMess);
                        }
                    } else {
                        if (logger != null) {
                            logger.error("Unexpected error occurred while performing expired object cleanup", e);
                        }
                    }
                }))
                .schedule();
    }
}
