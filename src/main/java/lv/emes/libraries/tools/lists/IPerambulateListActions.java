package lv.emes.libraries.tools.lists;
/**
 * Interface defines methods for list that can be perambulated.
 * Methods:<br>
 *     -first;
 *     -next;
 *     -prev;
 *     -last
 *     -currentIndexInsideTheList;
 *     -current
 * @param <T> type of list elements.
 * @version 1.3.
 */
public interface IPerambulateListActions<T> extends IConcateableList<T> {
	/**
	 * Index of current element. Returns -1 when reached end of list.
	 * @return [-1..count-1] Value depends on use of <b>first</b>, <b>next</b>, <b>prev</b> and <b>last</b>.
	 */
	int getIndexOfCurrent();

	/**
	 * Sets the position of current element in the list.
	 * @param aIndexOfCurrent index of current element. Used in perambulation purposes. Pass -1 to set reference to end of list!
	 */
	void setIndexOfCurrent(int aIndexOfCurrent);

	/**
	 * Returns current element.
	 * @return null if element not found in the list.
	 */
	T current();

	/**
	 * Points <b>indexOfCurrent</b> to first element of the list. If list is empty then <b>indexOfCurrent = -1</b>.
	 */
	default void first()  {
		setIndexOfCurrent(0);
	}

	/**
	 * Points <b>indexOfCurrent</b> to last element of the list. If list is empty then <b>indexOfCurrent = -1</b>.
	 */
	void last();

	/**
	 * Try to increase value of <b>indexOfCurrent</b> by 1 to move forward. If <b>indexOfCurrent = count</b> then <b>currentIndexInsideTheList = false</b>.
	 */
	void next();

	/**
	 * Try to decrease value of <b>indexOfCurrent</b> by 1 to move backward. If <b>indexOfCurrent = -1</b> then <b>currentIndexInsideTheList = false</b>.
	 */
	void prev();
	
	/**
	 * Tests whether current element is inside the list.
	 * @return returns true if current element is still in the list. Returns false if by perambulation process all the list is walked through.
	 */
	boolean currentIndexInsideTheList();
}
