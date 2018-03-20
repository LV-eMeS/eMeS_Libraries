package lv.emes.libraries.tools;

import java.lang.reflect.InvocationTargetException;

/**
 * Helper methods to be used in {@link MS_ObjectWrapper} in order to create wrapped object or wrapper itself.
 * <p>Static methods:
 * <ul>
 * <li>getWrappedObject</li>
 * <li>wrap</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_ObjectWrapperHelper {

    private MS_ObjectWrapperHelper() {
    }

    /**
     * Helper method to always retrieve already wrapped object.
     * This method should be used only in class, which implements {@link MS_ObjectWrapper}.
     *
     * @param wrapper              a wrapper, who calls this method in {@link MS_ObjectWrapper#getWrappedObject()} like:
     *                             <code>return MS_ObjectWrapperHelper.getWrappedObject(this, wrappedObject);</code>
     * @param wrappedObjectCurrent current value of wrapped object. This will be either null, if {@link MS_ObjectWrapper#unwrap()}
     *                             method isn't called yet, either reference to already wrapped object.
     * @param <T>                  type of object to get.
     * @return wrapped object that is bind to <b>wrapper</b>.
     */
    public static <T> T getWrappedObject(MS_ObjectWrapper<T> wrapper, T wrappedObjectCurrent) {
        T res = wrappedObjectCurrent;
        if (wrappedObjectCurrent == null) res = wrapper.unwrap();
        return res;
    }

    /**
     * Creates new wrapper and fills it's instance member values with ones taken from <b>object</b>, which will be
     * wrapped into wrapper altogether with rest of data.
     * <p><u>Warning</u>: in order to create new instance of wrapper, wrapper must have a non-argument constructor.
     *
     * @param object       object to wrap into wrapper.
     * @param wrapperClass class of wrapper.
     * @param <T>          type of object to wrap.
     * @param <W>          type of wrapper itself.
     * @return new instance of wrapper filled with wrapping object and all needed data.
     * @throws MS_BadSetupException if contract about non-argument constructor is violated.
     */
    public static <T, W extends MS_ObjectWrapper<T>> W wrap(T object, Class<W> wrapperClass) throws MS_BadSetupException {
        W res;
        try {
            res = wrapperClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MS_BadSetupException(e);
        }
        res.wrap(object);
        return res;
    }
}
