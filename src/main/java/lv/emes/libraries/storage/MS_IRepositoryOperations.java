package lv.emes.libraries.storage;

import java.util.Map;

/**
 * Describes main operations that can be done for some repository.
 * <p>Public methods:
 * <ul>
 * <li>add</li>
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
 * @version 2.0.
 */
public interface MS_IRepositoryOperations<T, ID> {

    /**
     * Adds new item to the repository. If item with such identifier exists, does nothing.
     *
     * @param identifier an item identifier.
     * @param item       an item that will be added to repository.
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    void add(ID identifier, T item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException;

    /**
     * Adds or replaces existing item in the repository with an another item.
     *
     * @param identifier an item identifier.
     * @param item       an item that will be in place of existing item.
     * @return previous item or null if there were no such item in repository with such identifier yet.
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    T put(ID identifier, T item) throws UnsupportedOperationException, MS_RepositoryDataExchangeException;

    /**
     * Removes item with ID <b>identifier</b> from the repository.
     *
     * @param identifier an item identifier.
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    void remove(ID identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException;

    /**
     * Looks for item with presented identifier <b>identifier</b>.
     *
     * @param identifier an item identifier.
     * @return an existing item or null if such item couldn't be found in the repository.
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    T find(ID identifier) throws UnsupportedOperationException, MS_RepositoryDataExchangeException;

    /**
     * Gathers all the existing repository items in specific order to new instance of map.
     * E.g. changing content of this map has no effect on actual data in repository.
     * This method should be optimized as much as it's possible and should be working faster
     * than calling <b>find</b> method <b>size</b> times.
     *
     * @return map of existing items in repository.
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    Map<ID, T> findAll() throws UnsupportedOperationException, MS_RepositoryDataExchangeException;

    /**
     * Empties the repository by cleaning all the data from it.
     *
     * @throws UnsupportedOperationException   if this operation is not supported for this kind of repository or some
     *                                         specific conditions in order to operate within this repository isn't met.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    void removeAll() throws UnsupportedOperationException, MS_RepositoryDataExchangeException;
}