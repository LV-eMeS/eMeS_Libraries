package lv.emes.libraries.patches.android_compat;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * An utility class containing Java 8 standard util methods
 * that are not supported on older Android versions we need to support currently.
 * <p>Static methods:
 * <ul>
 * <li>computeIfAbsent</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.2.3.
 */
public final class JavaUtilCompatibility {

    private JavaUtilCompatibility() {
    }

    public static <K,V>  V computeIfAbsent(Map<K, V> map, K key,
                                           Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = map.get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                map.put(key, newValue);
                return newValue;
            }
        }

        return v;
    }
}
