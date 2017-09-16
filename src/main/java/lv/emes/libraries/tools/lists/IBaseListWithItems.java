package lv.emes.libraries.tools.lists;

/**
 * Interface defines method <b>forEachItem</b> to loop through every item in list.
 * Loop breaking is also supported by using <b>breakOngoingForLoop</b>.
 *
 * @param <T> type of list items.
 * @param <I> type of list item index.
 * @version 1.5.
 */
public interface IBaseListWithItems<T, I> {

    /**
     * @return count of items in list. [0..+]
     */
    int count();

    /**
     * Just an overloaded method of <b>count()</b>.
     *
     * @return count of items in list.
     * @see IBaseListWithItems#count()
     */
    int size();

    /**
     * Just an overloaded method of <b>count()</b>.
     *
     * @return count of items in list.
     * @see IBaseListWithItems#count()
     */
    int length();

    //methods of perambulation
    //------------------------------------------------------------------------------------------------------------------------

    /**
     * Gets item by index.
     *
     * @param index index of item in the list.
     * @return item of type T or null if such item couldn't be found in the list.
     */
    T get(I index);

    /**
     * Does break for method <b>forEachItem</b>.
     * In other words it is calling <b>setBreakOngoingForLoop(false)</b>.
     */
    void breakOngoingForLoop();

    /**
     * Implement setting of flag's for loop breaking value when inside of <b>forEachItem</b> method!
     *
     * @param value if true then loop will be broken and next iteration will not be reached.
     */
    void setBreakOngoingForLoop(boolean value);

    /**
     * Flag for <b>forEachItem</b> method that loop must be broken and execution of code must continue
     * to flow for next commands after loop.
     *
     * @return if true loop will be broken and next iteration will not be executed;
     * if false, loop will continue to next list item.
     */
    boolean getBreakOngoingForLoop();

    /**
     * Walks through all the items in list and does the defined action <b>action</b>.<br>
     * <code>
     * MS_StringList sl = new MS_StringList("One#Two#");<br>
     * sl.forEachItem((s, index) -&gt; {<br>
     * System.out.println(s);<br>
     * System.out.println(index);<br>
     * });<br>
     * </code>
     * <p>
     * Loop is broken if getBreakOngoingForLoop() returns true.
     * For example, code below will be executed only with first item even if there is more items in list.<br>
     * <code>
     * MS_StringList sl = new MS_StringList("One#Two#Three#");<br>
     * sl.forEachItem((str, index) -&gt; {<br>
     * System.out.println(str);<br>
     * sl.setBreakOngoingForLoop(true);<br>
     * });<br>
     * </code>
     * <p>
     * If some temporary variable is needed for lambda expression, just use <b>AtomicReference</b>!.<br>
     * <code>
     * MS_StringList sl = new MS_StringList("One#Two#");<br>
     * final AtomicReference&lt;Boolean&gt; itemFound = new AtomicReference&lt;&gt;(false);<br>
     * sl.forEachItem((str, index) -&gt; {<br>
     * System.out.println(str);<br>
     * if (str.equals("Two")) {<br>
     *  itemFound.set(true);<br>
     *  sl.breakOngoingForLoop();<br>
     *     }<br>
     * });<br>
     * </code>
     *
     * @param action method that describes the actions that every item must do.
     *               Method's incoming parameters are every item of the list and index of item.
     */
    void forEachItem(IFuncForEachItemLoopAction<T, I> action);

    /**
     * Loops through list starting from item with specified index <b>startFromIndex</b>.
     * <br><u>Note</u>: if presented index is not in the bounds of list item count this method will do nothing.
     * @param startFromIndex 0..count()-1
     * @param action method that describes the actions that every item must do.
     *               Method's incoming parameters are every item of the list and index of item.
     * @see IBaseListWithItems#forEachItem(IFuncForEachItemLoopAction)
     */
    void forEachItem(I startFromIndex, IFuncForEachItemLoopAction<T, I> action);

    /**
     * Loops through list starting from item with specified index <b>startFromIndex</b> till <b>endIndex</b> including it.
     * <br><u>Note</u>: if presented <b>startFromIndex</b> is not in the bounds of list item count this method will do nothing.
     * <br><u>Note</u>: if presented <b>endIndex</b> is not in the bounds of list item count this method will do nothing.
     * <br><u>Note</u>: if presented <b>endIndex</b> is less than <b>startFromIndex</b> indexes will switch places.
     * @param startFromIndex 0..count()-1
     * @param endIndex startFromIndex..count()-1
     * @param action method that describes the actions that every item must do.
     *               Method's incoming parameters are every item of the list and index of item.
     * @see IBaseListWithItems#forEachItem(IFuncForEachItemLoopAction)
     */
    void forEachItem(I startFromIndex, I endIndex, IFuncForEachItemLoopAction<T, I> action);
}
