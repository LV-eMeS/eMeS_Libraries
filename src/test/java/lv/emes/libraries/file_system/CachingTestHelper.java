package lv.emes.libraries.file_system;

import lv.emes.libraries.storage.MS_Cache;
import lv.emes.libraries.storage.MS_CachingRepository;
import lv.emes.libraries.tools.logging.MS_InMemoryLoggingRepository;
import lv.emes.libraries.tools.logging.MS_MultiLogger;
import lv.emes.libraries.tools.logging.MS_MultiLoggingSetup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Helper class that unifies cache repository testing approaches.
 * It defines methods that simply needs to be called in tests.
 *
 * @param <T>  type of objects that are going to be cached.
 * @param <ID> type of object identifiers.
 * @author eMeS
 * @version 1.0.
 */
public class CachingTestHelper<T, ID> {

    public static final String FIRST = "First object will stay there until it will be removed manually";
    public static final String SECOND = "Second object will become expired after 1 second";

    private static MS_Cache cache;
    private static MS_MultiLogger logger;
    private static MS_InMemoryLoggingRepository logs;

    public void init(MS_CachingRepository<T, ID> repository) {
        logs = new MS_InMemoryLoggingRepository();
        logger = new MS_MultiLogger(new MS_MultiLoggingSetup().withRepository(logs));
        cache = new MS_Cache<>(repository);
        cache.setDefaultTTL(1);
        cache.setLogger(logger);
    }

    public void checkSetParams() {
        assertEquals(1, cache.getDefaultTTL());
        assertEquals(logger, cache.getLogger());
        assertTrue("Repository is not initialized, but it should've been on init() step",
                cache.getRepository().isInitialized());
    }

    //*** Getters ***

    @SuppressWarnings("unchecked")
    public <T, ID> MS_Cache<T, ID> getCache() {
        return cache;
    }

    public MS_MultiLogger getLogger() {
        return logger;
    }

    public MS_InMemoryLoggingRepository getLogs() {
        return logs;
    }
}
