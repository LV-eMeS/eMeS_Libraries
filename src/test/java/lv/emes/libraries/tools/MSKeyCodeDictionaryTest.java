package lv.emes.libraries.tools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

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
public class MSKeyCodeDictionaryTest {
    @Test
    public void test01Symbols() {
        assertEquals(65, MS_KeyCodeDictionary.textToKeyCode("a"));
        assertEquals(65, MS_KeyCodeDictionary.textToKeyCode("A"));
        assertEquals(90, MS_KeyCodeDictionary.textToKeyCode("z"));
        assertEquals(90, MS_KeyCodeDictionary.textToKeyCode("Z"));
        assertEquals(49, MS_KeyCodeDictionary.textToKeyCode("1"));
        assertEquals(57, MS_KeyCodeDictionary.textToKeyCode("9"));
        assertEquals(48, MS_KeyCodeDictionary.textToKeyCode("0"));

        assertEquals(32, MS_KeyCodeDictionary.textToKeyCode(" "));
        assertEquals(220, MS_KeyCodeDictionary.textToKeyCode("|")); //todo CORRECT THIS ONE!
        assertEquals(220, MS_KeyCodeDictionary.textToKeyCode("\\"));
        assertEquals(192, MS_KeyCodeDictionary.textToKeyCode("~"));
        assertEquals(192, MS_KeyCodeDictionary.textToKeyCode("`"));
        assertEquals(49, MS_KeyCodeDictionary.textToKeyCode("!"));
        assertEquals(50, MS_KeyCodeDictionary.textToKeyCode("@"));
        assertEquals(51, MS_KeyCodeDictionary.textToKeyCode("#"));
        assertEquals(52, MS_KeyCodeDictionary.textToKeyCode("$"));
        assertEquals(53, MS_KeyCodeDictionary.textToKeyCode("%"));
        assertEquals(54, MS_KeyCodeDictionary.textToKeyCode("^"));
        assertEquals(55, MS_KeyCodeDictionary.textToKeyCode("&"));
        assertEquals(56, MS_KeyCodeDictionary.textToKeyCode("*"));
        assertEquals(57, MS_KeyCodeDictionary.textToKeyCode("("));
        assertEquals(48, MS_KeyCodeDictionary.textToKeyCode(")"));
        assertEquals(189, MS_KeyCodeDictionary.textToKeyCode("-"));
        assertEquals(189, MS_KeyCodeDictionary.textToKeyCode("_"));
        assertEquals(187, MS_KeyCodeDictionary.textToKeyCode("+"));
        assertEquals(187, MS_KeyCodeDictionary.textToKeyCode("="));
        assertEquals(219, MS_KeyCodeDictionary.textToKeyCode("["));
        assertEquals(219, MS_KeyCodeDictionary.textToKeyCode("{"));
        assertEquals(221, MS_KeyCodeDictionary.textToKeyCode("]"));
        assertEquals(221, MS_KeyCodeDictionary.textToKeyCode("}"));
        assertEquals(186, MS_KeyCodeDictionary.textToKeyCode(":"));
        assertEquals(186, MS_KeyCodeDictionary.textToKeyCode(";"));
        assertEquals(222, MS_KeyCodeDictionary.textToKeyCode("\""));
        assertEquals(222, MS_KeyCodeDictionary.textToKeyCode("'"));
        assertEquals(188, MS_KeyCodeDictionary.textToKeyCode(","));
        assertEquals(188, MS_KeyCodeDictionary.textToKeyCode("<"));
        assertEquals(190, MS_KeyCodeDictionary.textToKeyCode(">"));
        assertEquals(190, MS_KeyCodeDictionary.textToKeyCode("."));
        assertEquals(191, MS_KeyCodeDictionary.textToKeyCode("/"));
        assertEquals(191, MS_KeyCodeDictionary.textToKeyCode("?"));
    }

    @Test
    public void test02OtherKeys() {
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("Ctrl"), 17);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("CTRL"), 17);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("ALT"), 18);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("SHIFT"), 16);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("DEL"), 46);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("DELETE"), 46);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("INS"), 45);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("INSERT"), 45);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("HOME"), 36);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("END"), 35);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PGUP"), 33);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PAGEUP"), 33);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PG_UP"), 33);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PAGE_UP"), 33);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PGDOWN"), 34);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PG_DOWN"), 34);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("Page_DOWN"), 34);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("PAGEDOWN"), 34);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("SPACE"), 32);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("ESC"), 27);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("ESCAPE"), 27);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("ENT"), 13);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("ENTER"), 13);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("BACK"), 8);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("BCK"), 8);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("BACKSPACE"), 8);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("BCKS"), 8);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("TAB"), 9);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("CAPS"), 20);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("CAPSLOCK"), 20);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("CAPS_LOCK"), 20);

        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F1"), 112);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F2"), 113);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F3"), 114);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F4"), 115);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F5"), 116);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F6"), 117);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F7"), 118);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F8"), 119);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F9"), 120);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F10"), 121);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F11"), 122);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("F12"), 123);

        assertEquals(MS_KeyCodeDictionary.textToKeyCode("MENU"), 93);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("RIGHT_MCLICK"), 93);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("WIN"), 91);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("WINDOWS"), 91);

        assertEquals(MS_KeyCodeDictionary.textToKeyCode("BACKSLASH"), 220);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("SLASH"), 191);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("LEFT"), 37);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("UP"), 38);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("RIGHT"), 39);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("DOWN"), 40);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("NUM_LOCK"), 144);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("NUMLOCK"), 144);
    }

    @Test
    public void test03BadKeys() {
        assertEquals(MS_KeyCodeDictionary.textToKeyCode(" NUMLOCK "), 0);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("00"), 0);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("Badwords"), 0);
        assertEquals(MS_KeyCodeDictionary.textToKeyCode("#)*&$*(@_)#^&&$@^"), 0);
    }

    @Test
    public void test04KeysInCombinationWithShift() {
        //true conditions
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('!'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('@'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('#'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('$'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('%'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('^'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('&'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('*'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('('));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar(')'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('_'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('+'));

        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('|'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('~'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar(':'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('"'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('<'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('>'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('?'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('{'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('}'));

        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('A'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('B'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('C'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('D'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('E'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('F'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('G'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('H'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('I'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('J'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('K'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('L'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('M'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('N'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('O'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('P'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('R'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('S'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('T'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('U'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('V'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Z'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Q'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('W'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('X'));
        assertTrue(MS_KeyCodeDictionary.needToPushShiftToWriteChar('Y'));

        //false conditions
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('1'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('2'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('3'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('4'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('5'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('6'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('7'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('8'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('9'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('0'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('-'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('='));

        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('\\'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('`'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('['));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar(']'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar(';'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('\''));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar(','));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('.'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('/'));

        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('a'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('b'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('c'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('d'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('e'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('f'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('g'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('h'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('i'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('j'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('k'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('l'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('m'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('n'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('o'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('p'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('r'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('s'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('t'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('u'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('v'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('z'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('q'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('w'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('x'));
        assertFalse(MS_KeyCodeDictionary.needToPushShiftToWriteChar('y'));
    }
}
