package lv.emes.libraries.communication.cryptography.hybrid;

/**
 * Attributes required to perform hashing operations.
 * <p>Properties:
 * <ul>
 *     <li>salt</li>
 *     <li>keyLength</li>
 *     <li>iterations</li>
 *     <li>encodeInBase64</li>
 * </ul>
 * <p>Optional properties:
 * <ul>
 *     <li>salt</li>
 *     <li>keyLength</li>
 *     <li>iterations</li>
 *     <li>encodeInBase64</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.2.
 */
public class HashingAttributes {

    private String salt;
    private Integer keyLength;
    private Integer iterations;
    private Boolean encodeInBase64;

    public HashingAttributes withSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public HashingAttributes withKeyLength(Integer keyLength) {
        this.keyLength = keyLength;
        return this;
    }

    public HashingAttributes withIterations(Integer iterations) {
        this.iterations = iterations;
        return this;
    }

    public HashingAttributes withEncodeInBase64(Boolean encodeInBase64) {
        this.encodeInBase64 = encodeInBase64;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public Integer getKeyLength() {
        return keyLength;
    }

    public Integer getIterations() {
        return iterations;
    }

    public Boolean encodeInBase64() {
        return encodeInBase64;
    }
}
