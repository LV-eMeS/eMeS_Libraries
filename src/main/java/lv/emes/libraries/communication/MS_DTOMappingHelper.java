package lv.emes.libraries.communication;

import lv.emes.libraries.patches.android_compat.JavaUtilCompatibility;
import lv.emes.libraries.tools.MS_BadSetupException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Helper methods to be used with {@link MS_DTOMappingAlgorithm} in order to serialize or deserialize Data Transfer Objects.
 * <p>Static methods:
 * <ul>
 * <li>serialize</li>
 * <li>deserialize</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.1.
 * @since 2.2.4
 */
public final class MS_DTOMappingHelper {

    private MS_DTOMappingHelper() {
    }

    private static Map<Class, MS_DTOMappingAlgorithm> algorithmCache = new HashMap<>();

    public static <T, V> V serialize(T objectToSerialize, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (objectToSerialize == null) return null;
        else return getAlgorithmFromCache(algorithmClass).serialize(objectToSerialize);
    }

    public static <T, V> T deserialize(V serializedObject, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (serializedObject == null) return null;
        else return getAlgorithmFromCache(algorithmClass).deserialize(serializedObject);
    }

    public static <T, V> List<V> serializeList(List<T> objectsToSerialize, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (objectsToSerialize == null) return null;
        MS_DTOMappingAlgorithm<T, V> algorithm = getAlgorithmFromCache(algorithmClass);
        List<V> res = new ArrayList<>();
        objectsToSerialize.forEach(objectToSerialize -> res.add(objectToSerialize == null ? null : algorithm.serialize(objectToSerialize)));
        return res;
    }

    public static <T, V> List<T> deserializeList(List<V> serializedObjects, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (serializedObjects == null) return null;
        MS_DTOMappingAlgorithm<T, V> algorithm = getAlgorithmFromCache(algorithmClass);
        List<T> res = new ArrayList<>();
        serializedObjects.forEach(serializedObject -> res.add(serializedObject == null ? null : algorithm.deserialize(serializedObject)));
        return res;
    }

    public static <T, V, K> Map<K, V> serializeMap(Map<K, T> objectsToSerialize, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (objectsToSerialize == null) return null;
        MS_DTOMappingAlgorithm<T, V> algorithm = getAlgorithmFromCache(algorithmClass);
        Map<K, V> res = new HashMap<>();
        objectsToSerialize.forEach((key, objectToSerialize) -> res.put(key, objectToSerialize == null ? null : algorithm.serialize(objectToSerialize)));
        return res;
    }

    public static <T, V, K> Map<K, T> deserializeMap(Map<K, V> serializedObjects, Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        if (serializedObjects == null) return null;
        MS_DTOMappingAlgorithm<T, V> algorithm = getAlgorithmFromCache(algorithmClass);
        Map<K, T> res = new HashMap<>();
        serializedObjects.forEach((key, serializedObject) -> res.put(key, serializedObject == null ? null : algorithm.deserialize(serializedObject)));
        return res;
    }

    @SuppressWarnings("unchecked")
    private static <T, V> MS_DTOMappingAlgorithm<T, V> getAlgorithmFromCache(Class<? extends MS_DTOMappingAlgorithm<T, V>> algorithmClass) {
        Objects.requireNonNull(algorithmClass);
        return JavaUtilCompatibility.computeIfAbsent(algorithmCache, algorithmClass, aClass -> {
            try {
                return (MS_DTOMappingAlgorithm<T, V>) aClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new MS_BadSetupException(e);
            }
        });
    }
}
