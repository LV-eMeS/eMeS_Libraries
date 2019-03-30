package lv.emes.libraries.tools.lists;

import lv.emes.libraries.file_system.MS_TextFile;

import java.util.ArrayList;
import java.util.List;

import static lv.emes.libraries.utilities.MS_CodingUtils.inRange;

/**
 * Purpose of this class is to store many different but related texts in a list.
 * It implements number of methods for different operations with elements of the list.
 * <b>Delimiter</b> works to separate elements each from another when convert this list from big string.
 * <b>secondDelimiter</b> helps in cases when a symbol equal to <b>delimiter</b> is already used in big string.
 * <br><u>Note</u>: those delimiters shouldn't be changed unless they are often used in text which operated by this list.
 * <br><u>Note</u>: class is in it's final implementation state and therefore is considered as finished.
 *
 * @version 2.8.
 * @see MS_List
 */
public final class MS_StringList implements MS_ListActions<String>, MS_ContactableList<String, Integer> {

    private boolean flagForLoopBreaking;
    public static final char _DEFAULT_DELIMITER = '#';
    public static final char _SECOND_DEFAULT_DELIM = '`';//chr(9835);
    /**
     * Delimiter can be changed and will be used in <b>fromString</b> and <b>toString</b> methods.
     * Default value of this property is <b>_DEFAULT_DELIMITER</b>.
     *
     * @see MS_StringList#_DEFAULT_DELIMITER
     * @see MS_StringList#fromString(String)
     * @see MS_StringList#toString()
     * @see MS_StringList#fromTextFile(MS_TextFile)
     * @see MS_StringList#fromTextFile(String)
     */
    public char delimiter = _DEFAULT_DELIMITER;
    /**
     * Will be used when loading from string which contains some characters with value <b>delimiter</b> that are not actually perform delimiter functions.
     * In this case after <b>delimiter</b> symbol this <b>secondDelimiter</b> symbol will be inserted.
     * Default value for this property is <b>_SECOND_DEFAULT_DELIM</b>.
     *
     * @see MS_StringList#delimiter
     * @see MS_StringList#_SECOND_DEFAULT_DELIM
     */
    public char secondDelimiter = _SECOND_DEFAULT_DELIM;
    protected List<String> fList = new ArrayList<>(); //this list will be used for all the internal operations

    /**
     * Creates an String object list from String, which has all the elements delimited with <b>delimiter</b>.
     *
     * @param aString like: "a#B#C3#" when <b>delimiter = '#'</b> (by default).
     * @see MS_StringList#delimiter
     */
    public MS_StringList(String aString) {
        this.fromString(aString);
    }

    /**
     * Creates an String object list from String, which has all the elements delimited with <b>aDelimiter</b>.
     * It also sets a new value of <b>delimiter</b>.
     *
     * @param aString    ["a#B#C3#"].
     * @param aDelimiter a delimiter ['#'].
     */
    public MS_StringList(String aString, char aDelimiter) {
        this.fromString(aString, aDelimiter);
    }

    /**
     * Creates empty list with no loading from string and sets delimiter to presented <b>aDelimiter</b>.
     *
     * @param aDelimiter a delimiter.
     */
    public MS_StringList(char aDelimiter) {
        this.delimiter = aDelimiter;
    }

    /**
     * Creates empty list with no loading from string.
     */
    public MS_StringList() {
    }

    /**
     * Tries to create a list from file by reading all the contents of this file. If file not exist simply creates an instance of empty eMeS string list.
     *
     * @param aTextFile a eMeS text file that already have linked to a file.
     * @return a new instance of string list.
     */
    public static MS_StringList newInstance(MS_TextFile aTextFile) {
        MS_StringList res = new MS_StringList();
        res.fList = aTextFile.importStringListFromFile();
        return res;
    }

    /**
     * Tries to create a list from file by reading all the contents of this file. If file not exist simply creates an instance of empty eMeS string list.
     *
     * @param aPathToATextFile a path to a text file that will be read line by line.
     * @return a new instance of string list.
     */
    public static MS_StringList newInstance(String aPathToATextFile) {
        MS_TextFile file = new MS_TextFile(aPathToATextFile);
        return newInstance(file);
    }

    /**
     * Converts an array of strings to this kind of list.
     *
     * @param aTextArr an array of strings.
     * @return a new instance of string list.
     */
    public static MS_StringList newInstance(String[] aTextArr) {
        MS_StringList res = new MS_StringList();
        for (String str : aTextArr)
            res.add(str);
        return res;
    }

    /**
     * Creates copy of list.
     *
     * @param original list to copy from.
     * @return copied list.
     */
    public static MS_StringList copyOf(MS_StringList original) {
        return new MS_StringList(original.toString(), original.delimiter);
    }

    //OBJEKTA METODES
    //------------------------------------------------------------------------------------------------------------------------
    @Override
    public void add(String item) {
        if (item == null) item = "";
        fList.add(item);
    }

    @Override
    public void insert(int index, String item) {
        if (item == null) item = "";
        fList.add(index, item);
    }

    /**
     * Gets element by index.
     *
     * @param index index of element in the list (starting with 0).
     * @return element with index <b>index</b> or empty String if element not found in the list.
     */
    @Override
    public String get(Integer index) {
        if (this.listIsEmptyOrIndexNotInRange(index))
            return "";
        else
            return fList.get(index);
    }

    @Override
    public void breakOngoingForLoop() {
        setBreakOngoingForLoop(true);
    }

    @Override
    public void setBreakOngoingForLoop(boolean value) {
        flagForLoopBreaking = value;
    }

    @Override
    public boolean getBreakOngoingForLoop() {
        return flagForLoopBreaking;
    }

    @Override
    public void forEachItem(IFuncForEachItemLoopAction<String, Integer> action) {
        MS_ListActionWorker.forEachItem(this, action);
    }

    @Override
    public void forEachItem(Integer startFromIndex, IFuncForEachItemLoopAction<String, Integer> action) {
        MS_ListActionWorker.forEachItem(this, startFromIndex, action);
    }

    @Override
    public void forEachItem(Integer startFromIndex, Integer endIndex, IFuncForEachItemLoopAction<String, Integer> action) {
        MS_ListActionWorker.forEachItem(this, startFromIndex, endIndex, action);
    }

    public int getAsInteger(int index) {
        try {
            return Integer.parseInt(get(index));
        } catch (NumberFormatException exc) {
            return 0;
        }
    }

    @Override
    public int getIndex(String item) {
        for (int i = 0; i < count(); i++) {
            String el = get(i);
            if (el.equals(item))
                return i;
        }
        return -1;
    }

    @Override
    public boolean contains(String item) {
        return getIndex(item) >= 0;
    }

    @Override
    public void edit(int index, String aNewItem) {
        if (!listIsEmptyOrIndexNotInRange(index)) {
            remove(index);
            insert(index, aNewItem);
        }
    }

    @Override
    public int remove(int index) {
        if (this.listIsEmptyOrIndexNotInRange(index))
            return -1;
        else {
            fList.remove(index);
            return index;
        }
    }

    @Override
    public int remove(String item) {
        int elemToRemove = getIndex(item);
        if (elemToRemove != -1)
            return remove(elemToRemove);
        return elemToRemove;
    }

    @Override
    public boolean removeLast() {
        return remove(this.count() - 1) != -1;
    }

    @Override
    public boolean removeFirst() {
        return remove(0) != -1;
    }

    @Override
    public void clear() {
        fList.clear();
    }


    @Override
    public int count() {
        return fList.size();
    }

    @Override
    public int size() {
        return count();
    }

    @Override
    public int length() {
        return count();
    }

    //NEW METHODS, that aren't coming from interfaces
    //------------------------------------------------------------------------------------------------------------------------

    /**
     * Converts list of strings to eMeS string list.
     *
     * @param aList list containing objects of type String.
     */
    public void fromList(List<String> aList) {
        fList = aList;
    }

    /**
     * Copies all the information of presented list to this one.
     *
     * @param aList an another eMeS string list.
     */
    public void fromList(MS_StringList aList) {
        this.delimiter = aList.delimiter;
        this.secondDelimiter = aList.secondDelimiter;
        this.fList = aList.fList;
    }

    /**
     * @return all the String type objects as list of String.
     */
    public List<String> toList() {
        return fList;
    }

    /**
     * Returns all the elements of this list delimited with <b>delimiter</b>.
     * If some element already contains a char equal to <b>delimiter</b> then <b>secondDelimiter</b> is added after that char to mark it not as delimiter.
     *
     * @return delimited text of all the elements of eMeS string list.
     * @see MS_StringList#delimiter
     * @see MS_StringList#secondDelimiter
     */
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (String str : fList) {
            res.append(pAddSecDelim(str));
            res.append(delimiter);
        }
        return res.toString();
    }

    /**
     * Returns all the elements of this list delimited with <b>delimiter</b>, but do not add delimiter after last element of string list.
     * If some element already contains a char equal to <b>delimiter</b> then <b>secondDelimiter</b> is added after that char to mark it not as delimiter.
     *
     * @return delimited text of all the elements of eMeS string list.
     * @see MS_StringList#delimiter
     * @see MS_StringList#secondDelimiter
     */
    public String toStringWithNoLastDelimiter() {
        StringBuilder res = new StringBuilder();
        String str;
        if (fList.size() > 1) {
            for (int i = 0; i < fList.size() - 1; i++) { //everything but last element
                str = fList.get(i);
                res.append(pAddSecDelim(str));
                res.append(delimiter);
            }
            res.append(pAddSecDelim(fList.get(fList.size() - 1))); //appending last element without separator
        } else if (fList.size() == 1) { //only last element and no delimiters at all
            res.append(pAddSecDelim(fList.get(fList.size() - 1)));
        }
        return res.toString();
    }

    /**
     * Returns all the elements of this list delimited with <b>delimiter</b>.
     * Unlike <b>toString</b> method this method ignores second delimiter, which means that if string
     * originally will contain elements same as delimiter then those elements will be also printed without second delimiter.
     *
     * @return delimited text of all the elements of eMeS string list.
     * @see MS_StringList#delimiter
     * @see MS_StringList#secondDelimiter
     */
    public String toText() {
        StringBuilder res = new StringBuilder();
        for (String str : fList) {
            res.append(str);
            res.append(delimiter);
        }
        return res.toString();
    }

    private String pAddSecDelim(String aStringToCheck) {
        int i = 0;
        StringBuilder res = new StringBuilder();
        while (i < aStringToCheck.length()) {
            res.append(aStringToCheck.charAt(i));
            if (aStringToCheck.charAt(i) == delimiter)
                res.append(secondDelimiter);
            i++;
        }
        return res.toString();
    }

    /**
     * Loads elements from delimited String type text to this kind of list.
     * Every pattern in text <b>aString</b> that ends with element <b>delimiter</b> becomes to a new element of this list.
     *
     * @param aString text that is properly delimited with <b>delimiter</b>.
     * @return this string list.
     * @see MS_StringList#delimiter
     */
    public MS_StringList fromString(String aString) {
        this.fromString(aString, this.delimiter);
        return this;
    }

    /**
     * Loads elements from delimited String type text to this kind of list.
     * Every pattern in text <b>aString</b> that ends with element <b>aDelimiter</b> becomes to a new element of this list.
     *
     * @param aString    text that is properly delimited with <b>aDelimiter</b>.
     * @param aDelimiter a delimiter.
     * @return this string list.
     */
    public MS_StringList fromString(String aString, char aDelimiter) {
        this.delimiter = aDelimiter;
        clear(); //clear the list
        if (aString == null) return this; //if no text given, list will be left empty
        int aStringLen = aString.length();
        if (aStringLen == 0) return this;
        StringBuilder el = new StringBuilder(); //text, which will be saved as concrete element of list
        //make sure data ends with delimiter, in case it is not so for input
        if (aString.charAt(aString.length() - 1) != aDelimiter) {
            aString = aString + aDelimiter;
            aStringLen++;
        }

        int i = 0;
        while (i < aStringLen) {
            if (aString.charAt(i) != aDelimiter)
                el.append(aString.charAt(i));
            else {
                //if delimiter met...
                if ((i + 1 < aString.length()) && (aString.charAt(i + 1) == secondDelimiter)) {
                    //if there is second delimiter after this one, it means that delimiter was a symbol, and therefore
                    //we must continue on element content building
                    el.append(aString.charAt(i));
                    i++; //move on
                } else {//if there is no second delimiter after delimiter, it means that element ends here - lets add it to the list!
                    this.add(el.toString());
                    el = new StringBuilder(); //clear content of the element
                }
            }
            i++;
        } //while loop ends here
        return this;
    }

    private boolean listIsEmptyOrIndexNotInRange(int index) {
        return (count() == 0) || !inRange(index, 0, this.count() - 1);
    }

    /**
     * Loads elements from text file to this kind of list. A <b>delimiter</b> is used also.
     *
     * @param aPathToTextFile path to a file.
     * @return this string list.
     * @see MS_StringList#delimiter
     */
    public MS_StringList fromTextFile(String aPathToTextFile) {
        this.clear();
        this.fList = new MS_TextFile(aPathToTextFile).importStringListFromFile();
        return this;
    }

    /**
     * Loads elements from text file to this kind of list. A <b>delimiter</b> is used also.
     *
     * @param aTextFile an eMeS text file with already assigned physical file.
     * @return this string list.
     * @see MS_StringList#delimiter
     */
    public MS_StringList fromTextFile(MS_TextFile aTextFile) {
        this.clear();
        this.fList = aTextFile.importStringListFromFile();
        return this;
    }

    /**
     * Creates a text file and saves all the contents of eMeS string list to that file.
     *
     * @param aPathToTextFile path to a file.
     */
    public void toTextFile(String aPathToTextFile) {
        MS_TextFile file = new MS_TextFile(aPathToTextFile);
        file.exportStringListToFile(fList);
    }

    /**
     * Converts this list to array of strings.
     *
     * @return an array containing all the list elements.
     */
    public String[] toArray() {
        int c = this.count();
        String[] res = new String[c];
        for (int i = 0; i < c; i++)
            res[i] = this.get(i);
        return res;
    }

    /**
     * At the end of this list adds elements of list <b>anotherPart</b>.
     *
     * @param anotherPart list containing elements to add to this list.
     */
    public void concatenate(MS_StringList anotherPart) {
        for (int i = 0; i < anotherPart.length(); i++)
            this.add(anotherPart.get(i));
    }

    @Override
    public void concatenate(List<String> otherList) {
        otherList.forEach(this::add);
    }
}