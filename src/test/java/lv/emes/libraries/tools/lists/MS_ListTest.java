package lv.emes.libraries.tools.lists;

import lv.emes.libraries.utilities.MS_CodingUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_ListTest {
    private final MS_List<Integer> l = new MS_List<>();
    private int count, sum, indexSum;

    @Test
    public void test01ListEngine() {
        assertThat(l.count()).isEqualTo(0);
        l.add(1);
        assertThat((int) l.get(0)).isEqualTo(1);
        assertThat(l.count() > 0).isTrue();
        l.add(2);
        l.add(3);
        l.add(4);
        l.remove(2);
        assertThat(l.count()).isEqualTo(3);
    }

    @Test
    public void test02ToArray() {
        l.add(5);
        l.add(5);
        l.add(3);
        Object[] intArr = l.toArray();
        assertThat(intArr[0]).isEqualTo(5);
        assertThat(intArr[1]).isEqualTo(5);
        assertThat(intArr[2]).isEqualTo(3);
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
        assertThat(sum).isEqualTo(14);
        assertThat(indexSum).isEqualTo(3);
        assertThat(count).isEqualTo(3);

        //now to test for loop starting from second element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertThat(sum).isEqualTo(-3); //1-4
        assertThat(indexSum).isEqualTo(3); //1+2
        assertThat(count).isEqualTo(2); //only 2 elements should be scanned (second and third)

        //now to test for loop starting from last element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(l.count()-1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertThat(sum).isEqualTo(-4);
        assertThat(indexSum).isEqualTo(2);
        assertThat(count).isEqualTo(1);

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
        assertThat(sum).isEqualTo(0);
        assertThat(indexSum).isEqualTo(0);
        assertThat(count).isEqualTo(0);

        //now try to work with non existing elements from lower index boundary
        l.forEachItem(-15, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        //still nothing should be changed
        assertThat(sum).isEqualTo(0);
        assertThat(indexSum).isEqualTo(0);
        assertThat(count).isEqualTo(0);

        //now to test for loop starting from 2nd element and ending with the same element
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 1, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertThat(sum).isEqualTo(1);
        assertThat(indexSum).isEqualTo(1);
        assertThat(count).isEqualTo(1);

        //test when start and end elements are switched
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 0, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertThat(sum).isEqualTo(18);
        assertThat(indexSum).isEqualTo(1);
        assertThat(count).isEqualTo(2);

        //test when end element is out of range
        sum = 0;
        indexSum = 0;
        count = 0;
        l.forEachItem(1, 1230, (i, index) -> {
            sum += i;
            indexSum += index;
            count++;
        });
        assertThat(sum).isEqualTo(0);
        assertThat(indexSum).isEqualTo(0);
        assertThat(count).isEqualTo(0);
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
        assertThat(l.getBreakOngoingForLoop()).isEqualTo(false);
        l.forEachItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            if (i == 1)
                l.breakOngoingForLoop();
        });
        assertThat(sum).isEqualTo(18);
        assertThat(count).isEqualTo(2);
        assertThat(indexSum).isEqualTo(1);
        assertThat(l.getBreakOngoingForLoop()).isEqualTo(true);

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
        assertThat(sum).isEqualTo(1);
        assertThat(count).isEqualTo(1);
        assertThat(indexSum).isEqualTo(1);
        assertThat(l.getBreakOngoingForLoop()).isEqualTo(true);
    }

    @Test
    public void test05DoWithEveryItemAndBreak2() {
        l.add(17);
        l.add(1);
        l.add(-4);

        sum = 0;
        indexSum = 0;
        count = 0;
        assertThat(l.getBreakOngoingForLoop()).isEqualTo(false);
        l.forEachItem((i, index) -> {
            sum += i;
            indexSum += index;
            count++;
            //second way to do break
            l.setBreakOngoingForLoop(true); //breaks right after first iteration (because if statement is lacking)
        });
        assertThat(sum).isEqualTo(17);
        assertThat(count).isEqualTo(1);
        assertThat(indexSum).isEqualTo(0);
        assertThat(l.getBreakOngoingForLoop()).isEqualTo(true);

        l.forEachItem((i, index) -> {
            assertThat(l.getBreakOngoingForLoop()).isEqualTo(false);
        });
    }

    @Test
    public void test06ConcatenateLists() {
        MS_List<Integer> l2 = new MS_List<>();
        assertThat(l.count()).isEqualTo(0);
        l.add(1);
        l.add(3);
        l2.add(2);
        l2.add(4);
        l2.add(5);
        assertThat(l.count()).isEqualTo(2);
        l.concatenate(l2);
        assertThat(l.count()).isEqualTo(5);
        assertThat(l.get(4).intValue()).isEqualTo(5);
    }

    @Test
    public void test07NewInstance() {
        String[] stringArr = new String[2];
        stringArr[0] = "te"; stringArr[1] = "test";
        MS_List<String> strings = MS_List.newInstance(stringArr);
        assertThat(strings.count()).isEqualTo(2);
        assertThat(strings.get(0)).isEqualTo("te");
        assertThat(strings.get(1)).isEqualTo("test");

        Boolean[] boolArr = new Boolean[2];
        boolArr[0] = true; boolArr[1] = false;
        MS_List<Boolean> booleans = MS_List.newInstance(boolArr);
        assertThat(booleans.count()).isEqualTo(2);
        assertThat(booleans.get(0)).isTrue();
        assertThat(booleans.get(1)).isFalse();

        MS_List<Object> numbers = MS_List.newInstance(MS_CodingUtils.newArray(1, 3L, 5.0f, 2.17355767892d));
        assertThat(numbers.count()).isEqualTo(4);
        assertThat(numbers.get(0).getClass()).isEqualTo(Integer.class);
        assertThat(numbers.get(1).getClass()).isEqualTo(Long.class);
        assertThat(numbers.get(2).getClass()).isEqualTo(Float.class);
        assertThat(numbers.get(3).getClass()).isEqualTo(Double.class);
        assertThat(numbers.get(0)).isEqualTo(1);
        assertThat(numbers.get(1)).isEqualTo(3L);
        assertThat(numbers.get(2)).isEqualTo(5f);
        assertThat(numbers.get(3)).isEqualTo(2.17355767892d);
    }
}
