package lv.emes.libraries.communication.cryptography;

import lv.emes.libraries.tools.MS_BadSetupException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA implementation with ECB mode and PKCS1 padding that is able to work with keys previously stored as Base64 strings
 * or freshly generated keys.
 * <p>Public methods:
 * <ul>
 *     <li>encrypt</li>
 *     <li>decrypt</li>
 * </ul>
 * <p>Static methods:
 * <ul>
 *     <li>forEncryption</li>
 *     <li>forDecryption</li>
 *     <li>generateKeyPair</li>
 *     <li>keyToBase64String</li>
 *     <li>convertPublicKey</li>
 *     <li>convertPrivateKey</li>
 * </ul>
 * Encryption and decryption methods might throw an {@link IllegalBlockSizeException} in case of operations with large
 * amounts of data. Recommended approach to securely exchange large data is:
 * <ol>
 *     <li>Generate a symmetric key;</li>
 *     <li>Encrypt the data with the symmetric key;</li>
 *     <li>Encrypt the symmetric key with RSA;</li>
 *     <li>Send the encrypted key and the encrypted data;</li>
 *     <li>Decrypt the encrypted symmetric key with RSA;</li>
 *     <li>Decrypt the data with the symmetric key.</li>
 * </ol>
 *
 * @author eMeS
 * @version 1.0.
 * @since 2.4.1.
 */
public class MS_RSA {

    public static final int KEY_LENGTH_MINIMUM = 1024;
    public static final int KEY_LENGTH_MEDIUM = 2048;
    public static final int KEY_LENGTH_LONG = KEY_LENGTH_MEDIUM * 2;

    private final Cipher cipher;
    private final int encryptMode;

    private MS_RSA(Key key, int encryptMode) {
        this.encryptMode = encryptMode;
        try {
            this.cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(encryptMode, key);
        } catch (GeneralSecurityException e) {
            throw new MS_BadSetupException(e);
        }
    }

    private byte[] perform(byte[] bytes) throws IllegalBlockSizeException {
        try {
            return cipher.doFinal(bytes);
        } catch (BadPaddingException e) {
            throw new MS_BadSetupException(e);
        }
    }

    private void assertSupportedOperationType(int encryptMode) {
        if (this.encryptMode != encryptMode) {
            String modeName = encryptMode == Cipher.ENCRYPT_MODE ? "encryption" : "decryption";
            throw new UnsupportedOperationException("This RSA instance does not support " + modeName);
        }
    }

    public byte[] encrypt(byte[] bytes) throws IllegalBlockSizeException {
        assertSupportedOperationType(Cipher.ENCRYPT_MODE);
        return perform(bytes);
    }

    public byte[] decrypt(byte[] bytes) throws IllegalBlockSizeException {
        assertSupportedOperationType(Cipher.DECRYPT_MODE);
        return perform(bytes);
    }

    public String encrypt(String plainText) throws IllegalBlockSizeException {
        assertSupportedOperationType(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = perform(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String base64EncryptedText) throws IllegalBlockSizeException {
        assertSupportedOperationType(Cipher.DECRYPT_MODE);
        byte[] decryptedBytes = perform(Base64.getDecoder().decode(base64EncryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static MS_RSA forEncryption(Key key) {
        return new MS_RSA(key, Cipher.ENCRYPT_MODE);
    }

    public static MS_RSA forDecryption(Key key) {
        return new MS_RSA(key, Cipher.DECRYPT_MODE);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IllegalBlockSizeException {
        KeyPair keyPair = generateKeyPair(KEY_LENGTH_MINIMUM);
        String str = "This is a secret message: Mācēt enkōdt ŗīōķņģ";
        System.out.println("Original clear message: " + str);

        MS_RSA encryption = MS_RSA.forEncryption(keyPair.getPublic());
        String encrypted = encryption.encrypt(str);
        System.out.println("Encrypted message: " + encrypted);

        MS_RSA decryption = MS_RSA.forDecryption(keyPair.getPrivate());
        String decrypted = decryption.decrypt(encrypted);
        System.out.println("Decrypted message: " + decrypted);
    }

    public static KeyPair generateKeyPair(int keySize) {
        if (keySize < KEY_LENGTH_MINIMUM)
            throw new IllegalArgumentException("Minimum key size is " + KEY_LENGTH_MINIMUM);
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keySize);
            return kpg.genKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new MS_BadSetupException(e);
        }
    }

    public static String keyToBase64String(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static PublicKey convertPublicKey(String base64PublicKey) {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MS_BadSetupException(e);
        }
    }

    public static PrivateKey convertPrivateKey(String base64PrivateKey) {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        PrivateKey privateKey;
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new MS_BadSetupException(e);
        }
        return privateKey;
    }
}
