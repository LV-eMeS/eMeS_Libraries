package lv.emes.libraries.tools.lists;

/**
 * Describes main operations that can be done for some storage.
 * <p>Public methods:
 * <ul>
 * <li>put</li>
 * <li>remove</li>
 * <li>find</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @param <T> type of items.
 * @param <ID> type of item identifiers.
 */
public interface IStorageOperations<T, ID> {

    /**
     * Adds or replaces existing item in the storage with an another item.
     * @param identifier an item identifier.
     * @param item an item that will be in place of existing item.
     * @return previous item or null if there were no such item in storage with such identifier yet.
     */
    T put(ID identifier, T item);

    /**
     * Removes item with ID <b>identifier</b> from the storage.
     * @param identifier an item identifier.
     * @return removed item or null if there is no such item in storage.
     */
    T remove(ID identifier);

    /**
     * Looks for item with presented identifier <b>identifier</b>.
     * @param identifier an item identifier.
     * @return an existing item or null if such item couldn't be found in the storage.
     */
    T find(ID identifier);
}
