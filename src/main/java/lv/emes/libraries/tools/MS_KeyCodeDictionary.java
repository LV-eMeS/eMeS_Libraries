package lv.emes.libraries.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds mapping for system keystroke codes to be translated from String to int type.
 * <p>Methods:
 * -textToKeyCode
 * -needToPushShiftToWriteChar
 *
 * @author eMeS
 * @version 0.9.
 */
public class MS_KeyCodeDictionary {
    //PRIVATE VARIABLES
    /**
     * Contains mapping for different characters and their corresponding key codes.
     */
    private static Map<String, Integer> keyCodeDict = null;

    //PUBLIC METHOD

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
                MS_Tools.inRange(ascii, 33, 38) || MS_Tools.inRange(ascii, 40, 43)
                        || (ascii == 60) //< symbol
                        || (ascii == 62) //> symbol
                        || (ascii == 63) //? symbol
                        || (ascii == 64) //@ symbol
                        || (ascii == 94) //^ symbol
                        || (ascii == 95) //_ symbol
                        || (ascii == 58) //: symbol
                        || MS_Tools.inRange(ascii, 123, 126) // { | } ~ symbols
                        || MS_Tools.inRange(ascii, 65, 90) //Capital letters A..Z
                );
    }


    private static void initMappings() {
        if (keyCodeDict == null) { //if mappings not initialized then fill both char and string mappers
            keyCodeDict = new HashMap<>();

            keyCodeDict.put("CTRL", 17);
            keyCodeDict.put("CONTROL", 17);
            keyCodeDict.put("ALT", 18);
            keyCodeDict.put("SHIFT", 16);

            keyCodeDict.put("DEL", 46);
            keyCodeDict.put("DELETE", 46);

            keyCodeDict.put("INS", 45);
            keyCodeDict.put("INSERT", 45);

            keyCodeDict.put("HOME", 36);
            keyCodeDict.put("END", 35);
            keyCodeDict.put("PGUP", 33);
            keyCodeDict.put("PG_UP", 33);
            keyCodeDict.put("PAGE_UP", 33);
            keyCodeDict.put("PAGEUP", 33);
            keyCodeDict.put("PGDOWN", 34);
            keyCodeDict.put("PG_DOWN", 34);
            keyCodeDict.put("PAGE_DOWN", 34);
            keyCodeDict.put("PAGEDOWN", 34);

            keyCodeDict.put("SPACE", 32);

            keyCodeDict.put("ESC", 27);
            keyCodeDict.put("ESCAPE", 27);

            keyCodeDict.put("ENT", 13);
            keyCodeDict.put("ENTER", 13);

            keyCodeDict.put("BACK", 8);
            keyCodeDict.put("BCK", 8);
            keyCodeDict.put("BACKSPACE", 8);
            keyCodeDict.put("BCKS", 8);

            keyCodeDict.put("TAB", 9);
            keyCodeDict.put("CAPS", 20);
            keyCodeDict.put("CAPSLOCK", 20);
            keyCodeDict.put("CAPS_LOCK", 20);

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

            keyCodeDict.put("MENU", 93);
            keyCodeDict.put("RIGHT_MCLICK", 93);

            keyCodeDict.put("WIN", 91); //91 un 92 are Windows buttons, so doesn't matter, which one will be used here
            keyCodeDict.put("WINDOWS", 91);

            keyCodeDict.put("BACKSLASH", 220);
            keyCodeDict.put("SLASH", 191);

            keyCodeDict.put("LEFT", 37);
            keyCodeDict.put("UP", 38);
            keyCodeDict.put("RIGHT", 39);
            keyCodeDict.put("DOWN", 40);

            keyCodeDict.put("NUM_LOCK", 144);
            keyCodeDict.put("NUMLOCK", 144);
            //in the same manner fill printable char codes
            keyCodeDict.put("|", 220);
            keyCodeDict.put("\\", 220);
            keyCodeDict.put("`", 192);
            keyCodeDict.put("~", 192);
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
            keyCodeDict.put("-", 189);
            keyCodeDict.put("_", 189);
            keyCodeDict.put("+", 187);
            keyCodeDict.put("=", 187);
            keyCodeDict.put("[", 219);
            keyCodeDict.put("{", 219);
            keyCodeDict.put("]", 221);
            keyCodeDict.put("}", 221);
            keyCodeDict.put(":", 186);
            keyCodeDict.put(";", 186);
            keyCodeDict.put("\"", 222);
            keyCodeDict.put("'", 222);
            keyCodeDict.put(",", 188);
            keyCodeDict.put("<", 188);
            keyCodeDict.put(".", 190);
            keyCodeDict.put(">", 190);
            keyCodeDict.put("/", 191);
            keyCodeDict.put("?", 191);
            keyCodeDict.put(" ", 32);
        }
    }
}
