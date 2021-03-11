package lv.emes.libraries.communication.cryptography.hybrid;

import lv.emes.libraries.communication.cryptography.MS_RSA;
import lv.emes.libraries.communication.json.MS_JSONObject;
import lv.emes.libraries.tools.MS_BadSetupException;

import java.security.KeyPair;

/**
 * Key pair for asymmetric cryptography. Holds keys in both string representation and in {@link KeyPair} object.
 * <p>Properties:
 * <ul>
 *     <li>privateKey</li>
 *     <li>publicKey</li>
 *     <li>keys</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.2.
 */
public class AsymmetricKeyPair {

    private String privateKey;
    private String publicKey;
    private KeyPair keys;

    private AsymmetricKeyPair() {
    }

    public static AsymmetricKeyPair fromConstantStrings(String privateKey, String publicKey) {
        if (privateKey == null && publicKey == null)
            throw new MS_BadSetupException("At least one of private or public keys must be provided in order to work with MS_Cipher");

        AsymmetricKeyPair res = new AsymmetricKeyPair();
        res.privateKey = privateKey;
        res.publicKey = publicKey;
        res.keys = new KeyPair(MS_RSA.convertPublicKey(publicKey), MS_RSA.convertPrivateKey(privateKey));
        return res;
    }

    public static AsymmetricKeyPair fromKeyPair(KeyPair keys) {
        if (keys.getPrivate() == null && keys.getPublic() == null)
            throw new MS_BadSetupException("At least one of private or public keys must be provided in order to work with MS_Cipher");

        AsymmetricKeyPair res = new AsymmetricKeyPair();
        res.keys = keys;
        res.privateKey = keys.getPrivate() == null ? null : MS_RSA.keyToBase64String(keys.getPrivate());
        res.publicKey = keys.getPublic() == null ? null : MS_RSA.keyToBase64String(keys.getPublic());
        return res;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public KeyPair getKeys() {
        return keys;
    }

    @Override
    public String toString() {
        return new MS_JSONObject().put("privateKey", privateKey).put("publicKey", publicKey).toString();
    }
}
