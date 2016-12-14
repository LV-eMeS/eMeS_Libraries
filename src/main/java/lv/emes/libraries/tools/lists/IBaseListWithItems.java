package lv.emes.libraries.tools.lists;
/**
 * Interface defines method for lists every item to perform.
 * Method:<br>
 *     -doWithEveryItem;
 * @param <T> type of list elements.
 * @version 1.1.
 */
public interface IBaseListWithItems<T> {
	/**
	 * @return count of elements in list. [0..+]
	 */
	int count();

	/**
	 * Just an overloaded method of <b>count()</b>.
	 * @return count of items in list.
	 * @see IBaseListWithItems#count()
	 */
	default int size() {
		return count();
	}

	/**
	 * Just an overloaded method of <b>count()</b>.
	 * @return count of items in list.
	 * @see IBaseListWithItems#count()
	 */
	default int length() {
		return count();
	}

	//methods of perambulation
	//------------------------------------------------------------------------------------------------------------------------
	/**
	 * Gets element by index.
	 * @param aIndex index of element in the list.
	 * @return element of type T.
	 */
	T get(int aIndex);

	/**
	 * Does break for method <b>doWithEveryItem</b>.
	 * In other words it is calling <b>setBreakDoWithEveryItem(false)</b>.
	 */
	default void breakDoWithEveryItem(){
		setBreakDoWithEveryItem(true);
	}

	/**
	 * Implement setting of flag's for loop breaking value when inside of <b>doWithEveryItem</b> method!
	 * @param value if true then loop will be broken and next iteration will not be reached.
	 */
	void setBreakDoWithEveryItem(boolean value);

	/**
	 * Flag for <b>doWithEveryItem</b> method that loop must be broken and execution of code must continue
	 * to flow for next commands after loop.
	 * @return if true loop will be broken and next iteration will not be executed;
	 * if false, loop will continue to next list item.
	 */
	boolean getBreakDoWithEveryItem();

	/**
	 * Walks through all the elements in list and does the defined action <b>action</b>.
	 * <code>
     * MS_StringList sl = new MS_StringList("One#Two#");
     * sl.doWithEveryItem((s, index) -&gt; {
	 * 	System.out.println(s);
	 * 	System.out.println(index);
     * });
	 * </code>
	 * Loop is broken if getBreakDoWithEveryItem() returns true.
	 * For example, code below will be executed only with first element even if there is more elements in list.
	 * <code>
	 * MS_StringList sl = new MS_StringList("One#Two#");
	 * sl.doWithEveryItem((s, index) -&gt; {
	 * 	System.out.println(s);
	 * 	setBreakDoWithEveryItem(true);
	 * });
	 * </code>
	 * @param action method that describes the actions that every element must do.
	 *               Method's incoming parameters are every item of the list and index of item.
	 */
	default void doWithEveryItem(IFuncSomeAction<T> action) {
		setBreakDoWithEveryItem(false);
		if (action != null)
			for (int i = 0; i < this.count(); i++) {
				T itm = this.get(i);
				action.doAction(itm, i);
				if (getBreakDoWithEveryItem())
					break;
			}
	}
}
