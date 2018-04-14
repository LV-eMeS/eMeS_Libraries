package lv.emes.libraries.tools.flow;

import lv.emes.libraries.utilities.MS_RuntimeExecutionFailureException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * @author eMeS
 * @version 1.0.
 */
public class MS_FlowTest {

    private MS_Flow flow = new MS_Flow();

    private static final IFuncEventFlowAction CONVERT_LONG_TO_INT_EVENT = (input, prevEventIndex, nextEventIndex) -> {
        Long l = (Long) input;
        return l.intValue();
    };

    private static final IFuncEventFlowAction ADD_PLUS_1_EVENT = (input, prevEventIndex, nextEventIndex) -> {
        if (input instanceof Integer) {
            Integer i = (Integer) input;
            return ++i;
        } else {
            Long l = (Long) input;
            nextEventIndex.set(null); //finish after this
            return ++l;
        }
    };

    private static final IFuncEventFlowAction CONVERT_INT_BACK_TO_LONG_EVENT = (input, prevEventIndex, nextEventIndex) -> {
        Integer i = (Integer) input;
        nextEventIndex.set(1); //send back to increment after this event execution
        return Long.valueOf(i);
    };

    @Test
    public void test01NothingToExecute() {
        flow.execute(1L);
        assertEquals(1L, flow.getOutput());
    }

    @Test
    public void test02OneEventToExecute() {
        /*
            (Event index) Input type -> Output type
            (0) Long -> Integer
            Execution order: 0.
        */
        flow.withEvent(CONVERT_LONG_TO_INT_EVENT).execute(1L);
        assertEquals(1, flow.getOutput());
    }

    @Test
    public void test03TwoEventsToExecuteWithoutBranchingAndExceptions() {
        /*
            (Event index) Input type -> Output type
            (0) Long -> Integer
            (1) Integer -> Integer
            Execution order: 0, 1.
        */
        flow.withEvent(CONVERT_LONG_TO_INT_EVENT).withEvent(ADD_PLUS_1_EVENT).execute(1L);
        assertEquals(2, flow.getOutput());
    }

    @Test
    public void test04ManyEventsToExecuteWithBranching() {
        /*
            (Event index) Input type -> Output type
            (0) Long -> Integer
            (1) 1) Integer -> Integer; 2) Long -> Long
            (2) Integer -> Long
            _______________________________________________________
            Execution points | (0)     | (1)     | (2)     | (1)  |
            prevEventIndex   | null    | 0       | 1       | 2    |
            nextEventIndex   | 1       | 2       | 1       | null |
            input type       | Long    | Integer | Integer | Long |
            output type      | Integer | Integer | Long    | Long |
            output value     | 1       | 2       | 2L      | 3L   |
        */
        flow
                .withEvent(CONVERT_LONG_TO_INT_EVENT)
                .withEvent(ADD_PLUS_1_EVENT)
                .withEvent(CONVERT_INT_BACK_TO_LONG_EVENT)
                .execute(1L);
        assertEquals(3L, flow.getOutput());
    }

    @Test
    public void test05OneEventCallsItself() {
        /*
            (Event index) Input type -> Output type
            (0) 1) Integer -> Integer; 2) Long -> Long
            _______________________________________________________
            Execution points | (0)     | (0)     | (0)     | (0)     |
            prevEventIndex   | null    | 0       | 0       | 0       |
            nextEventIndex   | 0       | 0       | 0       | null    |
            input type       | Integer | Integer | Integer | Integer |
            output type      | Integer | Integer | Integer | Integer |
            output value     | 6       | 7       | 8       | 9       |
        */
        flow.withEvent((input, prevEventIndex, nextEventIndex) -> {
            Integer i = (Integer) input;
            if (++i < 9) {
                nextEventIndex.set(0);
            }
            return i;
        }).execute(5);
        assertEquals(9, flow.getOutput());
    }

    @Test
    public void test06KeepExecutingSameEventUntilSituationChanges() {
/*
    (Event index) Input type -> Output type
    (0) Long -> Integer
    (1) 1) Integer -> Integer; 2) Long -> Long
    (2) 1) Integer -> Integer; 2) Integer -> Long
    (3) Integer -> Long
    _____________________________________________________________________________________________________________________________
    Execution points | (0)     | (1)     | (2)     | (1)     | (2)     | (1)     | (2)     | (1)     | (2)     | (3)     | (1)  |
    prevEventIndex   | null    | 0       | 1       | 2       | 1       | 2       | 1       | 2       | 1       | 2       | 3    |
    nextEventIndex   | 1       | 2       | 1       | 2       | 1       | 2       | 1       | 2       | 3       | 1       | null |
    input type       | Long    | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Long |
    output type      | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Integer | Long    | Long |
    output value     | 1       | 2       | 2       | 3       | 3       | 4       | 4       | 5       | 5       | 5L      | 6L   |
*/
        flow
                .withEvent(CONVERT_LONG_TO_INT_EVENT)
                .withEvent(ADD_PLUS_1_EVENT)
                .withEvent((input, prevEventIndex, nextEventIndex) -> {
                    Integer in = (Integer) input;
                    if (in < 5) {
                        nextEventIndex.set(1);
                    }
                    return in;
                })
                .withEvent(CONVERT_INT_BACK_TO_LONG_EVENT)
                .execute(1L);
        assertEquals(6L, flow.getOutput());
    }

    @Test(expected = MS_RuntimeExecutionFailureException.class)
    public void test11TwoEventsToExecuteButSecondFailedWithException() {
        /*
            (Event index) Input type -> Output type
            (0) Long -> Integer
            (1) Long -> Integer >X<
        */
        flow.withEvent(CONVERT_LONG_TO_INT_EVENT).withEvent(CONVERT_LONG_TO_INT_EVENT).execute(1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test21RealLifeExampleLoginScenario() {
        Pair<String, String> credentials;
        Pair<String, String> dbCredentials = Pair.of("John", "Doe");
        AtomicReference<Pair<String, String>> dbCredentialsCached = new AtomicReference<>(null);

        AtomicBoolean dbCalled = new AtomicBoolean(false);

        /*  Scenario:
            0. Check cache against existing correct credentials, if exists, go to verify event (2);
            1. Get correct credentials from DB and write those into cache, then go to verify event (2);
            2. Verify, if input credentials matches correct cached credentials. End.

            (Event index) Input type -> Output type
            (0) Pair -> Pair
            (1) Pair -> Pair
            (2) Pair -> null; Pair -> Pair
        */
        flow
                .withEvent((input, prevEventIndex, nextEventIndex) -> {
                    if (dbCredentialsCached.get() != null) nextEventIndex.set(2);
                    return input;
                })
                .withEvent((input, prevEventIndex, nextEventIndex) -> {
                    dbCredentialsCached.set(dbCredentials);
                    dbCalled.set(true);
                    return input;
                })
                .withEvent((input, prevEventIndex, nextEventIndex) -> {
                    Pair<String, String> inputCredentials = (Pair<String, String>) input;
                    if (dbCredentialsCached.get().equals(inputCredentials)) return inputCredentials;
                    else return null;
                })
                .execute(Pair.of("Maris", "Doe")); //invalid creds

        assertNull(flow.getOutput()); //first attempt failed due to wrong credentials
        assertTrue(dbCalled.get());

        //do second attempt
        dbCalled.set(false);
        flow.execute(Pair.of("John", "abc123"));
        assertNull(flow.getOutput()); //second attempt failed as well
        assertFalse(dbCalled.get()); //but this time DB not involved, cause creds already cached

        //do third attempt
        dbCalled.set(false);
        flow.execute(Pair.of("John", "Doe"));
        assertNotNull(flow.getOutput());
        assertFalse(dbCalled.get());

        //check if returned credentials are correct ones
        credentials = (Pair<String, String>) flow.getOutput();
        assertEquals("John", credentials.getLeft());
        assertEquals("Doe", credentials.getRight());
    }
}