package lv.emes.libraries.tools;

/**
 * Object wrapper that holds wrapped object as instance member and sets all initial values for this object to be usable.
 * <p>Public methods:
 * <ul>
 * <li>wrap</li>
 * <li>unwrap</li>
 * <li>getWrappedObject</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public interface MS_IObjectWrapper<T> {

    /**
     * Fills all other instance member values with the ones from wrapped object and saves reference to wrapped object.
     * This method has an opposite effect of {@link MS_IObjectWrapper#unwrap()} method.
     * <p><u>For callers</u>: as it needs first to create new instance of wrapper <b>MS_IObjectWrapper</b>, that is recommended to use
     * {@link MS_ObjectWrapperHelper#wrap(Object, Class)} method to get wrapper created and initialized with data from
     * wrapped object instead of creating new instance of <b>MS_IObjectWrapper</b> and manually calling this method.
     * @param object object, which all data (needed for wrapper) are extracted from.
     */
    void wrap(T object);

    /**
     * Creates new instance of <b>T</b> type wrapped object.
     * This method has an opposite effect of {@link MS_IObjectWrapper#wrap(Object)} method.
     * <p><u>Note</u>: there is no need to call this method, as it should be handled only by
     * {@link MS_ObjectWrapperHelper#getWrappedObject(MS_IObjectWrapper, Object)} inside wrapper itself.
     * @return reference to newly created wrapped object.
     */
    T unwrap();

    /**
     * Should be implemented like: <code>return MS_ObjectWrapperHelper.getWrappedObject(this, wrappedObject);</code>
     * @return already created wrapped object.
     */
    T getWrappedObject();
}
