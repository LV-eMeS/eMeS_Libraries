package lv.emes.libraries.tools;

import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test for MS_EqualityCheckBuilder utility.
 * Here is best to test just <b>appendLists</b> method because it already includes calls to <b>append</b>.
 *
 * @author eMeS
 */
public class MS_EqualityCheckBuilderTest {

    private MS_EqualityCheckBuilder builder;

    @Test
    public void testListsEqual() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        integers.add(6);
        List<Long> longs = new ArrayList<>();
        longs.add(3L);
        longs.add(6L);
        builder = buildLists(integers, longs, false);
        assertThat(builder.isEquals()).isTrue();
    }

    @Test
    public void testListsNotEqual() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        integers.add(909);
        List<Long> longs = new ArrayList<>();
        longs.add(3L);
        longs.add(6L);
        builder = buildLists(integers, longs, false);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testListSizesDiffers() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        List<Long> longs = new ArrayList<>();
        builder = buildLists(integers, longs, false);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testEmptyListsEqual() {
        List<Integer> integers = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        builder = buildLists(integers, longs, false);
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testBothListsNull() {
        builder = buildLists(null, null, false);
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testFirstListNull() {
        List<Long> longs = new ArrayList<>();
        builder = buildLists(null, longs, false);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testSecondListNull() {
        List<Integer> integers = new ArrayList<>();
        builder = buildLists(integers, null, false);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test(expected = AssertionError.class)
    public void testFirstListNull_WithInterruption() {
        List<Long> longs = new ArrayList<>();
        builder = buildLists(null, longs, true);
    }

    @Test(expected = AssertionError.class)
    public void testSecondListNull_WithInterruption() {
        List<Integer> integers = new ArrayList<>();
        builder = buildLists(integers, null, true);
    }

    @Test(expected = AssertionError.class)
    public void testListsNotEqual_WithInterruption() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        List<Long> longs = new ArrayList<>();
        longs.add(6L);
        builder = buildLists(integers, longs, true);
    }

    @Test
    public void testNeedToPerformComparisonAfterNullChecks() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        List<Long> longs = new ArrayList<>();
        longs.add(6L);
        builder = buildLists(integers, longs, false);
        builder.append(true, true, (f, s) -> true); //a call just to
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testFailOnSecondAppend() {
        builder = new MS_EqualityCheckBuilder()
                .append(true, false)
                .append(new Object(), new Object());
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testConstructorWithNoParams() {
        builder = new MS_EqualityCheckBuilder();
        assertThat(builder.getMustBeEqual()).isFalse();
    }

    @Test
    public void testMustBeEqualsSetter() {
        builder = new MS_EqualityCheckBuilder(true).setMustBeEqual(false);
        assertThat(builder.getMustBeEqual()).isFalse();
    }

    @Test
    public void testDifferentTypesOfAppendParams() {
        builder = new MS_EqualityCheckBuilder(false)
                .append(1L, 1L)
                .append(1, 1)
                .append((short) 1, (short) 1)
                .append('a', 'a')
                .append((byte) 1, (byte) 1)
                .append(1D, 1D)
                .append(1F, 1F)
                .append(new Object[]{}, new Object[]{})
                .append(new long[]{}, new long[]{})
                .append(new int[]{}, new int[]{})
                .append(new short[]{}, new short[]{})
                .append(new char[]{}, new char[]{})
                .append(new byte[]{}, new byte[]{})
                .append(new double[]{}, new double[]{})
                .append(new float[]{}, new float[]{})
                .append(new boolean[]{}, new boolean[]{})
        ;
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testCutomExceptionEqualityWorks() {
        Exception exc1;
        Exception exc2;

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertThat(exc2).isEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2("");
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertThat(exc2).isNotEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException2((String) null);
        assertThat(exc2).isEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException1();
        assertThat(exc2).isNotEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        assertThat(exc2).isEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertThat(exc2).isNotEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        assertThat(exc2).isEqualTo(exc1);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException(""));
        assertThat(exc2).isNotEqualTo(exc1);
    }

    //*** Map equality comparison ***

    @Test
    public void testMapsEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        integers.put(2, 6);
        Map<Integer, Long> longs = new HashMap<>();
        longs.put(1, 3L);
        longs.put(2, 6L);
        builder = buildMaps(integers, longs);
        assertThat(builder.isEquals()).isTrue();
    }

    @Test
    public void testMapsNotEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        integers.put(2, 919);
        Map<Integer, Long> longs = new HashMap<>();
        longs.put(1, 3L);
        longs.put(2, 6L);
        builder = buildMaps(integers, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testMapSizesDiffers() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        Map<Integer, Long> longs = new HashMap<>();
        longs.put(1, 3L);
        longs.put(2, 6L);
        builder = buildMaps(integers, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testEmptyMapsEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        Map<Integer, Long> longs = new HashMap<>();
        builder = buildMaps(integers, longs);
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testBothMapsNull() {
        builder = buildMaps(null, null);
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testFirstMapNull() {
        Map<Integer, Long> longs = new HashMap<>();
        builder = buildMaps(null, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testSecondMapNull() {
        Map<Integer, Integer> integers = new HashMap<>();
        builder = buildMaps(integers, null);
        assertThat(builder.areEqual()).isFalse();
    }

    //*** Map with different types of IDs equality comparison ***

    @Test
    public void testMapsDifferentIdEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        integers.put(2, 6);
        Map<Long, Long> longs = new HashMap<>();
        longs.put(1L, 3L);
        longs.put(2L, 6L);
        builder = buildMapsDifferentIdTypes(integers, longs);
        assertThat(builder.isEquals()).isTrue();
    }

    @Test
    public void testMapsDifferentIdNotEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        integers.put(2, 919);
        Map<Long, Long> longs = new HashMap<>();
        longs.put(1L, 3L);
        longs.put(2L, 6L);
        builder = buildMapsDifferentIdTypes(integers, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testMapsDifferentIdSizesDiffers() {
        Map<Integer, Integer> integers = new HashMap<>();
        integers.put(1, 3);
        Map<Long, Long> longs = new HashMap<>();
        longs.put(1L, 3L);
        longs.put(2L, 6L);
        builder = buildMapsDifferentIdTypes(integers, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    @Test
    public void testEmptyMapsDifferentIdEqual() {
        Map<Integer, Integer> integers = new HashMap<>();
        Map<Long, Long> longs = new HashMap<>();
        builder = buildMapsDifferentIdTypes(integers, longs);
        assertThat(builder.areEqual()).isTrue();
    }

    @Test
    public void testFirstMapDifferentIdNull() {
        Map<Long, Long> longs = new HashMap<>();
        builder = buildMapsDifferentIdTypes(null, longs);
        assertThat(builder.areEqual()).isFalse();
    }

    private static final MS_EqualityCheckBuilder.IComparisonAlgorithm<Integer, Long> INTEGER_AND_LONG_COMPARISON =
            (integerElement, longElement) -> integerElement.intValue() == longElement;

    private MS_EqualityCheckBuilder buildLists(List<Integer> integers, List<Long> longs, boolean mandatoryEquality) {
        return new MS_EqualityCheckBuilder(mandatoryEquality)
                .appendLists(integers, longs, INTEGER_AND_LONG_COMPARISON);
    }

    private MS_EqualityCheckBuilder buildMaps(Map<Integer, Integer> integers, Map<Integer, Long> longs) {
        return new MS_EqualityCheckBuilder(false)
                .appendMaps(integers, longs, INTEGER_AND_LONG_COMPARISON);
    }

    private MS_EqualityCheckBuilder buildMapsDifferentIdTypes(Map<Integer, Integer> integers, Map<Long, Long> longs) {
        return new MS_EqualityCheckBuilder(false)
                .appendMaps(integers, longs, INTEGER_AND_LONG_COMPARISON, INTEGER_AND_LONG_COMPARISON);
    }
}
