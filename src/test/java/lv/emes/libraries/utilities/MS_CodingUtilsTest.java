package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.lists.MS_List;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static lv.emes.libraries.utilities.MS_CodingUtils.*;
import static org.junit.Assert.*;

public class MS_CodingUtilsTest {

    @Test
    public void testGetIPAddress() {
        assertNotNull(getIPAddress());
    }

    @Test
    public void testRandomNumber() {
        int res = randomNumber(5, -5);
        assertTrue("Generated random number is not in range [-5..5]", inRange(res, -5, 5));
    }

    @Test
    public void testInRange() {
        assertTrue(inRange(2, 1, 3));
        assertTrue(inRange(-2, -1, -3));
        assertTrue(inRange(1, 1, 1));
        assertFalse(inRange(10, 1, 1));
        assertFalse(inRange(10, -1, 5));
    }

    @SuppressWarnings("all")
    @Test
    public void testInverseBoolean() {
        assertTrue(inverseBoolean(false));
        assertFalse(inverseBoolean(true));
    }

    @Test
    public void testBooleanToChar() {
        assertEquals((Character) '1', booleanToChar(true));
        assertEquals((Character) '0', booleanToChar(false));
    }

    @Test
    public void testCharToBoolean() {
        assertEquals(true, charToBoolean('1'));
        assertEquals(false, charToBoolean('0'));
    }

    @Test
    public void testRound() {
        assertEquals(-6, round(-6.4444, 0), 0.0);
        assertEquals(1, round(1.17, 0), 0.0);
        assertEquals(1.17, round(1.17, 2), 0.0);
        assertEquals(0.17, round(0.17, 2), 0.0);
        assertEquals(0.2, round(0.17, 1), 0.0);
    }

    @Test
    public void testTruncate() {
        assertEquals(0, truncate(0.17));
        assertEquals(-6, truncate(-6.17));
        assertEquals(1, truncate(1.11111111111111111111111111111111111111111111111111));
        assertEquals(9, truncate(9.999999999999999));
    }

    @Test
    public void testFractionalPart() {
        assertEquals(0.14, fractionalPart(3.14, 2), 0.0);
        assertEquals(0.1, fractionalPart(3.14, 1), 0.0);
        assertEquals(0, fractionalPart(3.14, 0), 0.0);

        assertEquals(-0.1, fractionalPart(-10.1, 1), 0.0);
        assertEquals(0.0, fractionalPart(0.0, 0), 0.0);
        assertEquals(0.013001985, fractionalPart(27.013001985, 10), 0.0);
        assertEquals(0.999999999, fractionalPart(0.999999999, 9), 0.0);
    }

    @Test
    public void testGetArray() {
        Object[] arr;
        arr = newArray();
        assertEquals(0, arr.length);

        arr = newArray("", null, 0);
        assertEquals(3, arr.length);
        assertEquals("", arr[0]);
        assertNull(arr[1]);
        assertEquals(0, arr[2]);
    }

    @Test
    public void testNewSingletonMap() {
        int key = 2;
        Object value = new Object();
        Map<Integer, Object> map = newSingletonMap(key, value);
        assertEquals(value, map.get(key));
    }

    @Test
    public void testForEach() {
        MS_List<Integer> list = new MS_List<>();
        final AtomicInteger sum = new AtomicInteger(0);
        final AtomicInteger count = new AtomicInteger(0);

        list.add(17);
        list.add(1);
        list.add(220);
        list.add(-4);

        forEach(list, (number, breakLoop) -> {
            sum.addAndGet(number);
            count.incrementAndGet();
        });

        assertEquals(4, count.get());
        assertEquals(234, sum.get());

        //break right away
        sum.set(0);
        count.set(0);
        forEach(list, (number, breakLoop) -> {
            sum.addAndGet(number);
            count.incrementAndGet();
            breakLoop.set(true);
        });

        assertEquals(1, count.get());
        assertEquals(17, sum.get());

        //break when sum is greater than 20
        sum.set(0);
        count.set(0);
        forEach(list, (number, breakLoop) -> {
            sum.addAndGet(number);
            count.incrementAndGet();
            if (sum.get() > 20) breakLoop.set(true);
        });

        assertEquals(3, count.get());
        assertEquals(238, sum.get());
    }

    //*** executeWithRetry tests ***

    @Test
    public void testExecuteWithRetrySuccessOnFirstAttempt() throws MS_ExecutionFailureException {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        executeWithRetry(5, () -> {
            success.set(true);
            executionTimes.incrementAndGet();
        });
        assertTrue(success.get());
        assertEquals(1, executionTimes.get());
    }

    @Test
    public void testExecuteWithRetrySuccessOnSecondAttempt() throws MS_ExecutionFailureException {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        executeWithRetry(5, () -> {
            if (executionTimes.incrementAndGet() < 2) {
                throw new Exception();
            }
            success.set(true);
        });
        assertTrue(success.get());
        assertEquals(2, executionTimes.get());
    }

    @Test
    public void testExecuteWithRetryFailureDueToBug2OutOf2() {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        try {
            executeWithRetry(2, () -> {
                executionTimes.incrementAndGet();
                throw new MS_BadSetupException("This is a complete failure");
            });
        } catch (Exception e) {
            assertTrue(e instanceof MS_BadSetupException);
            assertFalse(success.get());
            assertEquals(1, executionTimes.get());
        }
    }

    @Test
    public void testExecuteWithRetryFailure2OutOf2() {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        try {
            executeWithRetry(2, () -> {
                executionTimes.incrementAndGet();
                throw new IllegalAccessException("This is a complete failure");
            });
        } catch (Exception e) {
            assertTrue(e instanceof MS_ExecutionFailureException);
            assertTrue(e.getCause() instanceof IllegalAccessException);
            assertFalse(success.get());
            assertEquals(2, executionTimes.get());
        }
    }

    @Test
    public void testExecuteWithRetrySuccessOnSecondAttemptAndActionBetweenRetries() throws MS_ExecutionFailureException {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean actionBetweenRetriesPerformed = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        executeWithRetry(5, () -> {
            if (executionTimes.incrementAndGet() < 2) {
                throw new Exception();
            }
            success.set(true);
        }, () -> {
            actionBetweenRetriesPerformed.set(true);
        });
        assertEquals(2, executionTimes.get());
        assertTrue(success.get());
        assertTrue(actionBetweenRetriesPerformed.get());
    }

    @Test
    public void testExecuteWithRetrySuccessOnSecondAttemptAndActionBetweenRetriesFailed() {
        AtomicBoolean success = new AtomicBoolean(false);
        AtomicBoolean actionBetweenRetriesPerformed = new AtomicBoolean(false);
        AtomicInteger executionTimes = new AtomicInteger(0);
        try {
            executeWithRetry(5, () -> {
                if (executionTimes.incrementAndGet() < 2) {
                    throw new Exception();
                }
                success.set(true);
            }, () -> {
                throw new InterruptedException();
            });
        } catch (MS_ExecutionFailureException e) {
            assertTrue(e.getCause() instanceof InterruptedException);
            assertEquals(1, executionTimes.get());
            assertFalse(success.get());
            assertFalse(actionBetweenRetriesPerformed.get());
        }
    }

    @Test(expected = MS_BadSetupException.class)
    public void testExecuteWithRetryNegativeAmountOfTimesToRun() throws MS_ExecutionFailureException {
        executeWithRetry(-2, () -> {
        });
    }

    @Test(expected = MS_BadSetupException.class)
    public void testExecuteWithRetry0AmountOfTimesToRun() throws MS_ExecutionFailureException {
        executeWithRetry(0, () -> {
        });
    }

    @Test(expected = MS_BadSetupException.class)
    public void testExecuteWithRetryNoActionProvided() throws MS_ExecutionFailureException {
        executeWithRetry(-2, null);
    }

    //*** executeWithRetry test end ***

    @Test
    public void testGetMapItemKeyAndValue() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(5, "Test");
        map.put(6, "6");
        map.put(1, "XYZ");

        Integer expected;
        expected = 5;
        assertEquals(expected, getMapElementKey(map, 0));
        expected = 6;
        assertEquals(expected, getMapElementKey(map, 1));
        expected = 1;
        assertEquals(expected, getMapElementKey(map, 2));

        assertEquals("Test", getMapElementValue(map, 0));
        assertEquals("6", getMapElementValue(map, 1));
        assertEquals("XYZ", getMapElementValue(map, 2));
    }

    @Test(expected = MS_BadSetupException.class)
    public void testGetMapItemKeyIndexOutOfBounds() {
        Map<Integer, String> map = new LinkedHashMap<>();
        getMapElementKey(map, 0);
    }

    @Test(expected = MS_BadSetupException.class)
    public void testGetMapItemKeyIndexOutOfBoundsNegative() {
        Map<Integer, String> map = new LinkedHashMap<>();
        getMapElementKey(map, -1);
    }

    @Test(expected = MS_BadSetupException.class)
    public void testGetMapItemValueIndexOutOfBounds() {
        Map<Integer, String> map = new LinkedHashMap<>();
        getMapElementKey(map, 6);
    }

    @Test
    public void testNullSafe() {
        Supplier<Supplier<String>> methodThatReturnsNull = () -> null;
        Assertions.assertThat(nullSafe(() -> methodThatReturnsNull.get().get())).isNull(); //first method call failed

        final String SUCCESS_RESULT = "This time value is there, as first method returned some object, which method also returned value (this String)";
        Supplier<String> secondMethod = () -> SUCCESS_RESULT;
        Supplier<Supplier<String>> firstMethod = () -> secondMethod;
        Assertions.assertThat(nullSafe(() -> firstMethod.get().get())).isEqualTo(SUCCESS_RESULT); //both method calls succeeded

    }

    //*** getMapElementKey and getMapElementValue test end ***
}