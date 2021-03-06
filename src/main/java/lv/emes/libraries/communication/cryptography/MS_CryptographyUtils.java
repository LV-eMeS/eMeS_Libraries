package lv.emes.libraries.communication.cryptography;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

/**
 * Class for symmetric encryption/decryption processes.
 * http://netnix.org/2015/04/19/aes-encryption-with-hmac-integrity-in-java/
 *
 * @author eMeS
 * @version 1.1.
 */
public class MS_CryptographyUtils {

    private final static Integer keyLength = 128;
    private final static Integer hmacKeyLength = 160;
    private final static Integer iterations = 9876;
    private final static String DEFAULT_HMAC_KEY = "Netnix.org/2015/04/1";

    /**
     * Encrypts provided text <b>text</b> using AES algorithm and encryption key <b>secretKey</b>
     * and {@link MS_CryptographyUtils#DEFAULT_HMAC_KEY}.
     *
     * @param text      text to encrypt.
     * @param secretKey secret key to encrypt <b>text</b>.
     * @return encrypted text.
     * @throws GeneralSecurityException when any problem occurs during encryption.
     */
    public static String encrypt(String text, String secretKey) throws GeneralSecurityException {
        return encrypt(text, secretKey, DEFAULT_HMAC_KEY);
    }

    /**
     * Decrypts provided encrypted text <b>encryptedText</b> using AES algorithm and encryption key <b>secretKey</b>
     * and {@link MS_CryptographyUtils#DEFAULT_HMAC_KEY}.
     *
     * @param encryptedText encrypted text.
     * @param secretKey     secret key.
     * @return decrypted text.
     * @throws GeneralSecurityException when HMAC secret key is incorrect.
     */
    public static String decrypt(String encryptedText, String secretKey) throws GeneralSecurityException {
        return decrypt(encryptedText, secretKey, DEFAULT_HMAC_KEY);
    }

    /**
     * Method encrypts provided text with such secret key and HMAC secret key.
     *
     * @param text      text to be encrypted.
     * @param secretKey secret key.
     * @param hmacKey   HMAC secret key.
     * @return encrypted text.
     * @throws GeneralSecurityException when any problem occurs during encryption.
     */
    public static String encrypt(String text, String secretKey, String hmacKey) throws GeneralSecurityException {
        SecureRandom r = SecureRandom.getInstance("SHA1PRNG");

        // Generate 160 bit Salt for Encryption Key
        byte[] esalt = new byte[20];
        r.nextBytes(esalt);
        // Generate 128 bit Encryption Key
        byte[] dek = deriveKey(secretKey, esalt, iterations, keyLength);

        // Perform Encryption
        SecretKeySpec eks = new SecretKeySpec(dek, "AES");
        Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
        c.init(Cipher.ENCRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
        byte[] es = c.doFinal(text.getBytes(StandardCharsets.UTF_8));

        // Generate 160 bit Salt for HMAC Key
        byte[] hsalt = new byte[20];
        r.nextBytes(hsalt);
        // Generate 160 bit HMAC Key
        byte[] dhk = deriveKey(hmacKey, hsalt, iterations, hmacKeyLength);

        // Perform HMAC using SHA-256
        SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
        Mac m = Mac.getInstance("HmacSHA256");
        m.init(hks);
        byte[] hmac = m.doFinal(es);

        // Construct Output as "ESALT + HSALT + CIPHERTEXT + HMAC"
        byte[] os = new byte[40 + es.length + 32];
        System.arraycopy(esalt, 0, os, 0, 20);
        System.arraycopy(hsalt, 0, os, 20, 20);
        System.arraycopy(es, 0, os, 40, es.length);
        System.arraycopy(hmac, 0, os, 40 + es.length, 32);

        // Return a Base64 Encoded String
        return Base64.getEncoder().encodeToString(os);
    }

    /**
     * Method decrypts provided encrypted text with such secret key and HMAC secret key.
     *
     * @param encryptedText encrypted text.
     * @param secretKey     secret key.
     * @param hmacKey       HMAC secret key.
     * @return decrypted text.
     * @throws GeneralSecurityException when HMAC secret key is incorrect.
     */
    public static String decrypt(String encryptedText, String secretKey, String hmacKey) throws GeneralSecurityException {
        // Recover our Byte Array by Base64 Decoding
        byte[] os = Base64.getDecoder().decode(encryptedText);

        // Check Minimum Length (ESALT (20) + HSALT (20) + HMAC (32))
        if (os.length > 72) {
            // Recover Elements from String
            byte[] esalt = Arrays.copyOfRange(os, 0, 20);
            byte[] hsalt = Arrays.copyOfRange(os, 20, 40);
            byte[] es = Arrays.copyOfRange(os, 40, os.length - 32);
            byte[] hmac = Arrays.copyOfRange(os, os.length - 32, os.length);

            // Regenerate HMAC key using Recovered Salt (hsalt)
            byte[] dhk = deriveKey(hmacKey, hsalt, iterations, hmacKeyLength);

            // Perform HMAC using SHA-256
            SecretKeySpec hks = new SecretKeySpec(dhk, "HmacSHA256");
            Mac m = Mac.getInstance("HmacSHA256");
            m.init(hks);
            byte[] chmac = m.doFinal(es);

            // Compare Computed HMAC vs Recovered HMAC
            if (MessageDigest.isEqual(hmac, chmac)) {
                // HMAC Verification Passed
                // Regenerate Encryption Key using Recovered Salt (esalt)
                byte[] dek = deriveKey(secretKey, esalt, iterations, keyLength);

                // Perform Decryption
                SecretKeySpec eks = new SecretKeySpec(dek, "AES");
                Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
                c.init(Cipher.DECRYPT_MODE, eks, new IvParameterSpec(new byte[16]));
                byte[] s = c.doFinal(es);

                // Return our Decrypted String
                return new String(s, StandardCharsets.UTF_8);
            }
        }
        throw new GeneralSecurityException("Provided text (" + Arrays.toString(os) + ") for decryption has wrong length");
    }

    /**
     * Method takes a password (p), a salt (s) an iteration count (i) and a key output length (l) in bits.
     * It then uses PBKDF2 using an SHA-1 HMAC to generate a key
     *
     * @param p password
     * @param s salt
     * @param i iteration count
     * @param l key output length
     * @return encoded encSecretKey
     */
    private static byte[] deriveKey(String p, byte[] s, int i, int l) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec ks = new PBEKeySpec(p.toCharArray(), s, i, l);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return skf.generateSecret(ks).getEncoded();
    }
}
