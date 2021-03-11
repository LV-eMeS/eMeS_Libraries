package lv.emes.libraries.communication.cryptography.hybrid;

import lv.emes.libraries.communication.cryptography.MS_CryptographyUtils;
import lv.emes.libraries.communication.cryptography.MS_Hash;
import lv.emes.libraries.communication.cryptography.MS_RSA;
import lv.emes.libraries.tools.MS_BadSetupException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.crypto.IllegalBlockSizeException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Objects;

/**
 * Class to support cipher capabilities for most common use cases by combining symmetric, asymmetric cryptography and hashing functionality.
 * Some of operations may not be supported if some of mandatory inputs are missing,
 * for example, {@link #encryptAsymmetric(String)} cannot be done without constructing {@link MS_Cipher}
 * with {@link AsymmetricKeyPair} that does contain public key.
 * <p>Public methods:
 * <ul>
 *     <li>encryptAsymmetric</li>
 *     <li>decryptAsymmetric</li>
 *     <li>sign</li>
 *     <li>verifySignature</li>
 * </ul>
 * <p>Getters and setters:
 * <ul>
 *     <li>getAsymmetricEncryptionKeys</li>
 *     <li>getSignatureKeys</li>
 *     <li>getSymmetricKeyAttributes</li>
 *     <li>getHashingAttributes</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 * @see MS_CipherBuilder
 * @since 2.4.2.
 */
public class MS_Cipher {

    private final static String DEFAULT_HMAC_KEY = "Join all supported cryptographic operations into one class MS_Cipher";
    private static final String ERROR_MESS_WRONG_PRIVATE_KEY_PROVIDED_FOR_ASYMMETRIC_DECRYPTION = "Wrong private key provided for asymmetric decryption of given symmetric key";
    private static final String ERR_MESS_WRONG_ENCRYPTED_TEXT_PROVIDED = "Wrong encrypted text provided";

    private AsymmetricKeyPair asymmetricEncryptionKeys;
    private AsymmetricKeyPair signatureKeys;
    private SymmetricKeyAttributes symmetricKeyAttributes;
    private HashingAttributes hashingAttributes;

    private MS_RSA asymmetricEncrypt;
    private MS_RSA asymmetricDecrypt;
    private MS_RSA signatureSigner;
    private MS_RSA signatureVerifier;
    private MS_Hash hash;

    MS_Cipher() { // Meant to be build only via MS_CipherBuilder
    }

    public String encryptAsymmetric(String str) throws IllegalBlockSizeException {
        if (asymmetricEncrypt == null)
            throw new MS_BadSetupException("In order to perform encryptAsymmetric operation public key for encryption must be set via asymmetricEncryptionKeys");
        return asymmetricEncrypt.encrypt(str);
    }

    public String decryptAsymmetric(String encrypted) throws IllegalBlockSizeException {
        if (asymmetricDecrypt == null)
            throw new MS_BadSetupException("In order to perform decryptAsymmetric operation private key for decryption must be set via asymmetricEncryptionKeys");
        return asymmetricDecrypt.decrypt(encrypted);
    }

    public String sign(String str) throws IllegalBlockSizeException {
        if (signatureSigner == null)
            throw new MS_BadSetupException("In order to perform sign operation private key for signing must be set via signatureKeys");
        return signatureSigner.encrypt(str);
    }

    public String verifySignature(String signature) throws IllegalBlockSizeException {
        if (signatureVerifier == null)
            throw new MS_BadSetupException("In order to perform verifySignature operation public key for verification must be set via signatureKeys");
        return signatureVerifier.decrypt(signature);
    }

    public String encryptSymmetric(String str) throws GeneralSecurityException {
        if (symmetricKeyAttributes == null)
            throw new MS_BadSetupException("In order to perform encryptSymmetric operation symmetricKeyAttributes must be set");
        return MS_CryptographyUtils.encrypt(str, symmetricKeyAttributes.getSecretKey(), symmetricKeyAttributes.getHmacKey());
    }

    public String decryptSymmetric(String encrypted) throws GeneralSecurityException {
        if (symmetricKeyAttributes == null)
            throw new MS_BadSetupException("In order to perform decryptSymmetric operation symmetricKeyAttributes must be set");
        return MS_CryptographyUtils.decrypt(encrypted, symmetricKeyAttributes.getSecretKey(), symmetricKeyAttributes.getHmacKey());
    }

    public String hash(String textToHash) {
        if (hash == null)
            throw new MS_BadSetupException("In order to perform hash operation hashingAttributes must be set");
        return hash.hash(textToHash);
    }

    /**
     * Performs both symmetric and asymmetric encryption of large text that could be sent via network.
     * First given text is encrypted with symmetric key that is generated on the fly, then symmetric key is
     * encrypted with asymmetric public key and both results returned as pair.
     *
     * @param largeText large text to encrypt that would be impossible to encrypt with asymmetric key alone due to
     *                  known limitations of block size that can be encrypted asymmetrically.
     * @return pair with encrypted symmetric key on the left side and encrypted text on the right.
     * @throws MS_BadSetupException if public key is no set or {@link GeneralSecurityException} occurs while
     *                              performing encryption due to underlying failure of {@link MS_CryptographyUtils#encrypt(String, String, String)}.
     */
    public Pair<String, String> encryptHybrid(String largeText) throws MS_BadSetupException {
        if (asymmetricEncrypt == null)
            throw new MS_BadSetupException("In order to perform encryptHybrid operation public key for encryption must be set via asymmetricEncryptionKeys");
        String randomSymmetricKey = RandomStringUtils.randomAlphanumeric(getSymmetricKeyCharCountBasedOnLengthOfAsymmetricKey());

        MS_Cipher symmetricEncrypt = MS_CipherBuilder.aBuilder()
                .withSymmetricKeyAttributes(new SymmetricKeyAttributes().withSecretKey(randomSymmetricKey))
                .build();
        try {
            String encryptedKey = asymmetricEncrypt.encrypt(randomSymmetricKey); // Invalid block size should be never thrown here
            String encryptedPlainText = symmetricEncrypt.encryptSymmetric(largeText);
            return Pair.of(encryptedKey, encryptedPlainText);
        } catch (GeneralSecurityException e) {
            throw new MS_BadSetupException(e);
        }
    }

    /**
     * Performs asymmetric decryption of provided <b>encryptedSymmetricKey</b> and uses decrypted key to symmetrically
     * decrypt large text provided as <b>encryptedText</b>.
     *
     * @param encryptedSymmetricKey symmetric key that was encrypted asymmetrically with public key, which is paired with
     *                              private key set fo this instance.
     * @param encryptedText         text to decrypt.
     * @return decrypted <b>encryptedText</b>.
     * @throws NullPointerException if provided <b>encryptedSymmetricKey</b> or <b>encryptedText</b> is <tt>null</tt>.
     * @throws MS_BadSetupException if private key is not set or given <b>encryptedSymmetricKey</b> is not encrypted
     *                              with public key that is pair for this private key. Also if given input arguments are invalid.
     */
    public String decryptHybrid(String encryptedSymmetricKey, String encryptedText) throws NullPointerException, MS_BadSetupException {
        if (asymmetricDecrypt == null)
            throw new MS_BadSetupException("In order to perform decryptHybrid operation private key for encryption must be set via asymmetricEncryptionKeys");

        String decryptedSymmetricKey;
        try {
            decryptedSymmetricKey = asymmetricDecrypt.decrypt(encryptedSymmetricKey);
        } catch (IllegalBlockSizeException e) {
            throw new MS_BadSetupException(ERROR_MESS_WRONG_PRIVATE_KEY_PROVIDED_FOR_ASYMMETRIC_DECRYPTION, e);
        } catch (MS_BadSetupException e) {
            throw new MS_BadSetupException(ERROR_MESS_WRONG_PRIVATE_KEY_PROVIDED_FOR_ASYMMETRIC_DECRYPTION, e.getCause());
        } catch (IllegalArgumentException e) {
            throw new MS_BadSetupException(ERR_MESS_WRONG_ENCRYPTED_TEXT_PROVIDED, e);
        }

        SymmetricKeyAttributes symmetricKeyAttributes = new SymmetricKeyAttributes().withSecretKey(decryptedSymmetricKey);
        try {
            return MS_CipherBuilder.aBuilder().withSymmetricKeyAttributes(symmetricKeyAttributes).build().decryptSymmetric(encryptedText);
        } catch (GeneralSecurityException e) {
            throw new MS_BadSetupException(e); // Should not happen
        } catch (IllegalArgumentException e) {
            throw new MS_BadSetupException(ERR_MESS_WRONG_ENCRYPTED_TEXT_PROVIDED, e);
        }
    }

    /**
     * Overloaded method for {@link #decryptHybrid(String, String)}
     *
     * @param encryptedData pair of encrypted asymmetric key and encrypted data that needs to be decrypted.
     * @return decrypted data.
     * @throws NullPointerException if provided <b>encryptedData</b> is <tt>null</tt> or one or both pair elements are <tt>null</tt>.
     * @throws MS_BadSetupException if private key is not set or given <b>encryptedSymmetricKey</b> is not encrypted
     *                              with public key that is pair for this private key. Also if given input arguments are invalid.
     */
    public String decryptHybrid(Pair<String, String> encryptedData) throws NullPointerException, MS_BadSetupException {
        return decryptHybrid(Objects.requireNonNull(encryptedData).getKey(), encryptedData.getValue());
    }

    private int getSymmetricKeyCharCountBasedOnLengthOfAsymmetricKey() {
        int keyCharacterCount = asymmetricEncryptionKeys.getPublicKey().length();
        if (keyCharacterCount < 220) return 20; // minimum key size (MS_RSA.KEY_LENGTH_MINIMUM)
        if (keyCharacterCount < 300)
            return keyCharacterCount / 8; // medium key size (MS_RSA.KEY_LENGTH_MINIMUM .. MS_RSA.KEY_LENGTH_MEDIUM)
        if (keyCharacterCount < 700)
            return keyCharacterCount / 7; // medium key size (MS_RSA.KEY_LENGTH_MINIMUM .. MS_RSA.KEY_LENGTH_MEDIUM)
        if (keyCharacterCount < 800)
            return keyCharacterCount / 6; // medium-long key size (MS_RSA.KEY_LENGTH_MEDIUM .. MS_RSA.KEY_LENGTH_LONG)
        return 140; // large key size (> MS_RSA.KEY_LENGTH_MEDIUM)
    }

    // Meant for 1 time call from MS_CipherBuilder
    void init() {
        if (this.asymmetricEncryptionKeys != null) {
            KeyPair asymmetricEncryptionKeys = this.asymmetricEncryptionKeys.getKeys();
            this.asymmetricEncrypt = asymmetricEncryptionKeys.getPublic() == null ? null : MS_RSA.forEncryption(asymmetricEncryptionKeys.getPublic());
            this.asymmetricDecrypt = asymmetricEncryptionKeys.getPrivate() == null ? null : MS_RSA.forDecryption(asymmetricEncryptionKeys.getPrivate());
        }

        if (signatureKeys != null) {
            KeyPair asymmetricEncryptionKeys = signatureKeys.getKeys();
            this.signatureSigner = asymmetricEncryptionKeys.getPrivate() == null ? null : MS_RSA.forEncryption(asymmetricEncryptionKeys.getPrivate());
            this.signatureVerifier = asymmetricEncryptionKeys.getPublic() == null ? null : MS_RSA.forDecryption(asymmetricEncryptionKeys.getPublic());
        }

        if (symmetricKeyAttributes != null && symmetricKeyAttributes.getHmacKey() == null) {
            symmetricKeyAttributes = new SymmetricKeyAttributes()
                    .withSecretKey(symmetricKeyAttributes.getSecretKey()).withHmacKey(DEFAULT_HMAC_KEY);
        }

        if (hashingAttributes != null) {
            Boolean encodeInBase64 = hashingAttributes.encodeInBase64();
            hash = new MS_Hash(hashingAttributes.getSalt(), hashingAttributes.getKeyLength(),
                    hashingAttributes.getIterations(), encodeInBase64 != null && encodeInBase64);
        }
    }

    void setAsymmetricEncryptionKeys(AsymmetricKeyPair asymmetricEncryptionKeys) {
        this.asymmetricEncryptionKeys = asymmetricEncryptionKeys;
    }

    void setSignatureKeys(AsymmetricKeyPair signatureKeys) {
        this.signatureKeys = signatureKeys;
    }

    void setSymmetricKeyAttributes(SymmetricKeyAttributes symmetricKeyAttributes) {
        this.symmetricKeyAttributes = symmetricKeyAttributes;
    }

    void setHashingAttributes(HashingAttributes hashingAttributes) {
        this.hashingAttributes = hashingAttributes;
    }

    public AsymmetricKeyPair getAsymmetricEncryptionKeys() {
        return asymmetricEncryptionKeys;
    }

    public AsymmetricKeyPair getSignatureKeys() {
        return signatureKeys;
    }

    public SymmetricKeyAttributes getSymmetricKeyAttributes() {
        return symmetricKeyAttributes;
    }

    public HashingAttributes getHashingAttributes() {
        return hashingAttributes;
    }
}
