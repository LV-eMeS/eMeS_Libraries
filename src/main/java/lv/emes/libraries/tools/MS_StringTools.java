package lv.emes.libraries.tools;

import lv.emes.libraries.tools.lists.MS_StringList;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static lv.emes.libraries.tools.MS_Tools.inRange;

/**
 * Module designed to combine different actions related to text formatting and other things to do with String type Objects.
 * Also there is included latvian laguage customization tools.
 * Methods:
 * -getRandomString
 * -isSubstring
 * -textContains
 * -replaceInString
 * -getInversedText
 * -getTextWithCapitalizedFirstLetter
 * -hasOnlyDigitsInText
 * -hasOnlyASCIILettersInText
 * -isCharASCIILetter
 * -removeLV
 * -textToWords
 * -removePunctuation
 * -getHashFromString
 * -intToStr
 * -strToInt
 * -chr
 * -ord
 * -pos
 * -getSubstring
 *
 * @version 1.7.
 */
public final class MS_StringTools {
    //konstantes
    public static final int C_DIACTRITIC_CHAR_COUNT = 13 * 2;
    public static final int C_PUNCTUATION_COUNT = 8;
    public static final char[] C_DIACTRITIC_CHARS = {'ē', 'ŗ', 'ū', 'ī', 'ō', 'ā', 'š', 'ģ', 'ķ', 'ļ', 'ž', 'č', 'ņ',
            'Ē', 'Ŗ', 'Ū', 'Ī', 'Ō', 'Ā', 'Š', 'Ģ', 'Ķ', 'Ļ', 'Ž', 'Č', 'Ņ'};
    public static final char[] C_NON_DIACTRITIC_CHARS = {'e', 'r', 'u', 'i', 'o', 'a', 's', 'g', 'k', 'l', 'z', 'c', 'n',
            'E', 'R', 'U', 'I', 'O', 'A', 'S', 'G', 'K', 'L', 'Z', 'C', 'N'};
    public static final String C_CARRIAGE_RETURN = "\r"; //chr 13 - carriage return (Mac style)
    public static final String C_LINE_FEED = "\n"; //chr 10 - line feed (Unix style)
    public static final String C_LINE_BRAKE = C_CARRIAGE_RETURN + C_LINE_FEED; //Windows style line breaks
    public static final String C_TAB_SPACE = "\t";
    public static final char[] C_PUNCTUATION_CHARS = {'.', ',', '!', '?', ';', ':', '/', '\\'};
    public static final String C_NUMBERS = "0123456789";
    public static final String C_SMALL_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String C_BIG_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //mazie tipi
    public static enum TNotificationLang {
        nlEN, nlLV
    }

    /**
     * Used to describe small letters, capital letters, digits and also symbols (stfgNormalSymbol :/*!@#$%^&amp; etc.),
     * and also special UTF-8 or Unicode symbols, for example, 1/4 symbol, degree symbol, etc. (stfgSpecialSymbol Ǒøė˧Ω etc.)
     * This enum is used in set <b>SetForCodeGenParams</b> to implement combinations of those 4 types of symbols.
     */
    public enum TSymbolTypeForGenerator {
        stfgSmallLetter, stfgBigLetter, stfgDigit, stfgNormalSymbol, stfgSpecialSymbol
    }

    public static Set<TSymbolTypeForGenerator> SetForCodeGenParams = Collections.synchronizedSet(EnumSet.noneOf(TSymbolTypeForGenerator.class));

    //klases
/*	public class T_DateTime { //pilda PASCAL analoga - ieraksts - lomu.
        int iMilliSec, iSecond, iMinute, iHour, iDay, iMonth, iYear;
		String sMilliSec, sSecond, sMinute, sHour, sDay, sMonth, sYear;
	}*/
    //metodes
    //------------------------------------------------------------------------------------------------------------------------
    private static char _returnSpecialSymbol() {
        //there is 4 regions in ASCII code that contains special symbols.
        //we will take one of those regions
        int resASCII = 0;
        switch (MS_Tools.randomNumber(1, 3)) {
            case 1:
                resASCII = MS_Tools.randomNumber(33, 47);
                break;
            case 2:
                resASCII = MS_Tools.randomNumber(58, 64);
                break;
            case 3:
                resASCII = MS_Tools.randomNumber(91, 94);
                break;
        }
        return chr(resASCII);
    }

    /**
     * Method implements generation of text of size <b>aSymbolCount</b>.
     * Generated text will consist of symbols that are defined in set <b>SetForCodeGenParams</b>.
     * <br><u>Example</u>: <code>
     * MSStringTools.SetForCodeGenParams.add(TSymbolTypeForGenerator.stfgSmallLetter);
     * MSStringTools.SetForCodeGenParams.add(TSymbolTypeForGenerator.stfgDigit);
     * String test = MSStringTools.RandomString(5, MSStringTools.SetForCodeGenParams); </code>
     * <br><u>Note</u>: there is no need to do <code>MSStringTools.SetForCodeGenParams.clear()</code>. Method will do it after successful generation.
     *
     * @param aSymbolCount count of generated symbols.
     * @param aOptions     settings, which symbols will resulting text consist.
     * @return text of random symbols: de24j
     * @see TSymbolTypeForGenerator
     */
    public static String getRandomString(int aSymbolCount, Set<TSymbolTypeForGenerator> aOptions) {
        String res = "";
        char ch = '\0';
        if (aOptions.size() == 0)
            aOptions.add(TSymbolTypeForGenerator.stfgSmallLetter);    //ja tukšs, tad paļubomu vismaz burtam jābūt
        for (int i = 1; i <= aSymbolCount; i++) {
            //48..57 digits; 65..90 capital letters; 97..122 small letters;
            int tmp;
            do {
                tmp = MS_Tools.randomNumber(1, 5);
                switch (tmp) {
                    case 1:
                        if (aOptions.contains(TSymbolTypeForGenerator.stfgSmallLetter)) {
                            ch = Character.toChars(MS_Tools.randomNumber(97, 122))[0];  //kāds mazais burts
                            break;
                        }
                    case 2:
                        if (aOptions.contains(TSymbolTypeForGenerator.stfgBigLetter)) {
                            ch = Character.toChars(MS_Tools.randomNumber(65, 90))[0];  //kāds Lielais burts
                            break;
                        }
                    case 3:
                        if (aOptions.contains(TSymbolTypeForGenerator.stfgDigit)) {
                            ch = Character.toChars(MS_Tools.randomNumber(48, 57))[0];  //kāds cipars
                            break;
                        }
                    case 4:
                        if (aOptions.contains(TSymbolTypeForGenerator.stfgNormalSymbol)) {
                            ch = _returnSpecialSymbol();  //kāds simbols: />$(]=?(*="">>\'[("
                            break;
                        }
                    case 5:
                        if (aOptions.contains(TSymbolTypeForGenerator.stfgSpecialSymbol)) {
                            ch = Character.toChars(MS_Tools.randomNumber(123, 254))[0];  //kāds speciālais simbols ¢­ÉÄÊèÉðÎÛ¡°¾Æ²
                            break;
                        }
                    default:
                        tmp = 0;
                        break; //continue loop until generated symbol fits input conditions
                } //switch ends here
            } while (tmp == 0);
            res = res.concat(Character.toString(ch)); //produce result char by char
        } //for ends here
        SetForCodeGenParams.clear(); //clear for the next use
        return res;
    }

    /**
     * Overloaded version with default option - return only random small letters.
     *
     * @param aSymbolCount length of result text.
     * @return random string.
     */
    public static String getRandomString(int aSymbolCount) {
        SetForCodeGenParams.clear();
        SetForCodeGenParams.add(TSymbolTypeForGenerator.stfgSmallLetter);
        return getRandomString(aSymbolCount, SetForCodeGenParams);
    }

    /**
     *
     * @param aSymbolCount
     * @return
     */
    //TODO use enum here!
    public static String getDateTimeNow(int aSymbolCount) {
        //TODO do this using MS_TimeTools
        return "";
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
     * Checks, whether text <b>aSmallString</b> is a part of some bigger text <b>aBigString</b>.
     * <p>This function is case sensitive.
     *
     * @param aBigString   text that will be tested.
     * @param aSmallString pattern to find in text.
     * @return true if <b>aBigString</b> contains <b>aSmallString</b>, otherwise returns false.
     */
    public static boolean isSubstring(String aSmallString, String aBigString) {
        return aBigString.contains(aSmallString);
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
            aReplaceWith = new String("");
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
     * Replaces part <b>aPattern</b> of text <b>aStr</b> with string <b>aReplaceWith</b>.
     * Always ignores case.
     *
     * @param aStr         "ABCDEFG"
     * @param aPattern     "CD"
     * @param aReplaceWith "XY"
     * @return "ABXYEFG"
     */
    public static String replaceInString(String aStr, String aPattern, String aReplaceWith) {
        return replaceInString(aStr, aPattern, aReplaceWith, false);
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
        if (inRange(aASCIISymbol, 65, 90) || inRange(aASCIISymbol, 97, 122))
            return true;
        else
            return false;
    }

    /**
     * Checks whether input text <b>aText</b> consists only of ASCII letters.
     *
     * @param aText ["abc"]["abc1"]
     * @return [true][false]
     * @see MS_StringTools#isCharASCIILetter(char)
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
            for (int j = 0; j < C_DIACTRITIC_CHAR_COUNT; j++) {
                if (aText.charAt(i) == C_DIACTRITIC_CHARS[j]) {
                    resAsArray[i] = C_NON_DIACTRITIC_CHARS[j];
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
     * Removes any commas, dots etc. Punctuation symbols declared in <b>C_PUNCTUATION_CHARS</b>.
     *
     * @param aText input text with punctuation.
     * @return output text without punctuation.
     */
    public static String removePunctuation(String aText) {
        int i = 0;
        if (aText == null) return null;
        String res = "";
        while (i < aText.length()) {
            boolean allowToAddSymbolToRes = true;
            int j = 0;
            char iThSymbol = aText.charAt(i);

            while (j < C_PUNCTUATION_COUNT) {
                if (iThSymbol == C_PUNCTUATION_CHARS[j]) {
                    allowToAddSymbolToRes = false;
                    break;
                }
                j++;
            }
            if (allowToAddSymbolToRes)
                res = res + iThSymbol;
            i++;
        }
        return res;
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
    //import static lv.emes.tools.MS_StringTools.*;
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
     * @param text    "Pattern X in this text"
     * @param pattern "X"
     * @return 8
     */
    public static int getPosition(String text, String pattern) {
        return pos(text, pattern);
    }

    public static String getTabSpace(int countOfTabs) {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i <= countOfTabs; i++)
            res.append(C_TAB_SPACE);
        return res.toString();
    }
}