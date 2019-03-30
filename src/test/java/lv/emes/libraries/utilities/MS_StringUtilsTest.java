package lv.emes.libraries.utilities;

import lv.emes.libraries.tools.lists.MS_StringList;
import org.assertj.core.api.SoftAssertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static lv.emes.libraries.utilities.MS_StringUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_StringUtilsTest {

    @Test
    public void test01PrimitiveFunctions() {
        //test constants
        System.out.println("My test message"+ _LINE_BRAKE +"is in new line");
        System.out.println(_TAB_SPACE + "My test message begins with tab");
        System.out.println("_DIACTRITIC_CHAR_COUNT is: "+ _DIACTRITIC_CHAR_COUNT);
    }

    @Test
    public void test02Substrings() {
        //test substrings
        assertThat(isSubstring("please", "Test me, PLEASE!")).isFalse();
        assertThat(isSubstring("please", "Test me, PLEASE!", true)).isFalse();
        assertThat(isSubstring("please", "Test me, PLEASE!", false)).isTrue();
        assertThat(isSubstring("PLEaSE", "Test me, PLEASE!", false)).isTrue();

        assertThat(textContains("My text contains", "contains")).isTrue();
        assertThat(textContains("My text contains", "contains", true)).isTrue();
        assertThat(textContains("My text CONTAINS", "contains")).isFalse();
        assertThat(textContains("My text CONTAINS", "contains", false)).isTrue();
        assertThat(textContains("My text contains", "c")).isTrue();
        assertThat(textContains("My text cOntains", "O")).isTrue();
        assertThat(textContains("My text contains", "O")).isFalse();
    }

    @Test
    public void test03Replaces() {
        assertThat(replaceInString("Mana manna garšo labi", "na", "XXX", true).
                equals("MaXXX manXXX garšo labi")).isTrue();
        assertThat(replaceInString("Mana manna garšo labi", "NA", "XXX").
                equals("MaXXX manXXX garšo labi")).isTrue();
        assertThat(replaceInString("aaa", "aa", "b").equals("ba")).isTrue();
        assertThat(replaceInString("foo.bar", ".", "").equals("foobar")).isTrue();
    }

    @Test
    public void test04Array() {
        String[] arr;
        arr = getStringArray();
        assertThat(arr.length).isEqualTo(0);

        arr = getStringArray("test");
        assertThat(arr.length).isEqualTo(1);
        assertThat(arr[0]).isEqualTo("test");

        arr = getStringArray("test1", "test2");
        assertThat(arr.length).isEqualTo(2);
        assertThat(arr[0]).isEqualTo("test1");
        assertThat(arr[1]).isEqualTo("test2");

        arr = getStringArray("test1", "test2", "test3");
        assertThat(arr.length).isEqualTo(3);
        assertThat(arr[0]).isEqualTo("test1");
        assertThat(arr[1]).isEqualTo("test2");
        assertThat(arr[2]).isEqualTo("test3");
    }

    @Test
    public void test05OtherSmallOperations() {
        //test other small operations
        assertThat(getInversedText("ABC123cba")).isEqualTo("abc321CBA");
        String testMyText = "my Text mmM";
        assertThat(getTextWithCapitalizedFirstLetter(testMyText)).isEqualTo("My Text mmM");
        assertThat(hasOnlyDigitsInText("0631")).isTrue();
        assertThat(isCharASCIILetter('a')).isTrue();
        assertThat(isCharASCIILetter('ā')).isFalse();
        assertThat(hasOnlyASCIILettersInText("ZabcdEFG")).isTrue();
        assertThat(hasOnlyASCIILettersInText("1")).isFalse();
        assertThat(removeLV("ō, kāds brīnumiņš!")).isEqualTo("o, kads brinumins!");
        assertThat(removePunctuation("Viens teikums. Otrs teikums! Trešais... Ceturtais arī vēl būs? Nu tad jau redzēs //?''\\"))
                .isEqualTo("Viens teikums Otrs teikums Trešais Ceturtais arī vēl būs Nu tad jau redzēs ''");
        System.out.println(getHashFromString("My test -> String; Dīvains tekstiņš. Русский язык."));
        assertThat(pos("Pattern X in this text", "X")).isEqualTo(8);

        assertThat(getSubstring("given text", 0, 5)).isEqualTo("given");
        assertThat(getSubstring("AbcdXXXefg", 4, 7)).isEqualTo("XXX");
        assertThat(getSubstring("AbcdXXXefg", 100, 7)).isEqualTo("");
        assertThat(getSubstring(" A text to test", 3, 700)).isEqualTo("text to test");
        assertThat(getSubstring("a", 0, 1)).isEqualTo("a");
        assertThat(getSubstring("a", 0, 2)).isEqualTo("a");
        assertThat(getSubstring("a", 0, 0)).isEqualTo("");
        assertThat(getSubstring("a", 1, 1)).isEqualTo("");
        assertThat(getTabSpace(1)).isEqualTo(_TAB_SPACE);
        assertThat(MS_StringUtils.getTabSpace(2)).isEqualTo(_TAB_SPACE + _TAB_SPACE);
        assertThat(getTabSpace(3)).isEqualTo(_TAB_SPACE + _TAB_SPACE + _TAB_SPACE);
        assertThat(getTabSpace(-33)).isEqualTo("");
        assertThat(getTabSpace(0)).isEqualTo("");
        assertThat(getPosition("X", "Pattern X in this text")).isEqualTo(8);
    }

    @Test
    public void test07StringToPrimitiveNullInteger() {
        Integer actual = MS_StringUtils.stringToPrimitive(MS_StringUtils.NULL_STRING, Integer.class);
        assertThat(actual).isNull();
    }

    @Test
    public void test08StringToPrimitiveNullBoolean() {
        Boolean actual = MS_StringUtils.stringToPrimitive(MS_StringUtils.NULL_STRING, Boolean.class);
        assertThat(actual).isNull();
    }

    @Test
    public void test09StringToPrimitiveNullString() {
        String actual = MS_StringUtils.stringToPrimitive(MS_StringUtils.NULL_STRING, String.class);
        assertThat(actual).isNull();
    }

    @Test
    public void test10StringToPrimitiveNullObject() {
        Object actual = MS_StringUtils.stringToPrimitive(MS_StringUtils.NULL_STRING, Object.class);
        assertThat(actual).isNull();
    }

    @Test
    public void test11StringToPrimitiveCommaDelimitedToStringList() {
        SoftAssertions.assertSoftly(softly -> {
            MS_StringList actual = MS_StringUtils.stringToPrimitive(MS_StringUtils.NULL_STRING, MS_StringList.class);
            softly.assertThat(actual).isNull();

            actual = MS_StringUtils.stringToPrimitive("", MS_StringList.class);
            softly.assertThat(actual.size()).isEqualTo(0);
            softly.assertThat(actual.get(0)).isEqualTo("");

            actual = MS_StringUtils.stringToPrimitive("12", MS_StringList.class);
            softly.assertThat(actual.size()).isEqualTo(1);
            softly.assertThat(actual.get(0)).isEqualTo("12");

            actual = MS_StringUtils.stringToPrimitive("12,abc,", MS_StringList.class);
            softly.assertThat(actual.size()).isEqualTo(2);
            softly.assertThat(actual.get(0)).isEqualTo("12");
            softly.assertThat(actual.get(1)).isEqualTo("abc");
        });
    }

    @Test
    public void test31PrimitiveToStringStringListToCommaDelimited() {
        SoftAssertions.assertSoftly(softly -> {
            String actual = MS_StringUtils.primitiveToString(null);
            softly.assertThat(actual).isEqualTo(NULL_STRING);

            actual = MS_StringUtils.primitiveToString(new MS_StringList());
            softly.assertThat(actual).isEqualTo("");

            actual = MS_StringUtils.primitiveToString(new MS_StringList("party", ','));
            softly.assertThat(actual).isEqualTo("party");

            MS_StringList list = new MS_StringList("party", '%'); //it will still convert
            list.add("like");
            actual = MS_StringUtils.primitiveToString(list);
            softly.assertThat(actual).isEqualTo("party,like");

            list = new MS_StringList();
            softly.assertThat(list.delimiter).isEqualTo(MS_StringList._DEFAULT_DELIMITER);
            list.add("like");
            list.add("an");
            list.add("animal");
            actual = MS_StringUtils.primitiveToString(list);
            softly.assertThat(actual).isEqualTo("like,an,animal");
            softly.assertThat(list.delimiter).isEqualTo(',');
        });
    }
}
