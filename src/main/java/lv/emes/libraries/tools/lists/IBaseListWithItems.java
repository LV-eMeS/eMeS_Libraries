package lv.emes.libraries.tools.lists;
/**
 * Interface defines method for lists every item to perform.
 * Method:<br>
 *     -doWithEveryItem;
 * @param <T> type of list elements.
 * @version 1.0.
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
	 * Walks through all the elements in list and does the defined action <b>action</b>.
	 * <pre><code>{@code
     * MS_StringList sl = new MS_StringList("One#Two#");
     * sl.doWithEveryItem((s, index) -> {
	 * 	System.out.println(s);
	 * 	System.out.println(index);
     * });
	 * }</code></pre>
	 * @param action method that describes the actions that every element must do.
	 *               Method's incoming parameters are every item of the list and index of item.
	 */
	default void doWithEveryItem(IFuncSomeAction<T> action) {
		if (action != null)
			for (int i = 0; i < this.count(); i++) {
				T itm = this.get(i);
				action.doAction(itm, i);
			}
	}
}
