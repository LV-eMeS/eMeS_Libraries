package lv.emes.libraries.tools.lists;

import java.util.List;

/**
 * Interface defines method for lists to enable concatenation with different list of elements with same type.
 * <p>Method:
 * <ul>
 * <li>concatenate</li>
 * </ul>
 *
 * @param <T> type of list elements.
 * @param <I> type of list element index.
 * @version 3.0.
 */
public interface MS_ContactableList<T, I> extends MS_IterableListWithItems<T, I> {

    /**
     * Concatenates this list with other list <b>otherList</b> by appending other list's content right after
     * last element of this list.
     * @param otherList list that will be appended to this list.
     */
    void concatenate(List<T> otherList);
}
