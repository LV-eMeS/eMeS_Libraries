package lv.emes.libraries.tools.lists;

/**
 * Interface defines method for lists to enable concatenation with different list of elements with same type.
 * <p>Method:
 * <ul>
 * <li>concatenate</li>
 * </ul>
 *
 * @param <T> type of list elements.
 * @param <I> type of list element index.
 * @version 1.1.
 */
public interface MS_IContactableList<T, I> extends MS_ILoopableListWithItems<T, I> {
    /**
     * Concatenates this list with other list <b>otherList</b> by appending other list's content right after
     * last element of this list.
     * @param otherList list that will be appended to this list.
     */
    void concatenate(MS_IContactableList<T, I> otherList);
}
