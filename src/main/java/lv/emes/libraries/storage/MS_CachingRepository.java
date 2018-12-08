package lv.emes.libraries.storage;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;

/**
 * An abstract caching repository, which is meant to cache objects of same type.
 * Should be initialized and used in {@link MS_Cache} as constructor parameter.
 * All repository methods must be implemented thread-safely.
 * <p><u>Note</u>: second element of Pair is expiration time, which can be
 * <code>null</code> in case object must stay cached for a lifetime {@link MS_CachingRepository#doAdd(Object, Pair)}.
 *
 * @param <T>  type of objects that are going to be cached.
 * @param <ID> type of object identifiers.
 * @author eMeS
 * @version 1.0.
 * @see MS_Cache
 */
public abstract class MS_CachingRepository<T, ID> extends MS_Repository<Pair<T, LocalDateTime>, ID> {

    public static final String _DEFAULT_REPOSITORY_ROOT_NAME = "MS_Cache";

    /**
     * Constructs new caching repository.
     *
     * @param repositoryRoot name that will identify this repository across other repositories. E.g. "Caches".
     * @param cacheName      name that will identify objects that can be found only in this cache. E.g. "Auth tokens".
     */
    public MS_CachingRepository(String repositoryRoot, String cacheName) {
        super(repositoryRoot, cacheName);
    }

    /**
     * Constructs new caching repository and initializes it if <b>autoInitialize</b> is passed as <code>true</code>.
     *
     * @param repositoryRoot name that will identify this repository across other repositories. E.g. "Caches".
     * @param cacheName      name that will identify objects that can be found only in this cache. E.g. "Auth tokens".
     * @param autoInitialize if initialization needs to be performed right after successful construction.
     */
    public MS_CachingRepository(String repositoryRoot, String cacheName, boolean autoInitialize) {
        super(repositoryRoot, cacheName, autoInitialize);
    }

    /**
     * Without any checking store object into repository.
     * Used in:
     * <ul>
     *     <li>{@link MS_Cache#store(Object, Object)}</li>
     *     <li>{@link MS_Cache#store(Object, Object, Long)}</li>
     *     <li>{@link MS_Cache#cache(Object, Object)}</li>
     *     <li>{@link MS_Cache#cache(Object, Object, Long)}</li>
     * </ul>
     * <p><u>Note</u>: be aware that expiration time can be <code>null</code>
     * in case object must stay cached for a lifetime!
     *
     * @param identifier an object identifier.
     * @param object     a pair of object and its expiration time.
     */
    @Override
    protected abstract void doAdd(ID identifier, Pair<T, LocalDateTime> object);

    /**
     * Looks for single cached object.
     * Used in:
     * <ul>
     *     <li>{@link MS_Cache#retrieve(Object)}</li>
     *     <li>{@link MS_Cache#get(Object)}</li>
     * </ul>
     * <p><u>Note</u>: be aware that expiration time can be <code>null</code>
     * in case object must stay cached for a lifetime!
     *
     * @param identifier an identifier to find cached object altogether with its expiration time.
     * @return cached object altogether with its expiration time.
     */
    @Override
    protected abstract Pair<T, LocalDateTime> doFind(ID identifier);

    /**
     * Removes object from cache by its ID.
     * Used in:
     * <ul>
     *     <li>{@link MS_Cache#retrieve(Object)}</li>
     *     <li>{@link MS_Cache#get(Object)}</li>
     * </ul>
     * @param identifier an identifier of object.
     */
    @Override
    protected abstract void doRemove(ID identifier);

    /**
     * Clears cache by removing / deleting all previously cached objects from it.
     * Used in:
     * <ul>
     *     <li>{@link MS_Cache#clear()}</li>
     * </ul>
     */
    @Override
    protected abstract void doRemoveAll();
}
