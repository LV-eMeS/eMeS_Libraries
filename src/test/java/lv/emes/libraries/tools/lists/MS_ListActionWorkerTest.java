package lv.emes.libraries.tools.lists;

import lv.emes.libraries.tools.MS_BadSetupException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MS_ListActionWorkerTest {

    @Test
    public void testReverseList() {
        assertNull(MS_ListActionWorker.reverseList(null, null));

        MS_StringList list = new MS_StringList("1#2#3#4#5");
        MS_StringList reverseOrderList = MS_ListActionWorker.reverseList(list, new MS_StringList());
        //5#4#3#2#1#
        assertEquals(5, reverseOrderList.size());
        assertEquals(list.get(0), reverseOrderList.get(4));
        assertEquals(list.get(1), reverseOrderList.get(3));
        assertEquals(list.get(2), reverseOrderList.get(2));
        assertEquals(list.get(3), reverseOrderList.get(1));
        assertEquals(list.get(4), reverseOrderList.get(0));

        //Test that changes in this list doesn't affect original list
        reverseOrderList.removeLast();
        //5#4#3#2#
        assertEquals(5, list.size());
        assertEquals(4, reverseOrderList.size());

        reverseOrderList.edit(2, "Three");
        //5#4#Three#2#
        assertEquals("3", list.get(2));
        assertEquals("Three", reverseOrderList.get(2));

    }

    @Test(expected = MS_BadSetupException.class)
    public void testReverseListEmptyNull() {
        assertNull(MS_ListActionWorker.reverseList(new MS_StringList(), null));
    }
}