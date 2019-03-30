package lv.emes.libraries.tools;

/**
 * Enumeration as property concept.
 * <p>Public methods:
 * <ul>
 * <li>getProperty</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @see MS_PropertyInitializerImpl
 * @since 2.2.3.
 */
public interface MS_PropertyInitializer {

    /**
     * Retrieves the String property named the same as enumeration name.
     *
     * @return specific property as String.
     */
    String getProperty();

    /**
     * Retrieves the property of primitive type supported by
     * {@link lv.emes.libraries.utilities.MS_StringUtils#stringToPrimitive(String, Class)} for conversion
     * named the same as enumeration name.
     *
     * @param valueClass desired class of given <b>parameter</b> to which it will be converted from String.
     * @param <T>        type of parameter value. Currently only Boolean, Integer, Long and String are supported.
     * @return specific property as <b>T</b>.
     */
    <T> T getProperty(Class<T> valueClass);
}
