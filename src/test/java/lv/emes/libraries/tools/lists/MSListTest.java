package lv.emes.libraries.tools.lists;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSListTest {
    private MS_List<Integer> l = new MS_List<Integer>();
    int count, sum, elementCountIterated;

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
        assertTrue(((Integer) intArr[0]).intValue() == 5);
        assertTrue(((Integer) intArr[1]).intValue() == 5);
        assertTrue(((Integer) intArr[2]).intValue() == 3);
    }

    @Test
    public void test03DoWithEveryItem() {
        l.add(17);
        l.add(1);
        l.add(-4);
        sum = 0;
        elementCountIterated = 0;
        l.doWithEveryItem((i, index) -> {
            sum += i;
            elementCountIterated += index;
            count++;
        });
        assertTrue(sum == 14);
        assertTrue(count == 3);
        assertTrue(elementCountIterated == 3);
    }
}
