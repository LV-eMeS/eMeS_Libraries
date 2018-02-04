package lv.emes.libraries.storage;

/**
 * Function to retrieve some object by its ID from some kind of repository.
 *
 * @param <T>  type of object needed to retrieve.
 * @param <ID> type of object's identifier.
 * @author eMeS
 * @version 1.0.
 */
@FunctionalInterface
public interface IFuncObjectRetrievalOperation<ID, T> {

    T get(ID id);
}
