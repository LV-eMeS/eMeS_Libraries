package lv.emes.libraries.tools;

/**
 * Object wrapper that holds wrapped object as instance member and sets all initial values for this object to be usable.
 * <p>Public methods:
 * <ul>
 * <li>wrap</li>
 * <li>getWrappedObject</li>
 * <li>unwrap</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public interface MS_IObjectWrapper<T> {

    /**
     * Creates new instance of <b>T</b> type wrapped object.
     * This method has an opposite effect of {@link MS_IObjectWrapper#unwrap(Object)} method.
     * <p><u>Note</u>: there is no need to call this method, as it should be handled only by
     * {@link MS_ObjectWrapperHelper#getWrappedObject(MS_IObjectWrapper, Object)} inside wrapper itself.
     * @return reference to newly created wrapped object.
     */
    T wrap();

    /**
     * @return already created wrapped object.
     */
    T getWrappedObject();

    /**
     * Fills all other instance member values with the ones from wrapped object.
     * This method has an opposite effect of {@link MS_IObjectWrapper#wrap()} method.
     * As it needs first to create new instance of wrapper <b>MS_IObjectWrapper</b>, that is recommended to use
     * {@link MS_ObjectWrapperHelper#unwrap(Object, Class)} method to get wrapper created and initialized with data from
     * wrapped object.
     * @param object object, from which all data needed for wrapper are taken.
     */
    void unwrap(T object);
}
