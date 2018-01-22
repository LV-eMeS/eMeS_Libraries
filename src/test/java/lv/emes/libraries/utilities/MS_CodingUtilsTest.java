package lv.emes.libraries.utilities;

import org.junit.Test;

import java.util.Map;

import static lv.emes.libraries.utilities.MS_CodingUtils.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author eMeS
 * @version 1.0.
 */
public class MS_CodingUtilsTest {

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
        assertEquals(null, arr[1]);
        assertEquals(0, arr[2]);
    }

    @Test
    public void testNewSingletonMap() {
        int key = 2;
        Object value = new Object();
        Map<Integer, Object> map = newSingletonMap(key, value);
        assertEquals(value, map.get(key));
    }
}