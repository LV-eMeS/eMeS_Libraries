package lv.emes.libraries.storage;

/**
 * Interface for repositories ({@link MS_Repository}), which support batch adding operation in order to
 * save resources and increase performance if lot of values needs to be added at time.
 * <p>Public methods:
 * <ul>
 * <li>batchAdd</li>
 * <li>commitAddition</li>
 * </ul>
 *
 * @param <T>  type of items.
 * @param <ID> type of item identifiers.
 * @author eMeS
 * @version 1.0.
 * @since 2.2.2.
 */
public interface BatchAddOperation<T, ID> {

    /**
     * Prepares item to be added to repository. Actual changes are saved only
     * after {@link BatchAddOperation#commitAddition(boolean)} is called.
     *
     * @param identifier item ID.
     * @param item       item to be added to repository.
     */
    void batchAdd(String identifier, String item);

    /**
     * Applies addition of items in queue by method's {@link BatchAddOperation#batchAdd(String, String)} call.
     * Item (which has be added) queue is cleaned after successful item addition to repository.
     * In case of errors this method can be called repeatedly to retry commit.
     *
     * @param forceReplace flag to perform deletion of items that was stored to repository before
     *                     with same ID. In other words, if <code>true</code>, addition will be performed as
     *                     put operation. If <code>false</code> then items to add will not replace
     *                     existing items in repository.
     * @throws MS_RepositoryDataExchangeException if something repository-specific happens while performing data exchange.
     */
    void commitAddition(boolean forceReplace) throws MS_RepositoryDataExchangeException;
}
