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

    public MS_InMemoryCachingRepository(String repositoryRoot, String cacheName) {
        super(repositoryRoot, cacheName, true);
    }

    @Override
    protected boolean isInitialized() {
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
