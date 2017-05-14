package lv.emes.libraries.tools;

import java.util.HashMap;
import java.util.Map;

import static java.awt.event.KeyEvent.*;

/**
 * Holds mapping for system keystroke codes to be translated from String to int type.
 * <p>Static methods:
 * <ul>
 * <li>textToKeyCode</li>
 * <li>needToPushShiftToWriteChar</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_KeyCodeDictionary {

    private MS_KeyCodeDictionary() {
    }
//PRIVATE VARIABLES
    /**
     * Contains mapping for different characters and their corresponding key codes.
     */
    private static Map<String, Integer> keyCodeDict = null;

    /**
     * Returns key code of input <b>text</b>. Every recognizable keys are tested in <b>MSKeyCodeDictionaryTest</b>.<br>
     * import static lv.emes.libraries.tools.MS_KeyCodeDictionary.textToKeyCode;
     *
     * @param text a code that can be recognized by this dictionary. For example "CTRL" for button Control key code.
     * @return corresponding key code.
     */
    public static int textToKeyCode(String text) {
        initMappings();
        text = text.toUpperCase();

        if (text.length() == 1) { //might be a letter or number
            char firstChar = text.charAt(0);
            if (Character.isLetterOrDigit(firstChar))
                return MS_StringTools.chr(firstChar); //just return ASCII of letter or number!
        }
        try {
            return keyCodeDict.get(text);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    //PRIVATE METHODS

    /**
     * Tests whether char can be written on keyboard only with pressed Shift key.<br>
     *
     * @param aChar character to be written using keyboard.
     * @return true if written using combination Shift + <b>aChar</b>. Also returns true if char is capital letter.
     */
    public static boolean needToPushShiftToWriteChar(char aChar) {
        int ascii = MS_StringTools.ord(aChar);
        return (
            //from ! till +, but excluding apostrophe ('), which is ascii 39
                MS_CodingTools.inRange(ascii, 33, 38) || MS_CodingTools.inRange(ascii, 40, 43)
                        || (ascii == 60) //< symbol
                        || (ascii == 62) //> symbol
                        || (ascii == 63) //? symbol
                        || (ascii == 64) //@ symbol
                        || (ascii == 94) //^ symbol
                        || (ascii == 95) //_ symbol
                        || (ascii == 58) //: symbol
                        || MS_CodingTools.inRange(ascii, 123, 126) // { | } ~ symbols
                        || MS_CodingTools.inRange(ascii, 65, 90) //Capital letters A..Z
                );
    }


    private static void initMappings() {
        if (keyCodeDict == null) { //if mappings not initialized then fill both char and string mappers
            keyCodeDict = new HashMap<>();

            keyCodeDict.put("CTRL", VK_CONTROL);
            keyCodeDict.put("CONTROL", VK_CONTROL);
            keyCodeDict.put("ALT", VK_ALT);
            keyCodeDict.put("ALTGR", VK_ALT_GRAPH);
            keyCodeDict.put("ALT_GR", VK_ALT_GRAPH);
            keyCodeDict.put("SHIFT", VK_SHIFT);

            keyCodeDict.put("DEL", VK_DELETE);
            keyCodeDict.put("DELETE", VK_DELETE);

            keyCodeDict.put("INS", VK_INSERT);
            keyCodeDict.put("INSERT", VK_INSERT);

            keyCodeDict.put("HOME", VK_HOME);
            keyCodeDict.put("END", VK_END);
            keyCodeDict.put("PGUP", VK_PAGE_UP);
            keyCodeDict.put("PG_UP", VK_PAGE_UP);
            keyCodeDict.put("PAGE_UP", VK_PAGE_UP);
            keyCodeDict.put("PAGEUP", VK_PAGE_UP);
            keyCodeDict.put("PGDOWN", VK_PAGE_DOWN);
            keyCodeDict.put("PG_DOWN", VK_PAGE_DOWN);
            keyCodeDict.put("PAGE_DOWN", VK_PAGE_DOWN);
            keyCodeDict.put("PAGEDOWN", VK_PAGE_DOWN);

            keyCodeDict.put("SPACE", 32);

            keyCodeDict.put("ESC", VK_ESCAPE);
            keyCodeDict.put("ESCAPE", VK_ESCAPE);

            keyCodeDict.put("ENT", VK_ENTER);
            keyCodeDict.put("ENTER", VK_ENTER);

            keyCodeDict.put("BACK", VK_BACK_SPACE);
            keyCodeDict.put("BCK", VK_BACK_SPACE);
            keyCodeDict.put("BACKSPACE", VK_BACK_SPACE);
            keyCodeDict.put("BCKS", VK_BACK_SPACE);

            keyCodeDict.put("TAB", VK_TAB);
            keyCodeDict.put("CAPS", VK_CAPS_LOCK);
            keyCodeDict.put("CAPSLOCK", VK_CAPS_LOCK);
            keyCodeDict.put("CAPS_LOCK", VK_CAPS_LOCK);

            keyCodeDict.put("F1", 112);
            keyCodeDict.put("F2", 113);
            keyCodeDict.put("F3", 114);
            keyCodeDict.put("F4", 115);
            keyCodeDict.put("F5", 116);
            keyCodeDict.put("F6", 117);
            keyCodeDict.put("F7", 118);
            keyCodeDict.put("F8", 119);
            keyCodeDict.put("F9", 120);
            keyCodeDict.put("F10", 121);
            keyCodeDict.put("F11", 122);
            keyCodeDict.put("F12", 123);

            keyCodeDict.put("NUM0", VK_NUMPAD0);
            keyCodeDict.put("NUM1", VK_NUMPAD1);
            keyCodeDict.put("NUM2", VK_NUMPAD2);
            keyCodeDict.put("NUM3", VK_NUMPAD3);
            keyCodeDict.put("NUM4", VK_NUMPAD4);
            keyCodeDict.put("NUM5", VK_NUMPAD5);
            keyCodeDict.put("NUM6", VK_NUMPAD6);
            keyCodeDict.put("NUM7", VK_NUMPAD7);
            keyCodeDict.put("NUM8", VK_NUMPAD8);
            keyCodeDict.put("NUM9", VK_NUMPAD9);

            keyCodeDict.put("MENU", VK_CONTEXT_MENU);
            keyCodeDict.put("RIGHT_MCLICK", VK_CONTEXT_MENU);

            keyCodeDict.put("WIN", VK_WINDOWS); //91 un 92 are Windows buttons, so doesn't matter, which one will be used here
            keyCodeDict.put("WINDOWS", VK_WINDOWS);

            keyCodeDict.put("BACKSLASH", VK_BACK_SLASH);
            keyCodeDict.put("SLASH", VK_SLASH);

            keyCodeDict.put("LEFT", 37);
            keyCodeDict.put("UP", 38);
            keyCodeDict.put("RIGHT", 39);
            keyCodeDict.put("DOWN", 40);

            keyCodeDict.put("NUM_LOCK", 144);
            keyCodeDict.put("NUMLOCK", 144);
            //in the same manner fill printable char codes
            keyCodeDict.put("|", VK_BACK_SLASH);
            keyCodeDict.put("\\", VK_BACK_SLASH);
            keyCodeDict.put("`", VK_BACK_QUOTE);
            keyCodeDict.put("~", VK_BACK_QUOTE);
            keyCodeDict.put("!", 49);
            keyCodeDict.put("@", 50);
            keyCodeDict.put("#", 51);
            keyCodeDict.put("$", 52);
            keyCodeDict.put("%", 53);
            keyCodeDict.put("^", 54);
            keyCodeDict.put("&", 55);
            keyCodeDict.put("*", 56);
            keyCodeDict.put("(", 57);
            keyCodeDict.put(")", 48);
            keyCodeDict.put("-", VK_MINUS);
            keyCodeDict.put("_", VK_MINUS);
            keyCodeDict.put("+", VK_EQUALS);
            keyCodeDict.put("=", VK_EQUALS);
            keyCodeDict.put("[", VK_OPEN_BRACKET);
            keyCodeDict.put("{", VK_OPEN_BRACKET);
            keyCodeDict.put("]", VK_CLOSE_BRACKET);
            keyCodeDict.put("}", VK_CLOSE_BRACKET);
            keyCodeDict.put(":", VK_SEMICOLON);
            keyCodeDict.put(";", VK_SEMICOLON);
            keyCodeDict.put("\"", VK_QUOTE); //222
            keyCodeDict.put("'", VK_QUOTE); //222
            keyCodeDict.put(",", VK_COMMA);
            keyCodeDict.put("<", VK_COMMA);
            keyCodeDict.put(".", VK_PERIOD);
            keyCodeDict.put(">", VK_PERIOD);
            keyCodeDict.put("/", VK_SLASH);
            keyCodeDict.put("?", VK_SLASH);
            keyCodeDict.put(" ", 32);
            keyCodeDict.put("VK_PRINTSCREEN", VK_PRINTSCREEN);
            keyCodeDict.put("PRINT_SCREEN", VK_PRINTSCREEN);
            keyCodeDict.put("PRINTSCREEN", VK_PRINTSCREEN);
            keyCodeDict.put("Printscr", VK_PRINTSCREEN);
            keyCodeDict.put("Prtscr", VK_PRINTSCREEN);
            keyCodeDict.put("TEST", VK_ASTERISK);
        }
    }
}
