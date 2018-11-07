package lv.emes.libraries.tools;

/**
 * Function to retrieve some object in pre-defined way.
 *
 * @param <T> type of object needed to retrieve.
 * @author eMeS
 * @since 2.1.10
 */
@FunctionalInterface
public interface IFuncHandledSupplier<T> {

    T get() throws Exception;
}
