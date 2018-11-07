package lv.emes.libraries.utilities;

import lv.emes.libraries.communication.cryptography.MS_Hash;
import lv.emes.libraries.tools.lists.MS_StringList;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static lv.emes.libraries.utilities.MS_CodingUtils.inRange;

/**
 * Module designed to combine different actions related to text formatting and other things to do with String type Objects.
 * Also there is included latvian language customization tools.
 * <p>Methods:
 * <ul>
 *     <li>getRandomString</li>
 *     <li>isSubstring</li>
 *     <li>textContains</li>
 *     <li>replaceInString</li>
 *     <li>getInversedText</li>
 *     <li>getTextWithCapitalizedFirstLetter</li>
 *     <li>hasOnlyDigitsInText</li>
 *     <li>hasOnlyASCIILettersInText</li>
 *     <li>isCharASCIILetter</li>
 *     <li>removeLV</li>
 *     <li>textToWords</li>
 *     <li>removePunctuation</li>
 *     <li>getHashFromString</li>
 *     <li>intToStr</li>
 *     <li>strToInt</li>
 *     <li>chr</li>
 *     <li>ord</li>
 *     <li>pos</li>
 *     <li>getSubstring</li>
 * </ul>
 *
 * @version 1.9.
 */
public final class MS_StringUtils {

    private MS_StringUtils() {
    }

    //constants
    public static final int _DIACTRITIC_CHAR_COUNT = 13 * 2;
    public static final int _PUNCTUATION_COUNT = 8;
    public static final char[] _DIACTRITIC_CHARS = {'ē', 'ŗ', 'ū', 'ī', 'ō', 'ā', 'š', 'ģ', 'ķ', 'ļ', 'ž', 'č', 'ņ',
            'Ē', 'Ŗ', 'Ū', 'Ī', 'Ō', 'Ā', 'Š', 'Ģ', 'Ķ', 'Ļ', 'Ž', 'Č', 'Ņ'};
    public static final char[] _NON_DIACTRITIC_CHARS = {'e', 'r', 'u', 'i', 'o', 'a', 's', 'g', 'k', 'l', 'z', 'c', 'n',
            'E', 'R', 'U', 'I', 'O', 'A', 'S', 'G', 'K', 'L', 'Z', 'C', 'N'};
    public static final String _CARRIAGE_RETURN = "\r"; //chr 13 - carriage return (Mac style)
    public static final String _LINE_FEED = "\n"; //chr 10 - line feed (Unix style)
    public static final String _LINE_BRAKE = _CARRIAGE_RETURN + _LINE_FEED; //Windows style line breaks
    public static final String _TAB_SPACE = "\t";
    public static final char[] _PUNCTUATION_CHARS = {'.', ',', '!', '?', ';', ':', '/', '\\'};
    public static final Character _SINGLE_QUOTE = '\'';
    public static final String _SINGLE_QUOTE_2X = "''";

    public enum TNotificationLang {
        nlEN, nlLV
    }

    /**
     * Checks, whether text <b>aSmallString</b> is a part of some bigger text <b>aBigString</b>.
     *
     * @param aBigString     text that will be tested.
     * @param aSmallString   pattern to find in text.
     * @param aCaseSensitive true if checking will be done case sensitively.
     * @return true if <b>aText</b> contains <b>pattern</b>, otherwise returns false.
     */
    public static boolean isSubstring(String aSmallString, String aBigString, boolean aCaseSensitive) {
        if (aBigString == null) aBigString = "";
        if (aSmallString == null) aSmallString = "";
        if (aCaseSensitive) {
            return isSubstring(aSmallString, aBigString);
        } else {
            return isSubstring(aSmallString.toUpperCase(), aBigString.toUpperCase());
        }
    }

    /**
     * Checks, whether text <b>smallString</b> is a part of some bigger text <b>bigString</b>.
     * <p>This function is case sensitive.
     *
     * @param bigString   text that will be tested.
     * @param smallString pattern to find in text.
     * @return true if <b>bigString</b> contains <b>smallString</b>, otherwise returns false.
     */
    public static boolean isSubstring(String smallString, String bigString) {
        if (smallString == null && bigString == null) return true;
        else if (bigString != null && smallString == null) return false;
        else if (bigString == null) return false;
        return bigString.contains(smallString);
    }

    /**
     * Checks, if text <b>aText</b> contains <b>aChar</b>.
     *
     * @param aText a text which will be checked.
     * @param aChar character to check.
     * @return true if <b>aText</b> contains <b>aChar</b>, otherwise returns false.
     */
    public static boolean textContains(String aText, char aChar) {
        if (aText == null) return false;
        for (int i = 0; i < aText.length(); i++)
            if (aText.charAt(i) == aChar)
                return true;
        return false;
    }

    /**
     * Checks, whether text <b>aText</b> contains some smaller text <b>pattern</b>.
     * <p>This function is case sensitive.
     *
     * @param text    text that will be tested.
     * @param pattern pattern to find in text.
     * @return true if <b>aText</b> contains <b>pattern</b>, otherwise returns false.
     */
    public static boolean textContains(String text, String pattern) {
        return isSubstring(pattern, text);
    }

    /**
     * Checks, whether text <b>aText</b> contains some smaller text <b>pattern</b>.
     *
     * @param text          text that will be tested.
     * @param pattern       pattern to find in text.
     * @param caseSensitive true if checking will be done case sensitively.
     * @return true if <b>aText</b> contains <b>pattern</b>, otherwise returns false.
     */
    public static boolean textContains(String text, String pattern, boolean caseSensitive) {
        return isSubstring(pattern, text, caseSensitive);
    }

    /**
     * Replaces part <b>aPattern</b> of text <b>aStr</b> with string <b>aReplaceWith</b>.
     *
     * @param aStr                "ABCDEFG"
     * @param aPattern            "CD"
     * @param aReplaceWith        "XY"
     * @param aCaseSensitiveCheck if true and text <b>aStr</b> is "ABcdEFG" then replacement will not be made.
     * @return "ABXYEFG"
     */
    public static String replaceInString(String aStr, String aPattern, String aReplaceWith, boolean aCaseSensitiveCheck) {
        if (aStr == null) {
            return null;
        }
        if (aPattern == null || aPattern.length() == 0) {
            return aStr;
        }
        if (aPattern.length() > aStr.length()) {
            return aStr;
        }
        if (aReplaceWith == null)
            aReplaceWith = "";
        int counter = 0;
        String thesubstr = "";
        while ((counter < aStr.length())
                && (aStr.substring(counter).length() >= aPattern.length())) {
            thesubstr = aStr.substring(counter, counter + aPattern.length());
            if (aCaseSensitiveCheck) {
                if (thesubstr.equals(aPattern)) {
                    aStr = aStr.substring(0, counter) + aReplaceWith
                            + aStr.substring(counter + aPattern.length());
                    counter += aReplaceWith.length();
                } else {
                    counter++;
                }
            } else {
                if (thesubstr.equalsIgnoreCase(aPattern)) {
                    aStr = aStr.substring(0, counter) + aReplaceWith
                            + aStr.substring(counter + aPattern.length());
                    // Failing to increment counter by replacetxt.length() leaves you open
                    // to an infinite-replacement loop scenario: Go to replace "a" with "aa" but
                    // increment counter by only 1 and you'll be replacing 'a's forever.
                    counter += aReplaceWith.length();
                } else {
                    counter++; // No match so move on to the next character from
                    // which to check for a findtxt string match.
                }
            }
        }
        return aStr;
    }

    /**
     * Replaces part <b>pattern</b> of text <b>targetString</b> with string <b>replaceWith</b>.
     * Always ignores case.
     *
     * @param targetString         "ABCDEFG"
     * @param pattern     "CD"
     * @param replaceWith "XY"
     * @return "ABXYEFG"
     */
    public static String replaceInString(String targetString, String pattern, String replaceWith) {
        return replaceInString(targetString, pattern, replaceWith, false);
    }

    /**
     * Returns inversed text.
     *
     * @param atext "ABC"
     * @return "CBA"
     */
    public static String getInversedText(String atext) {
        if (atext == null) atext = "";
        String res = "";
        for (int i = atext.length() - 1; i >= 0; i--)
            res = res.concat(Character.toString(atext.charAt(i)));
        return res;
    }

    /**
     * Returns text same text as input, changing only first letter to capital.
     *
     * @param aText "this is a text"
     * @return "This is a text"
     */
    public static String getTextWithCapitalizedFirstLetter(String aText) {
        if (aText != null && aText.length() > 0) {
            //paņemsim pirmo charu
            char first = aText.charAt(0);
            first = Character.toUpperCase(first);
            char[] inputText = aText.toCharArray();
            inputText[0] = first;
            return new String(inputText);
        } else //if text is null or empty
            return aText;
    }

    /**
     * Checks whether text consists of letters only.
     *
     * @param aText ["1234"]["1abc1"]["0"]
     * @return [true][false][true]
     */
    public static boolean hasOnlyDigitsInText(String aText) {
        try {
            strToInt(aText);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks only for ASCII letter. Chars that aren't letters or are non ASCII letters (cyrillic and other language characters) are not accepted.
     *
     * @param aSymbol [a..z, A..Z][0..1, āčūī..., ~!@#$%^&amp;*...]
     * @return [true][false]
     */
    public static boolean isCharASCIILetter(char aSymbol) {
        int aASCIISymbol = ord(aSymbol);
        return inRange(aASCIISymbol, 65, 90) || inRange(aASCIISymbol, 97, 122);
    }

    /**
     * Checks whether input text <b>aText</b> consists only of ASCII letters.
     *
     * @param aText ["abc"]["abc1"]
     * @return [true][false]
     * @see MS_StringUtils#isCharASCIILetter(char)
     */
    public static boolean hasOnlyASCIILettersInText(String aText) {
        if (aText == null || aText.length() == 0) return false;

        for (int i = 0; i < aText.length(); i++) {
            char symbol = aText.charAt(i);
            if (!isCharASCIILetter(symbol)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes all the latvian diacritic symbols from text.
     *
     * @param aText "Ō, kāds brīnumiņš!"
     * @return "O, kads brinumins!"
     */
    public static String removeLV(String aText) {
        if (aText == null) return aText;

        char[] resAsArray = aText.toCharArray();
        for (int i = 0; i < aText.length(); i++) {
            for (int j = 0; j < _DIACTRITIC_CHAR_COUNT; j++) {
                if (aText.charAt(i) == _DIACTRITIC_CHARS[j]) {
                    resAsArray[i] = _NON_DIACTRITIC_CHARS[j];
                } //if ends here
            }
        } //outer for ends here
        return new String(resAsArray);
    }

    /**
     * Splits given text into words, removing any spaces between words in text.
     *
     * @param aText a sentence.
     * @return list of words.
     */
    public static MS_StringList textToWords(String aText) {
        MS_StringList res = new MS_StringList(aText, ' ');
        return res;
    }

    /**
     * Removes any commas, dots etc. Punctuation symbols declared in <b>_PUNCTUATION_CHARS</b>.
     *
     * @param aText input text with punctuation.
     * @return output text without punctuation.
     */
    public static String removePunctuation(String aText) {
        int i = 0;
        if (aText == null) return null;
        StringBuilder res = new StringBuilder();
        while (i < aText.length()) {
            boolean allowToAddSymbolToRes = true;
            int j = 0;
            char iThSymbol = aText.charAt(i);

            while (j < _PUNCTUATION_COUNT) {
                if (iThSymbol == _PUNCTUATION_CHARS[j]) {
                    allowToAddSymbolToRes = false;
                    break;
                }
                j++;
            }
            if (allowToAddSymbolToRes)
                res.append(iThSymbol);
            i++;
        }
        return res.toString();
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm using default salt value and default key length.
     *
     * @param aTextForHashGen a text which will be used to do hashing.
     * @return empty string in case if <b>aTextToHash</b> is empty.
     */
    public static String getHashFromString(String aTextForHashGen) {
        return MS_Hash.getHash(aTextForHashGen);
    }

    //------------------------------------------------------------------------------------------------------------------------
    //methods for static import.
    //import static lv.emes.tools.MS_StringUtils.*;
    //import static lv.emes.tools.MSStringTools.intToStr;
    public static String intToStr(int aValue) {
        return Integer.toString(aValue);
    }

    public static int strToInt(String aValue) {
        return Integer.parseInt(aValue);
    }

    /**
     * ASCII -&gt; char.
     * @param ascii ASCII code.
     * @return char matching presented ASCII code <b>ascii</b>.
     */
    public static char chr(int ascii) {
        return Character.toChars(ascii)[0];
    }

    /**
     * Char -&gt; ASCII.
     * @param aValue a char.
     * @return ASCII code for presented char <b>aValue</b>.
     */
    public static int ord(char aValue) {
        return (int) aValue;
    }

    /**
     * Returns a part of another text starting from <b>from</b> symbol in text <b>text</b> and ending with <b>till</b> symbol.
     *
     * @param text  ["given text"]["AbcdXXXefg"]
     * @param from [0][4]
     * @param till [5][7]
     * @return ["given"]["XXX"]
     */
    public static String getSubstring(String text, int from, int till) {
        if (from - 1 > text.length())
            return "";

        if (till > text.length())
            till = text.length();
        char[] chars = new char[till - from];
        text.getChars(from, till, chars, 0);
        return new String(chars);
    }

    /**
     * Returns a part of another text starting from <b>from</b> symbol in text <b>text</b> and ending with <b>till</b> symbol.
     *
     * @param text  ["given text"]["AbcdXXXefg"]
     * @param from [0][4]
     * @param till [5][7]
     * @return ["given"]["XXX"]
     */
    public static String substring(String text, int from, int till) {
        return getSubstring(text, from, till);
    }

    /**
     * Returns position of pattern <b>pattern</b> in text <b>text</b>.
     *
     * @param text    "Pattern X in this text"
     * @param pattern "X"
     * @return 8
     */
    public static int pos(String text, String pattern) {
        return text.indexOf(pattern);
    }

    /**
     * Returns position of pattern <b>pattern</b> in text <b>text</b>.
     *
     * @param pattern "X"
     * @param text    "Pattern X in this text"
     * @return 8
     */
    public static int getPosition(String pattern, String text) {
        return pos(text, pattern);
    }

    public static String getTabSpace(int countOfTabs) {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= countOfTabs; i++)
            res.append(_TAB_SPACE);
        return res.toString();
    }

    /**
     * This method can be used to form constants that includes many strings related to one particular concept.
     * @param strings all the strings that will be included to result array.
     * @return an array of strings.
     */
    public static String[] getStringArray(String... strings) {
        return strings;
    }

    /**
     * Adds single quote characters before and after presented string <b>value</b>.
     * Single quotation marks inside presented string (like this: <b>'</b>) are escaped with extra single quotation mark (like this: <b>''</b>).
     * @param value preferable value without any quotation.
     * @return value quoted with single quotes from both sides.
     */
    public static String toQuotedText(String value) {
        value = MS_StringUtils.replaceInString(value, _SINGLE_QUOTE.toString(), _SINGLE_QUOTE_2X);
        return _SINGLE_QUOTE + value + _SINGLE_QUOTE;
    }

    public static String convertMillisToSecsString(long milliseconds) {
        BigDecimal res = new BigDecimal(milliseconds).movePointLeft(3);
        return res.setScale(2, RoundingMode.HALF_UP).toString();
    }
}