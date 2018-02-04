package lv.emes.libraries.storage;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author eMeS
 * @version 1.0.
 */
public class MSCachedObjectRepositoryTest {

    private static MS_CachedObjectRepository<String, Object> repository = new MS_CachedObjectRepository<>();

    @Test
    public void test01RetrievalOperations() {
        final String FIRST = "First object";
        final String SECOND = "Second object";

        Object obj = repository.get(FIRST, id -> new Object());
        assertNotNull(obj);
        Object sameObject = repository.get(FIRST, id -> new Object());
        assertEquals(obj, sameObject);

        //now to check that object with different ID is not the same
        Object secondObj = repository.get(SECOND, id -> new Object());
        assertNotEquals(obj, secondObj);
    }

    @Test(expected = NullPointerException.class)
    public void test02NullIdentifier() {
        repository.get(null, id -> new Object());
    }

    @Test(expected = NullPointerException.class)
    public void test03NullretrievalOperation() {
        repository.get("", null);
    }

    @Test(expected = RuntimeException.class)
    public void test04RuntimeInRetrievalOperation() {
        repository.get("", (id) -> {
            throw new RuntimeException("Exception occurred");
        });
    }
}