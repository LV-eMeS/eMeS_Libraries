package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.MS_BadSetupException;
import lv.emes.libraries.tools.lists.MS_List;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static lv.emes.libraries.utilities.MS_CodingUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

public class MS_CodingUtilsTest {

    @Test
    public void testGetIPAddress() {
        assertThat(getIPAddress()).isNotNull();
    }

    @Test
    public void testRandomNumber() {
        int res = randomNumber(5, -5);
        assertThat(inRange(res, -5, 5)).as("Generated random number is not in range [-5..5]").isTrue();
    }

    @Test
    public void testInRange() {
        assertThat(inRange(2, 1, 3)).isTrue();
        assertThat(inRange(-2, -1, -3)).isTrue();
        assertThat(inRange(1, 1, 1)).isTrue();
        assertThat(inRange(10, 1, 1)).isFalse();
        assertThat(inRange(10, -1, 5)).isFalse();
    }

    @SuppressWarnings("all")
    @Test
    public void testInverseBoolean() {
        assertThat(inverseBoolean(false)).isTrue();
        assertThat(inverseBoolean(true)).isFalse();
    }

    @Test
    public void testBooleanToChar() {
        assertThat(booleanToChar(true)).isEqualTo((Character) '1');
        assertThat(booleanToChar(false)).isEqualTo((Character) '0');
    }

    @Test
    public void testCharToBoolean() {
        assertThat(charToBoolean('1')).isEqualTo(true);
        assertThat(charToBoolean('0')).isEqualTo(false);
    }

    @Test
    public void testRound() {
        assertThat(round(-6.4444, 0)).isCloseTo(-6, offset(0.0));
        assertThat(round(1.17, 0)).isCloseTo(1, offset(0.0));
        assertThat(round(1.17, 2)).isCloseTo(1.17, offset(0.0));
        assertThat(round(0.17, 2)).isCloseTo(0.17, offset(0.0));
        assertThat(round(0.17, 1)).isCloseTo(0.2, offset(0.0));
    }

    @Test
    public void testTruncate() {
        assertThat(truncate(0.17)).isEqualTo(0);
        assertThat(truncate(-6.17)).isEqualTo(-6);
        assertThat(truncate(1.11111111111111111111111111111111111111111111111111)).isEqualTo(1);
        assertThat(truncate(9.999999999999999)).isEqualTo(9);
    }

    @Test
    public void testFractionalPart() {
        assertThat(fractionalPart(3.14, 2)).isCloseTo(0.14, offset(0.0));
        assertThat(fractionalPart(3.14, 1)).isCloseTo(0.1, offset(0.0));
        assertThat(fractionalPart(3.14, 0)).isCloseTo(0, offset(0.0));

        assertThat(fractionalPart(-10.1, 1)).isCloseTo(-0.1, offset(0.0));
        assertThat(fractionalPart(0.0, 0)).isCloseTo(0.0, offset(0.0));
        assertThat(fractionalPart(27.013001985, 10)).isCloseTo(0.013001985, offset(0.0));
        assertThat(fractionalPart(0.999999999, 9)).isCloseTo(0.999999999, offset(0.0));
    }

    @Test
    public void testGetArray() {
        Object[] arr;
        arr = newArray();
        assertThat(arr.length).isEqualTo(0);

        arr = newArray("", null, 0);
        assertThat(arr.length).isEqualTo(3);
        assertThat(arr[0]).isEqualTo("");
        assertThat(arr[1]).isNull();
        assertThat(arr[2]).isEqualTo(0);
    }

    @Test
    public void testNewSingletonMap() {
        int key = 2;
        Object value = new Object();
        Map<Integer, Object> map = newSingletonMap(key, value);
        assertThat(map.get(key)).isEqualTo(value);
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

        assertThat(count.get()).isEqualTo(4);
        assertThat(sum.get()).isEqualTo(234);

        //break right away
        sum.set(0);
        count.set(0);
        forEach(list, (number, breakLoop) -> {
            sum.addAndGet(number);
            count.incrementAndGet();
            breakLoop.set(true);
        });

        assertThat(count.get()).isEqualTo(1);
        assertThat(sum.get()).isEqualTo(17);

        //break when sum is greater than 20
        sum.set(0);
        count.set(0);
        forEach(list, (number, breakLoop) -> {
            sum.addAndGet(number);
            count.incrementAndGet();
            if (sum.get() > 20) breakLoop.set(true);
        });

        assertThat(count.get()).isEqualTo(3);
        assertThat(sum.get()).isEqualTo(238);
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
        assertThat(success.get()).isTrue();
        assertThat(executionTimes.get()).isEqualTo(1);
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
        assertThat(success.get()).isTrue();
        assertThat(executionTimes.get()).isEqualTo(2);
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
            assertThat(e instanceof MS_BadSetupException).isTrue();
            assertThat(success.get()).isFalse();
            assertThat(executionTimes.get()).isEqualTo(1);
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
            assertThat(e instanceof MS_ExecutionFailureException).isTrue();
            assertThat(e.getCause() instanceof IllegalAccessException).isTrue();
            assertThat(success.get()).isFalse();
            assertThat(executionTimes.get()).isEqualTo(2);
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
        assertThat(executionTimes.get()).isEqualTo(2);
        assertThat(success.get()).isTrue();
        assertThat(actionBetweenRetriesPerformed.get()).isTrue();
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
            assertThat(e.getCause() instanceof InterruptedException).isTrue();
            assertThat(executionTimes.get()).isEqualTo(1);
            assertThat(success.get()).isFalse();
            assertThat(actionBetweenRetriesPerformed.get()).isFalse();
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
        assertThat(getMapElementKey(map, 0)).isEqualTo(expected);
        expected = 6;
        assertThat(getMapElementKey(map, 1)).isEqualTo(expected);
        expected = 1;
        assertThat(getMapElementKey(map, 2)).isEqualTo(expected);

        assertThat(getMapElementValue(map, 0)).isEqualTo("Test");
        assertThat(getMapElementValue(map, 1)).isEqualTo("6");
        assertThat(getMapElementValue(map, 2)).isEqualTo("XYZ");
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
        assertThat(nullSafe(() -> methodThatReturnsNull.get().get())).isNull(); //first method call failed

        final String SUCCESS_RESULT = "This time value is there, as first method returned some object, which method also returned value (this String)";
        Supplier<String> secondMethod = () -> SUCCESS_RESULT;
        Supplier<Supplier<String>> firstMethod = () -> secondMethod;
        assertThat(nullSafe(() -> firstMethod.get().get())).isEqualTo(SUCCESS_RESULT); //both method calls succeeded
    }

    @Test
    public void testGetFromSysOrEnvProperty() {
        final String existingIntegerKey = "testGetFromSysOrEnvProperty.existingIntegerKey";
        final String existingNullKey = "testGetFromSysOrEnvProperty.existingNullKey";
        final Integer existingIntegerValue = 5;
        final String notExistingKey = UUID.randomUUID().toString();
        System.getProperties().put(existingIntegerKey, existingIntegerValue.toString());
        System.getProperties().put(existingNullKey, MS_StringUtils.NULL_STRING);

        assertThat(MS_CodingUtils.getFromSysOrEnvProperty(existingIntegerKey, Integer.class)).isExactlyInstanceOf(Optional.class)
                .isEqualTo(Optional.of(existingIntegerValue));
        assertThat(MS_CodingUtils.getFromSysOrEnvProperty(existingIntegerKey, String.class)).isExactlyInstanceOf(Optional.class)
                .isEqualTo(Optional.of(existingIntegerValue.toString()));
        assertThat(MS_CodingUtils.getFromSysOrEnvProperty(existingNullKey, String.class)).isExactlyInstanceOf(Optional.class)
                .isEqualTo(Optional.empty());
        assertThat(MS_CodingUtils.getFromSysOrEnvProperty(notExistingKey, String.class)).isExactlyInstanceOf(Optional.class)
                .isEqualTo(Optional.empty());
    }

    //*** getMapElementKey and getMapElementValue test end ***
}