package lv.emes.libraries.communication;

/**
 * Class that holds algorithms, how to serialize or deserialize particular object.
 * Typically must be extended without creating any additional constructors to default one.
 * Then necessary methods must be implemented to determine, how serialization and deserialization needs to be performed.
 * <p>Public methods:
 * <ul>
 * <li>serialize</li>
 * <li>deserialize</li>
 * </ul>
 *
 * @param <T> object type that can be serialized.
 * @param <V> type of object after serialization (mostly String or some kind of JSON object).
 * @author eMeS
 * @version 1.0.
 * @since 2.2.4
 */
public abstract class MS_DTOMappingAlgorithm<T, V> {

    protected MS_DTOMappingAlgorithm() {
        // must-have no-arg constructor for MS_DTOMappingHelper needs
    }

    /**
     * Performs object serialization.
     *
     * @param objectToSerialize non-null object that should be serialized.
     * @return serialized object.
     */
    public abstract V serialize(T objectToSerialize);

    /**
     * Performs object deserialization.
     *
     * @param serializedObject already serialized non-null object that needs to be deserialized.
     * @return deserialized object.
     */
    public abstract T deserialize(V serializedObject);
}
