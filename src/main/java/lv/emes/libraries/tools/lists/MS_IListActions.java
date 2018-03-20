package lv.emes.libraries.tools.lists;

/**
 * Interface defines main actions with T type lists.
 *
 * @param <T> type of list elements.
 * @version 1.8.
 */
public interface MS_IListActions<T> extends IPerambulateListActions<T> {

    /**
     * Add element to the end of T list.
     *
     * @param aItem object that has to be inserted to list.
     */
    void add(T aItem);

    /**
     * Insert element T before index <b>aIndex</b>. For example, list [A,C] after inserting(B, 1) becomes [A,B,C]
     *
     * @param aItem  = object of type T.
     * @param aIndex = [0..count-1]
     */
    void insert(int aIndex, T aItem);

    /**
     * Returns index of first matching item in the list. If element is not found returns -1.
     *
     * @param aItem object of type T which matches one of elements in the list.
     * @return [-1..count-1]
     */
    int getIndex(T aItem);

    /**
     * Returns <tt>true</tt> if this list contains the specified item.
     *
     * @param aItem item whose presence in this list is to be tested.
     * @return <tt>true</tt> if this list contains the specified element.
     */
    boolean contains(T aItem);

    /**
     * Changes item at index <b>aIndex</b> with passed item <b>aNewItem</b>. Does nothing if index not in bounds.
     *
     * @param aIndex   index of target item.
     * @param aNewItem item which will take place of an old item.
     */
    void edit(int aIndex, T aNewItem);

    /**
     * Removes element with index <b>aIndex</b> from the list.
     *
     * @param aIndex [0..count-1]
     * @return [-1..count-1]. If successfully removed, returns index of element that was removed, otherwise returns -1.
     */
    int remove(int aIndex);

    /**
     * Removed first matching object <b>aItem</b> from the list.
     *
     * @param aItem item to remove from list.
     * @return index of removed item.
     */
    int remove(T aItem);

    /**
     * Removes last element of the list.
     *
     * @return false if list is empty or for some other reason element cannot be removed, otherwise true.
     */
    boolean removeLast();

    /**
     * Removes first element of the list.
     *
     * @return false if list is empty or for some other reason element cannot be removed, otherwise true.
     */
    boolean removeFirst();

    /**
     * Clears list by removing all the elements.
     */
    void clear();
}
