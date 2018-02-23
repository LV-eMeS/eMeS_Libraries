package lv.emes.libraries.tools.lists;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSListTest {
    private MS_List<Integer> l = new MS_List<>();
    private int count, sum, indexSum;

    @Test
    public void test01ListEngine() {
        assertTrue(l.count() == 0);
        l.add(1);
        assertTrue(l.get(0) == 1);
        assertTrue(l.count() > 0);
        l.add(2);
        l.add(3);
        l.add(4);
        l.remove(2);
        assertTrue(l.count() == 3);
        l.indexOfCurrent = 2;
        assertTrue(l.current() == 4);
        //fiziski notestējam apstaigāšanu
        l.last();
        while (l.currentIndexInsideTheList()) {
            System.out.println(l.current());
            l.prev();
        }
    }

    @Test
    public void test02ToArray() {
        l.add(5);
        l.add(5);
        l.add(3);
        Object[] intArr = l.toArray();
        assertEquals(5, intArr[0]);
        assertEquals(5, intArr[1]);
        assertEquals(3, intArr[2]);
    }

    @Test
    public void test03DoWithEveryItem() {
        l.add(17);
        l.add(1);
        l.add(-4);

        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(14, sum);
        assertEquals(3, indexSum);
        assertEquals(3, count);

        //now to test for loop starting from second element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(-3, sum); //1-4
        assertEquals(3, indexSum); //1+2
        assertEquals(2, count); //only 2 elements should be scanned (second and third)

        //now to test for loop starting from last element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(l.count()-1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(-4, sum);
        assertEquals(2, indexSum);
        assertEquals(1, count);

        //now to test for loop starting from non existing element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(156, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        //nothing should be changed
        assertEquals(0, sum);
        assertEquals(0, indexSum);
        assertEquals(0, count);

        //now try to work with non existing elements from lower index boundary
        l.forEachItem(-15, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        //still nothing should be changed
        assertEquals(0, sum);
        assertEquals(0, indexSum);
        assertEquals(0, count);

        //now to test for loop starting from 2nd element and ending with the same element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(1, sum);
        assertEquals(1, indexSum);
        assertEquals(1, count);

        //test when start and end elements are switched
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 0, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(18, sum);
        assertEquals(1, indexSum);
        assertEquals(2, count);

        //test when end element is out of range
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 1230, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(0, sum);
        assertEquals(0, indexSum);
        assertEquals(0, count);
    }

    @Test
    public void test04DoWithEveryItemAndBreak() {
        l.add(17);
        l.add(1);
        l.add(-4);
        l.add(220);

        sum = 0;
        indexSum = 0;
        count = 0;
        assertEquals(false, l.getBreakOngoingForLoop());
        l.forEachItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            if (i == 1)
                l.breakOngoingForLoop();
        });
        assertEquals(18, sum);
        assertEquals(2, count);
        assertEquals(1, indexSum);
        assertEquals(true, l.getBreakOngoingForLoop());

        //test break when loop started from specific index
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            if (i == 1)
                l.breakOngoingForLoop();
        });
        assertEquals(1, sum);
        assertEquals(1, count);
        assertEquals(1, indexSum);
        assertEquals(true, l.getBreakOngoingForLoop());
    }

    @Test
    public void test05DoWithEveryItemAndBreak2() {
        l.add(17);
        l.add(1);
        l.add(-4);

        sum = 0;
        indexSum = 0;
        count = 0;
        assertEquals(false, l.getBreakOngoingForLoop());
        l.forEachItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            //second way to do break
            l.setBreakOngoingForLoop(true); //breaks right after first iteration (because if statement is lacking)
        });
        assertEquals(17, sum);
        assertEquals(1, count);
        assertEquals(0, indexSum);
        assertEquals(true, l.getBreakOngoingForLoop());

        l.forEachItem((i, index) -> {
            assertEquals(false, l.getBreakOngoingForLoop());
        });
    }

    @Test
    public void test06ConcatenateLists() {
        MS_List<Integer> l2 = new MS_List<>();
        assertEquals(0, l.count());
        l.add(1);
        l.add(3);
        l2.add(2);
        l2.add(4);
        l2.add(5);
        assertEquals(2, l.count());
        l.concatenate(l2);
        assertEquals(5, l.count());
        assertEquals(5, l.get(4).intValue());
    }

    @Test
    public void test07NewInstance() {
        String[] stringArr = new String[2];
        stringArr[0] = "te"; stringArr[1] = "test";
        MS_List<String> strings = MS_List.newInstance(stringArr);
        assertEquals(2, strings.count());
        assertEquals("te", strings.get(0));
        assertEquals("test", strings.get(1));

        Boolean[] boolArr = new Boolean[2];
        boolArr[0] = true; boolArr[1] = false;
        MS_List<Boolean> booleans = MS_List.newInstance(boolArr);
        assertEquals(2, booleans.count());
        assertTrue(booleans.get(0));
        assertFalse(booleans.get(1));

        MS_List<Object> numbers = MS_List.newInstance(MS_CodingUtils.newArray(1, 3L, 5.0f, 2.17355767892d));
        assertEquals(4, numbers.count());
        assertEquals(Integer.class, numbers.get(0).getClass());
        assertEquals(Long.class, numbers.get(1).getClass());
        assertEquals(Float.class, numbers.get(2).getClass());
        assertEquals(Double.class, numbers.get(3).getClass());
        assertEquals(1, numbers.get(0));
        assertEquals(3L, numbers.get(1));
        assertEquals(5f, numbers.get(2));
        assertEquals(2.17355767892d, numbers.get(3));
    }
}
