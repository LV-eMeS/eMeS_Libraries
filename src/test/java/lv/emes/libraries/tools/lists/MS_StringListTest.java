package lv.emes.libraries.tools.lists;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_StringListTest {
    @Test
    public void test01listImportFromStringWithoutSecondDelimiter() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        assertTrue(sl.get(0).equals("Test"));
        assertTrue(sl.get(1).equals("String"));
        assertTrue(sl.get(2).equals("List"));
        assertTrue(sl.get(3) == "");
    }

    @Test
    public void test02listImportFromStringWithSecondDelimiter() {
        MS_StringList sl = new MS_StringList();
        sl.secondDelimiter = '^';
        sl.fromString("1#2#^3#4", '#');
        assertTrue(sl.get(0).equals("1"));
        assertTrue(sl.get(1).equals("2#3"));
        assertTrue(sl.get(2).equals("4"));
        sl.secondDelimiter = 'z';
        assertTrue(sl.toString().equals("1#2#z3#4#"));
        sl.delimiter = ' ';
        System.out.println(sl);
        assertTrue(sl.toString().equals("1 2#3 4 "));
    }

    @Test
    public void test03practicalTest() {
        MS_StringList sl = new MS_StringList();
        sl.secondDelimiter = '^';
        sl.fromString("My$List's$Delimiter$Is $^", '$');
        assertTrue(sl.get(0).equals("My"));
        assertTrue(sl.get(1).equals("List's"));
        assertTrue(sl.get(2).equals("Delimiter"));
        assertTrue(sl.get(3).equals("Is $"));
    }

    @Test
    public void test04EmptyListTest() {
        MS_StringList sl = new MS_StringList();
        assertTrue(sl.toString().equals(""));

        sl = new MS_StringList("", '?');
        assertEquals("", sl.toString());
        sl.add("");
        assertEquals("?", sl.toString());

        sl = new MS_StringList("!", '!');
        assertEquals("!", sl.toString());
    }

    //Here starts harder tests
    @Test
    public void test10AddAndInsertAndRemove() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        sl.add("Test");
        assertEquals(4, sl.count());
        assertEquals(4, sl.size());
        assertEquals(sl.get(0), sl.get(sl.count() - 1));

        sl.insert(0, "rrrr");
        assertEquals(5, sl.count());
        assertEquals("rrrr", sl.get(0));

        assertEquals(0, sl.getIndex("rrrR".toLowerCase()));
        String teststr = "Prove it!";
        sl.add(teststr);
        sl.remove(teststr);
        assertEquals(-1, sl.getIndex(teststr));
    }

    @Test
    public void test11EditAndToString() {
        MS_StringList sl = new MS_StringList();
        sl.add("One");
        sl.add("Two");
        sl.add("Three");
        sl.edit(1, "SOMETHING NEW");
        assertNotEquals("Two", sl.get(1));
        assertEquals('#', sl.delimiter);
        assertEquals("One#SOMETHING NEW#Three#", sl.toString());
    }

    @Test
    public void test12Perambulation() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        sl.first();
        assertEquals("Test", sl.current());
        sl.last();
        assertEquals("List", sl.current());
        sl.setIndexOfCurrent(1);
        assertEquals("String", sl.current());
        sl.next();
        assertEquals("List", sl.current());
        sl.prev();
        assertEquals("String", sl.current());
        sl.prev();
        assertEquals("Test", sl.current());
        sl.prev();
        assertEquals("", sl.current());
        sl.last();
        sl.next();
        assertEquals("", sl.current());
    }

    @Test
    public void test13FromListOrArray() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        List<String> stringList = new ArrayList<>();
        stringList.add("New #list");
        stringList.add("Add new");
        sl.fromList(stringList);
        assertEquals("New #list", sl.get(0));
        assertEquals("Add new", sl.get(1));
        assertEquals("New #" + sl.secondDelimiter + "list#Add new#", sl.toString());
    }

    @Test
    public void test14FromStringWithDifferentDelimiters() {
        MS_StringList sl = new MS_StringList();
        sl.fromString("Test&Something&New!", '&');
        assertEquals('&', sl.delimiter);
        assertEquals("Test", sl.get(0));
        assertEquals("Something", sl.get(1));
        assertEquals("New!", sl.get(2));

        sl.secondDelimiter = '!';
        sl.fromString("Test?Something?New?!", '?');
        assertEquals("Test", sl.get(0));
        assertEquals("Something", sl.get(1));
        assertEquals("New?", sl.get(2));
    }

    @Test
    public void test15DoWithEveryStringInList() {
        MS_StringList sl = new MS_StringList("One#Two#");
        sl.forEachItem((s, index) -> {
            boolean test = s.equals("One") || s.equals("Two");
            assertTrue(test);
        });
    }

    @Test
    public void test16ToStringWithNoLastDelimiter() {
        MS_StringList sl;
        String res;
        sl = new MS_StringList("");
        res = sl.toStringWithNoLastDelimiter();
        assertEquals("", res);

        sl = new MS_StringList("One#");
        res = sl.toStringWithNoLastDelimiter();
        assertEquals("One", res);

        sl = new MS_StringList("One#Two#");
        res = sl.toStringWithNoLastDelimiter();
        assertEquals("One#Two", res);
    }
}
