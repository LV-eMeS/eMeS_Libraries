package lv.emes.libraries.tools;

import lv.emes.libraries.utilities.MS_TestUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

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
        assertTrue(builder.isEquals());
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
        assertFalse(builder.areEqual());
    }

    @Test
    public void testListSizesDiffers() {
        List<Integer> integers = new ArrayList<>();
        integers.add(3);
        List<Long> longs = new ArrayList<>();
        builder = buildLists(integers, longs, false);
        assertFalse(builder.areEqual());
    }

    @Test
    public void testEmptyListsEqual() {
        List<Integer> integers = new ArrayList<>();
        List<Long> longs = new ArrayList<>();
        builder = buildLists(integers, longs, false);
        assertTrue(builder.areEqual());
    }

    @Test
    public void testBothListsNull() {
        builder = buildLists(null, null, false);
        assertTrue(builder.areEqual());
    }

    @Test
    public void testFirstListNull() {
        List<Long> longs = new ArrayList<>();
        builder = buildLists(null, longs, false);
        assertFalse(builder.areEqual());
    }

    @Test
    public void testSecondListNull() {
        List<Integer> integers = new ArrayList<>();
        builder = buildLists(integers, null, false);
        assertFalse(builder.areEqual());
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
        assertFalse(builder.areEqual());
    }

    @Test
    public void testFailOnSecondAppend() {
        builder = new MS_EqualityCheckBuilder()
                .append(true, false)
                .append(new Object(), new Object());
        assertFalse(builder.areEqual());
    }

    @Test
    public void testConstructorWithNoParams() {
        builder = new MS_EqualityCheckBuilder();
        assertFalse(builder.getMustBeEqual());
    }

    @Test
    public void testMustBeEqualsSetter() {
        builder = new MS_EqualityCheckBuilder(true).setMustBeEqual(false);
        assertFalse(builder.getMustBeEqual());
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
        assertTrue(builder.areEqual());
    }

    @Test
    public void testCutomExceptionEqualityWorks() {
        Exception exc1;
        Exception exc2;

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2("");
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertNotEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException2((String) null);
        assertEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2();
        exc2 = new MS_TestUtils.MS_UnCheckedException1();
        assertNotEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        assertEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException());
        exc2 = new MS_TestUtils.MS_UnCheckedException2();
        assertNotEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        assertEquals(exc1, exc2);

        exc1 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException("test"));
        exc2 = new MS_TestUtils.MS_UnCheckedException2(new RuntimeException(""));
        assertNotEquals(exc1, exc2);
    }

    private MS_EqualityCheckBuilder buildLists(List<Integer> integers, List<Long> longs, boolean mandatoryEquality) {
        return new MS_EqualityCheckBuilder(mandatoryEquality)
                .appendLists(integers, longs,
                        (integerElement, longElement) -> integerElement.intValue() == longElement
                );
    }
}
