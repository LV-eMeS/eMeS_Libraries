package lv.emes.libraries.storage;

import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of in-memory cache, which caches all objects in key-value map, where key is
 * <b>ID</b> and value is pair of object (cachable object) of type <b>T</b>
 * and {@link LocalDateTime}, which represents object expiration time.
 *
 * @param <T>  type of objects that are going to be cached.
 * @param <ID> type of object identifiers.
 * @author eMeS
 * @version 1.0.
 */
public class MS_InMemoryCachingRepository<T, ID> extends MS_CachingRepository<T, ID> {

    public static final String _DEFAULT_CACHE_NAME = "In-memory cache";

    private Map<ID, Pair<T, LocalDateTime>> objects;

    /**
     * Constructs new in-memory caching repository with default root and cache names.
     */
    public MS_InMemoryCachingRepository() {
        this(_DEFAULT_REPOSITORY_ROOT_NAME, _DEFAULT_CACHE_NAME);
    }

    /**
     * Constructs new in-memory caching repository with informative <b>cacheRootName</b> and <b>cacheName</b>.
     * @param cacheRootName name identifying in-memory cache root, which actually doesn't mean anything.
     * @param cacheName name for this in-memory cache.
     */
    public MS_InMemoryCachingRepository(String cacheRootName, String cacheName) {
        super(cacheRootName, cacheName, true);
    }

    @Override
    public boolean isInitialized() {
        return objects != null;
    }

    @Override
    protected void doInitialize() {
        objects = new ConcurrentHashMap<>();
    }

    @Override
    protected void doAdd(ID identifier, Pair<T, LocalDateTime> object) {
        objects.put(identifier, object);
    }

    @Override
    protected void doRemove(ID identifier) {
        objects.remove(identifier);
    }

    @Override
    protected void doRemoveAll() {
        objects.clear();
    }

    @Override
    protected Pair<T, LocalDateTime> doFind(ID identifier) {
        return objects.get(identifier);
    }

    @Override
    protected Map<ID, Pair<T, LocalDateTime>> doFindAll() {
        return new LinkedHashMap<>(objects);
    }

    @Override
    protected int doGetSize() {
        return objects.size();
    }
}
