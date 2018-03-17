package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import static lv.emes.libraries.utilities.MS_RepositoryUtils.retrieveObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 *
 * @author eMeS
 */
public class MS_RepositoryUtilsTest {

    //*** Object retrieval tests ***

    @Test
    public void testRetrieveObject() {
        final Integer objectToRetrieveId = 1;
        final String expected = "Object";
        String retrievedObject;
        retrievedObject = retrieveObject(objectToRetrieveId, true, (id) -> expected);
        assertEquals(expected, retrievedObject);
    }

    @Test
    public void testRetrieveObjectNullRetrievalOperationSet() {
        final Integer objectToRetrieveId = 1;
        String retrievedObject;
        retrievedObject = retrieveObject(objectToRetrieveId, true, (id) -> null);
        assertNull(retrievedObject);
    }

    @Test
    public void testRetrieveObjectOperationDidNotFindObject() {
        final Integer objectToRetrieveId = 1;
        String retrievedObject;
        retrievedObject = retrieveObject(objectToRetrieveId, true, (id) -> {
            if (id == 5)
                return "Some object";
            else
                return null;
        });
        assertNull(retrievedObject);
    }

    @Test
    public void testRetrieveObjectFoundOnSecondAttempt() {
        final Integer objectToRetrieveId = 1;
        final String expected = "Second operation worked";
        String retrievedObject;
        retrievedObject = retrieveObject(objectToRetrieveId, true,
                (id) -> null,
                (id) -> "Second operation worked"
        );
        assertEquals(expected, retrievedObject);
    }

    @Test
    public void testRetrieveObjectErrorOnFirstAttemptButContinue() {
        final Integer objectToRetrieveId = 1;
        final String expected = "Second operation worked";
        String retrievedObject;
        retrievedObject = retrieveObject(objectToRetrieveId, true, (id) -> {
                    throw new MS_BadSetupException("Some problems here");
                },
                (id) -> "Second operation worked"
        );
        assertEquals(expected, retrievedObject);
    }

    @Test(expected = MS_ObjectRetrievalFailureException.class)
    public void testRetrieveObjectErrorOnFirstAttemptAndFail() {
        retrieveObject(1, false, (id) -> {
                    throw new MS_BadSetupException("Some problems here");
                },
                (id) -> "Second operation will not be triggered here"
        );
    }
}