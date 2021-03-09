package lv.emes.libraries.tools.lists;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_StringListTest {

    @Test
    public void test01listImportFromStringWithoutSecondDelimiter() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        assertThat(sl.get(0)).isEqualTo("Test");
        assertThat(sl.get(1)).isEqualTo("String");
        assertThat(sl.get(2)).isEqualTo("List");
        assertThat(sl.get(3)).isSameAs("");

        final String STRING_WITHOUT_DELIMITER = "No delimiter here";
        MS_StringList sl2 = new MS_StringList(STRING_WITHOUT_DELIMITER);
        assertThat(sl2.size()).isEqualTo(1);
        assertThat(sl2.get(0)).isEqualTo(STRING_WITHOUT_DELIMITER);
        assertThat(sl2.toString()).isEqualTo(STRING_WITHOUT_DELIMITER + MS_StringList._DEFAULT_DELIMITER);
    }

    @Test
    public void test02listImportFromStringWithSecondDelimiter() {
        MS_StringList sl = new MS_StringList();
        sl.secondDelimiter = '^';
        sl.fromString("1#2#^3#4", '#');
        assertThat(sl.get(0)).isEqualTo("1");
        assertThat(sl.get(1)).isEqualTo("2#3");
        assertThat(sl.get(2)).isEqualTo("4");
        sl.secondDelimiter = 'z';
        assertThat(sl.toString()).isEqualTo("1#2#z3#4#");
        sl.delimiter = ' ';
        System.out.println(sl);
        assertThat(sl.toString()).isEqualTo("1 2#3 4 ");
    }

    @Test
    public void test03practicalTest() {
        MS_StringList sl = new MS_StringList();
        sl.secondDelimiter = '^';
        sl.fromString("My$List's$Delimiter$Is $^", '$');
        assertThat(sl.get(0)).isEqualTo("My");
        assertThat(sl.get(1)).isEqualTo("List's");
        assertThat(sl.get(2)).isEqualTo("Delimiter");
        assertThat(sl.get(3)).isEqualTo("Is $");
    }

    @Test
    public void test04EmptyListTest() {
        MS_StringList sl = new MS_StringList();
        assertThat(sl.toString()).isEqualTo("");

        sl = new MS_StringList("", '?');
        assertThat(sl.toString()).isEqualTo("");
        sl.add("");
        assertThat(sl.toString()).isEqualTo("?");

        sl = new MS_StringList("!", '!');
        assertThat(sl.toString()).isEqualTo("!");
    }

    //Here starts harder tests
    @Test
    public void test10AddAndInsertAndRemove() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        sl.add("Test");
        assertThat(sl.count()).isEqualTo(4);
        assertThat(sl.size()).isEqualTo(4);
        assertThat(sl.get(sl.count() - 1)).isEqualTo(sl.get(0));

        sl.insert(0, "rrrr");
        assertThat(sl.count()).isEqualTo(5);
        assertThat(sl.get(0)).isEqualTo("rrrr");

        assertThat(sl.getIndex("rrrR".toLowerCase())).isEqualTo(0);
        String teststr = "Prove it!";
        sl.add(teststr);
        sl.remove(teststr);
        assertThat(sl.getIndex(teststr)).isEqualTo(-1);
    }

    @Test
    public void test11EditAndToString() {
        MS_StringList sl = new MS_StringList();
        sl.add("One");
        sl.add("Two");
        sl.add("Three");
        sl.edit(1, "SOMETHING NEW");
        assertThat(sl.get(1)).isNotEqualTo("Two");
        assertThat(sl.delimiter).isEqualTo('#');
        assertThat(sl.toString()).isEqualTo("One#SOMETHING NEW#Three#");
    }

    @Test
    public void test12FromListOrArray() {
        MS_StringList sl = new MS_StringList("Test#String#List");
        List<String> stringList = new ArrayList<>();
        stringList.add("New #list");
        stringList.add("Add new");
        sl.fromList(stringList);
        assertThat(sl.get(0)).isEqualTo("New #list");
        assertThat(sl.get(1)).isEqualTo("Add new");
        assertThat(sl.toString()).isEqualTo("New #" + sl.secondDelimiter + "list#Add new#");
    }

    @Test
    public void test13FromStringWithDifferentDelimiters() {
        MS_StringList sl = new MS_StringList();
        sl.fromString("Test&Something&New!", '&');
        assertThat(sl.delimiter).isEqualTo('&');
        assertThat(sl.get(0)).isEqualTo("Test");
        assertThat(sl.get(1)).isEqualTo("Something");
        assertThat(sl.get(2)).isEqualTo("New!");

        sl.secondDelimiter = '!';
        sl.fromString("Test?Something?New?!", '?');
        assertThat(sl.get(0)).isEqualTo("Test");
        assertThat(sl.get(1)).isEqualTo("Something");
        assertThat(sl.get(2)).isEqualTo("New?");
    }

    @Test
    public void test14DoWithEveryStringInList() {
        MS_StringList sl = new MS_StringList("One#Two#");
        sl.forEachItem((s, index) -> {
            boolean test = s.equals("One") || s.equals("Two");
            assertThat(test).isTrue();
        });
    }

    @Test
    public void test17ToStringWithNoLastDelimiter() {
        MS_StringList sl;
        String res;
        sl = new MS_StringList("");
        res = sl.toStringWithNoLastDelimiter();
        assertThat(res).isEqualTo("");

        sl = new MS_StringList("One#");
        res = sl.toStringWithNoLastDelimiter();
        assertThat(res).isEqualTo("One");

        sl = new MS_StringList("One#Two#");
        res = sl.toStringWithNoLastDelimiter();
        assertThat(res).isEqualTo("One#Two");
    }

    @Test
    public void test21LoopThroughAndEdit() {
        MS_StringList sl = new MS_StringList("1:4:3", ':');
        //add leading zeroes to each element
        sl.forEachItem((str, i) -> sl.edit(i, "0" + str));
        assertThat(sl.toStringWithNoLastDelimiter()).isEqualTo("01:04:03");
    }
}
