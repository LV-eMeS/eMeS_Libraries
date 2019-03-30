package lv.emes.libraries.tools;

import java.util.function.Supplier;

/**
 * Non-thread safe Lazy field initializer.
 * <p><u>Example of use</u>:
 * <code>private Supplier&lt;Object&gt; someField = Lazy.lazily(() -&gt; someField = Lazy.value(expensiveComputation()));</code>
 * <p>Public methods:
 * <ul>
 * <li>get</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
public interface Lazy<T> extends Supplier<T> {

    Supplier<T> init();

    default T get() {
        return init().get();
    }

    static <U> Supplier<U> lazily(Lazy<U> lazy) {
        return lazy;
    }

    static <T> Supplier<T> value(T value) {
        return () -> value;
    }
}
