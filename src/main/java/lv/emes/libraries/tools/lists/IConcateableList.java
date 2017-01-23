package lv.emes.libraries.tools.lists;

/**
 * Interface defines method for lists to enable concatenation with different list of elements with same type.
 * Method:<br>
 * -concatenate;
 *
 * @param <T> type of list elements.
 * @version 1.0.
 */
public interface IConcateableList<T> extends IBaseListWithItems<T> {
    /**
     * Concatenates this list with other list <b>otherList</b> by appending other list's content right after
     * last element of this list.
     * @param otherList list that will be appended to this list.
     */
    void concatenate(IConcateableList<T> otherList);
}
