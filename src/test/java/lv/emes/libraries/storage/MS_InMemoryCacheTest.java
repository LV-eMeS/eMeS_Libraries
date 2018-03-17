package lv.emes.libraries.storage;

import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_InMemoryCacheTest {

    private static MS_InMemoryCache<Object, String> repository = new MS_InMemoryCache<>();

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
            throw new MS_TestUtils.MS_CheckedException("Exception occurred");
        });
    }
}