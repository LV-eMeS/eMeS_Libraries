package lv.emes.libraries.communication.cryptography.hybrid;

/**
 * Key attributes such as key and HMAC key for symmetric cryptography.
 * <p>Properties:
 * <ul>
 *     <li>secretKey</li>
 *     <li>hmacKey</li>
 * </ul>
 * <p>Optional properties:
 * <ul>
 *     <li>hmacKey</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.2.
 */
public class SymmetricKeyAttributes {

    private String secretKey;
    private String hmacKey;

    public SymmetricKeyAttributes withSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public SymmetricKeyAttributes withHmacKey(String hmacKey) {
        this.hmacKey = hmacKey;
        return this;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getHmacKey() {
        return hmacKey;
    }
}
