package lv.emes.libraries.tools.lists;

import java.util.Map;

/**
 * Describes main operations that can be done for some storage.
 * <p>Public methods:
 * <ul>
 * <li>put</li>
 * <li>remove</li>
 * <li>find</li>
 * <li>findAll</li>
 * <li>removeAll</li>
 * </ul>
 *
 * @param <T>  type of items.
 * @param <ID> type of item identifiers.
 * @author eMeS
 * @version 1.1.
 */
public interface IStorageOperations<T, ID> {

    /**
     * Adds or replaces existing item in the storage with an another item.
     *
     * @param identifier an item identifier.
     * @param item       an item that will be in place of existing item.
     * @return previous item or null if there were no such item in storage with such identifier yet.
     */
    T put(ID identifier, T item);

    /**
     * Removes item with ID <b>identifier</b> from the storage.
     *
     * @param identifier an item identifier.
     * @return true if there were such item in storage, false if nothing was removed because there didn't exist such item.
     */
    boolean remove(ID identifier);

    /**
     * Looks for item with presented identifier <b>identifier</b>.
     *
     * @param identifier an item identifier.
     * @return an existing item or null if such item couldn't be found in the storage.
     */
    T find(ID identifier);

    /**
     * Gathers all the existing repository items to list in specific order.
     * This method should be optimized as much as it's possible and should be working faster
     * than calling <b>find</b> method <b>size</b> times.
     *
     * @return list of existing items in repository.
     */
    Map<ID, T> findAll();

    /**
     * Empties the storage by cleaning all the data from it.
     */
    void removeAll();
}
