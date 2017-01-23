package lv.emes.libraries.tools.lists;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSListTest {
    private MS_List<Integer> l = new MS_List<>();
    int count, sum, indexSum;

    @Test
    public void test01ListEngine() {
        assertTrue(l.count() == 0);
        l.add(1);
        assertTrue(l.get(0).intValue() == 1);
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
        l.doWithEveryItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertEquals(14, sum);
        assertEquals(3, count);
        assertEquals(3, indexSum);
    }

    @Test
    public void test04DoWithEveryItemAndBreak() {
        l.add(17);
        l.add(1);
        l.add(-4);
        sum = 0;
        indexSum = 0;
        assertEquals(false, l.getBreakDoWithEveryItem());
        l.doWithEveryItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            if (i == 1)
                l.breakDoWithEveryItem();
        });
        assertEquals(18, sum);
        assertEquals(2, count);
        assertEquals(1, indexSum);
        assertEquals(true, l.getBreakDoWithEveryItem());
    }

    @Test
    public void test05DoWithEveryItemAndBreak2() {
        l.add(17);
        l.add(1);
        l.add(-4);
        sum = 0;
        indexSum = 0;
        assertEquals(false, l.getBreakDoWithEveryItem());
        l.doWithEveryItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            //second way to do break
            l.setBreakDoWithEveryItem(true); //breaks right after first iteration (because if statement is lacking)
        });
        assertEquals(17, sum);
        assertEquals(1, count);
        assertEquals(0, indexSum);
        assertEquals(true, l.getBreakDoWithEveryItem());

        l.doWithEveryItem((i, index) -> {
            assertEquals(false, l.getBreakDoWithEveryItem());
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
}
