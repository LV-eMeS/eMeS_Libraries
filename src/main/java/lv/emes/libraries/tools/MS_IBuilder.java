package lv.emes.libraries.tools;

/**
 * In order to use builder pattern following approach is performed:
 * Class should be abstract, implement this builder interface and be generified like this:
 * <code>BuilderClass&lt;T extends BuilderClass&lt;T&gt;&gt;</code>.
 * <br>After that builder methods should call <b>getThis</b> method as return parameter.
 * <p>Method to implement:
 * <ul>
 * <li>getThis</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public interface MS_IBuilder<T> {

    /**
     * Descendants of this class should implement this method by returning reference to
     * same instance of class.
     * @return reference to an object itself.
     */
    T getThis();
}
