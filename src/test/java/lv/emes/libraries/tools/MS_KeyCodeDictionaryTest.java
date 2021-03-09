package lv.emes.libraries.tools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static java.awt.event.KeyEvent.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests available key code dictionary. Recognizable inputs are:<br>
 *     <i>All single (input length = 1) digits and letters will be converted to ASCII code</i><br>
 CTRL = 17;<br>
 CONTROL = 17;<br>
 ALT = 18;<br>
 SHIFT = 16;<br>

 DEL = 46;<br>
 DELETE = 46;<br>

 INS = 45;<br>
 INSERT = 45;<br>

 HOME = 36;<br>
 END = 35;<br>
 PGUP = 33;<br>
 PG_UP = 33;<br>
 PGDOWN = 34;<br>
 PG_DOWN = 34;<br>

 SPACE = 32;<br>

 ESC = 27;<br>
 ESCAPE = 27;<br>

 ENT = 13;<br>
 ENTER = 13;<br>

 BACK = 8;<br>
 BCK = 8;<br>
 BACKSPACE = 8;<br>
 BCKS = 8;<br>

 TAB = 9;<br>
 CAPS = 20;<br>
 CAPSLOCK = 20;<br>
 CAPS_LOCK = 20;<br>

 F1 = 112;<br>
 F2 = 113;<br>
 F3 = 114;<br>
 F4 = 115;<br>
 F5 = 116;<br>
 F6 = 117;<br>
 F7 = 118;<br>
 F8 = 119;<br>
 F9 = 120;<br>
 F10 = 121;<br>
 F11 = 122;<br>
 F12 = 123;<br>

 MENU = 93;<br>
 RIGHT_MCLICK = 93;<br>

 WIN = 91;<br>
 WINDOWS = 91;<br>

 BACKSLASH = 220;<br>
 SLASH = 191;<br>

 LEFT = 37;<br>
 UP = 38;<br>
 RIGHT = 39;<br>
 DOWN = 40;<br>

 NUM_LOCK = 144;<br>
 NUMLOCK = 144;<br>

 | = 220;<br>
 \\ = 220;<br>
 ` = 192;<br>
 ~ = 192;<br>
 ! = 49;<br>
 @ = 50;<br>
 # = 51;<br>
 $ = 52;<br>
 % = 53;<br>
 ^ = 54;<br>
 & = 55;<br>
 * = 56;<br>
 ( = 57;<br>
 ) = 48;<br>
 - = 189;<br>
 _ = 189;<br>
 + = 187;<br>
 = = 187;<br>
 [ = 219;<br>
 { = 219;<br>
 ] = 221;<br>
 } = 221;<br>
 : = 186;<br>
 ; = 186;<br>
 " = 222;<br>
 ' = 222;<br>
 , = 188;<br>
 < = 188;<br>
 . = 190;<br>
 > = 190;<br>
 / = 191;<br>
 ? = 191;<br>
<i>{space}<i/> = 32;<br>
 * @version 1.0.
 * @author eMeS
 * @see lv.emes.libraries.tools.MS_KeyCodeDictionary
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MS_KeyCodeDictionaryTest {
    @Test
    public void test01Symbols() {
        assertThat(MS_KeyCodeDictionary.textToKeyCode("a")).isEqualTo(65);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("A")).isEqualTo(65);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("z")).isEqualTo(90);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("Z")).isEqualTo(90);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("1")).isEqualTo(49);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("9")).isEqualTo(57);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("0")).isEqualTo(48);

        assertThat(MS_KeyCodeDictionary.textToKeyCode(" ")).isEqualTo(VK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("|")).isEqualTo(VK_BACK_SLASH);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("\\")).isEqualTo(VK_BACK_SLASH);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("~")).isEqualTo(VK_BACK_QUOTE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("`")).isEqualTo(VK_BACK_QUOTE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("!")).isEqualTo(VK_1);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("@")).isEqualTo(VK_2);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("#")).isEqualTo(VK_3);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("$")).isEqualTo(VK_4);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("%")).isEqualTo(VK_5);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("^")).isEqualTo(VK_6);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("&")).isEqualTo(VK_7);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("*")).isEqualTo(VK_8);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("(")).isEqualTo(VK_9);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(")")).isEqualTo(VK_0);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("-")).isEqualTo(VK_MINUS);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("_")).isEqualTo(VK_MINUS);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("+")).isEqualTo(VK_EQUALS);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("=")).isEqualTo(VK_EQUALS);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("[")).isEqualTo(VK_OPEN_BRACKET);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("{")).isEqualTo(VK_OPEN_BRACKET);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("]")).isEqualTo(VK_CLOSE_BRACKET);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("}")).isEqualTo(VK_CLOSE_BRACKET);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(":")).isEqualTo(VK_SEMICOLON);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(";")).isEqualTo(VK_SEMICOLON);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("\"")).isEqualTo(VK_QUOTE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("'")).isEqualTo(VK_QUOTE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(",")).isEqualTo(VK_COMMA);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("<")).isEqualTo(VK_COMMA);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(">")).isEqualTo(VK_PERIOD);
        assertThat(MS_KeyCodeDictionary.textToKeyCode(".")).isEqualTo(VK_PERIOD);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("/")).isEqualTo(VK_SLASH);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("?")).isEqualTo(VK_SLASH);
    }

    @Test
    public void test02OtherKeys() {
        assertThat(MS_KeyCodeDictionary.textToKeyCode("Ctrl")).isEqualTo(VK_CONTROL);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("CTRL")).isEqualTo(VK_CONTROL);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("ALT")).isEqualTo(VK_ALT);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("SHIFT")).isEqualTo(VK_SHIFT);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("DEL")).isEqualTo(VK_DELETE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("DELETE")).isEqualTo(VK_DELETE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("INS")).isEqualTo(VK_INSERT);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("INSERT")).isEqualTo(VK_INSERT);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("HOME")).isEqualTo(VK_HOME);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("END")).isEqualTo(VK_END);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PGUP")).isEqualTo(VK_PAGE_UP);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PAGEUP")).isEqualTo(VK_PAGE_UP);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PG_UP")).isEqualTo(VK_PAGE_UP);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PAGE_UP")).isEqualTo(VK_PAGE_UP);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PGDOWN")).isEqualTo(VK_PAGE_DOWN);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PG_DOWN")).isEqualTo(VK_PAGE_DOWN);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("Page_DOWN")).isEqualTo(VK_PAGE_DOWN);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("PAGEDOWN")).isEqualTo(VK_PAGE_DOWN);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("SPACE")).isEqualTo(VK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("ESC")).isEqualTo(VK_ESCAPE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("ESCAPE")).isEqualTo(VK_ESCAPE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("ENT")).isEqualTo(VK_ENTER);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("ENTER")).isEqualTo(VK_ENTER);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("BACK")).isEqualTo(VK_BACK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("BCK")).isEqualTo(VK_BACK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("BACKSPACE")).isEqualTo(VK_BACK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("BCKS")).isEqualTo(VK_BACK_SPACE);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("TAB")).isEqualTo(VK_TAB);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("CAPS")).isEqualTo(VK_CAPS_LOCK);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("CAPSLOCK")).isEqualTo(VK_CAPS_LOCK);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("CAPS_LOCK")).isEqualTo(VK_CAPS_LOCK);

        assertThat(MS_KeyCodeDictionary.textToKeyCode("F1")).isEqualTo(112);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F2")).isEqualTo(113);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F3")).isEqualTo(114);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F4")).isEqualTo(115);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F5")).isEqualTo(116);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F6")).isEqualTo(117);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F7")).isEqualTo(118);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F8")).isEqualTo(119);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F9")).isEqualTo(120);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F10")).isEqualTo(121);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F11")).isEqualTo(122);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("F12")).isEqualTo(123);

        assertThat(MS_KeyCodeDictionary.textToKeyCode("MENU")).isEqualTo(VK_CONTEXT_MENU);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("RIGHT_MCLICK")).isEqualTo(VK_CONTEXT_MENU);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("WIN")).isEqualTo(VK_WINDOWS);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("WINDOWS")).isEqualTo(VK_WINDOWS);

        assertThat(MS_KeyCodeDictionary.textToKeyCode("BACKSLASH")).isEqualTo(VK_BACK_SLASH);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("SLASH")).isEqualTo(VK_SLASH);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("LEFT")).isEqualTo(37);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("UP")).isEqualTo(38);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("RIGHT")).isEqualTo(39);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("DOWN")).isEqualTo(40);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("NUM_LOCK")).isEqualTo(144);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("NUMLOCK")).isEqualTo(144);
    }

    @Test
    public void test03BadKeys() {
        assertThat(MS_KeyCodeDictionary.textToKeyCode(" NUMLOCK ")).isEqualTo(0);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("00")).isEqualTo(0);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("Badwords")).isEqualTo(0);
        assertThat(MS_KeyCodeDictionary.textToKeyCode("#)*&$*(@_)#^&&$@^")).isEqualTo(0);
    }

    @Test
    public void test04KeysInCombinationWithShift() {
        //true conditions
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('!')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('@')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('#')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('$')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('%')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('^')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('&')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('*')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('(')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar(')')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('_')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('+')).isTrue();

        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('|')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('~')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar(':')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('"')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('<')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('>')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('?')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('{')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('}')).isTrue();

        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('A')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('B')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('C')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('D')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('E')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('F')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('G')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('H')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('I')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('J')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('K')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('L')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('M')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('N')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('O')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('P')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('R')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('S')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('T')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('U')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('V')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Z')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Q')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('W')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('X')).isTrue();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Y')).isTrue();

        //false conditions
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('1')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('2')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('3')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('4')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('5')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('6')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('7')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('8')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('9')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('0')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('-')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('=')).isFalse();

        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('\\')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('`')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('[')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar(']')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar(';')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('\'')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar(',')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('.')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('/')).isFalse();

        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('a')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('b')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('c')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('d')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('e')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('f')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('g')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('h')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('i')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('j')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('k')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('l')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('m')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('n')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('o')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('p')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('r')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('s')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('t')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('u')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('v')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('z')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('q')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('w')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('x')).isFalse();
        assertThat(MS_KeyCodeDictionary.needToPushShiftToWriteChar('y')).isFalse();
    }
}
