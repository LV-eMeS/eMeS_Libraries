package lv.emes.libraries.communication.cryptography;

import lv.emes.libraries.utilities.MS_CodingUtils;
import lv.emes.libraries.utilities.MS_StringUtils;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Purpose of this module is easy hash (SHA1) generation from input text.
 * It's possible to use either default salt, either pass your own salt string, plus you can vary with length of key in range of [256..2048].
 * <p>Methods:
 * <ul>
 * <li>getHash</li>
 * </ul>
 *
 * @author eMeS
 * @version 4.0.
 */
public class MS_Hash {

    /**
     * Minimum key length that will produce 44 characters long hash code from any non blank text.
     * Hashing will be done in less than 0,6 seconds.
     */
    public static final int KEY_LENGTH_MINIMUM = 256;
    /**
     * Medium key length that will produce 88 characters long hash code from any non blank text.
     * Hashing will be done in less than 1 second.
     */
    public static final int KEY_LENGTH_MEDIUM = 512;
    /**
     * Long key length that will produce 172 characters long hash code from any non blank text.
     * Hashing will be done in less than 1,3 seconds.
     */
    public static final int KEY_LENGTH_LONG = 1024;
    /**
     * Maximum possible key length that will produce 344 characters long hash code from any non blank text.
     * Hashing will be done in less than 1,5 seconds.
     */
    public static final int KEY_LENGTH_MAXIMUM = 2048;

    // The higher the number of iterations the more 
    // expensive computing the hash is for user and also for an attacker.
    private static final int _ITERATION_MULTIPLIER = 10;
    private static final int _DEFAULT_KEY_LEN = KEY_LENGTH_MINIMUM;
    private static final byte[] _DEFAULT_SALT = {
            1, -115, 85, 122, -18, -49, -94, 25, -36, -128, -43, -83, 122, -21, 38, 47, 2, 124, 70, -54, 111, 67, 106, 39, -28, 51, -32, -112, 50, -28, 97, -37
    };

    private final byte[] salt;
    private final int keyLength;
    private final int iterations;
    private final boolean encodeInBase64;

    /**
     * Constructs new instance that can perform hashing with pre-configured parameters.
     *
     * @param salt           nullable salt for hash modification. If <tt>null</tt> then default salt value will be used.
     * @param keyLength      nullable length of hash key. Min = {@link #KEY_LENGTH_MINIMUM}, Max = {@link #KEY_LENGTH_MAXIMUM}.
     *                       For different lengths resulting hash will differ.
     *                       If <tt>null</tt> then {@link #KEY_LENGTH_MINIMUM} value will be used.
     * @param iterations     nullable count of iterations to perform when hashing.
     *                       It's recommended to have it greater than <b>1000</b>.
     *                       If <tt>null</tt> then <tt>10 times key length</tt> iterations will be performed.
     * @param encodeInBase64 whether calculated hash needs to be returned in Base64 format.
     */
    public MS_Hash(String salt, Integer keyLength, Integer iterations, boolean encodeInBase64) {
        this.salt = salt == null ? _DEFAULT_SALT : salt.getBytes();
        this.keyLength = keyLength == null ? _DEFAULT_KEY_LEN : keyLength;
        this.iterations = iterations == null || iterations <= 0 ? this.keyLength * _ITERATION_MULTIPLIER : iterations;
        this.encodeInBase64 = encodeInBase64;
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm.
     *
     * @param aTextToHash a text which will be used to perform hashing.
     * @return empty string in case if <b>aTextToHash</b> is empty.
     */
    public String hash(String aTextToHash) {
        return hash(aTextToHash, salt, keyLength, encodeInBase64, iterations);
    }

    // using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
    // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
    private static String hash(String aTextToHash, byte[] aSalt, int aKeyLength, boolean encodeInBase64, int iterations) {
        if (aTextToHash == null || aTextToHash.length() == 0) return "";
        SecretKeyFactory f;
        SecretKey key;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        } catch (Exception e) {
            return "";
        }

        try {
            key = f.generateSecret(new PBEKeySpec(aTextToHash.toCharArray(), aSalt, iterations, aKeyLength));
        } catch (Exception e) {
            return "";
        }

        return encodeInBase64 ? Base64.encodeBase64String(key.getEncoded()) : MS_StringUtils.bytesToHex(key.getEncoded());
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm for <tt>10 times key length</tt> iterations
     * and encoded in Base64 using default salt value and default key length.
     *
     * @param aTextToHash a text which will be used to perform hashing.
     * @return empty string in case if <b>aTextToHash</b> is empty.
     */
    public static String getHash(String aTextToHash) {
        // store the salt with the password
        return hash(aTextToHash, _DEFAULT_SALT, _DEFAULT_KEY_LEN, true, _ITERATION_MULTIPLIER * _DEFAULT_KEY_LEN);
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm for <tt>10 times key length</tt> iterations
     * and encoded in Base64 using default salt value and given length of key.
     *
     * @param aTextToHash a text which will be used to perform hashing.
     * @param aKeyLength  length of hash key. Min = 256, Max = 2048. For different lengths resulting hash will differ.
     * @return empty string in case if <b>aTextToHash</b> is empty or if <b>aKeyLength</b> is not in interval [256..2048].
     */
    public static String getHash(String aTextToHash, int aKeyLength) {
        if (!MS_CodingUtils.inRange(aKeyLength, KEY_LENGTH_MINIMUM, KEY_LENGTH_MAXIMUM)) return "";
        // store the salt with the password
        return hash(aTextToHash, _DEFAULT_SALT, aKeyLength, true, _ITERATION_MULTIPLIER * aKeyLength);
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm for <tt>10 times key length</tt> iterations
     * and encoded in Base64 using passed salt value and default length of key.
     *
     * @param aTextToHash a text which will be used to perform hashing.
     * @param aSalt       arbitrary text for hash modification.
     * @return empty string in case if <b>aTextToHash</b> is empty or if <b>aSalt</b> is empty.
     */
    public static String getHash(String aTextToHash, String aSalt) {
        byte[] salt = aSalt.getBytes();
        return hash(aTextToHash, salt, _DEFAULT_KEY_LEN, true, _ITERATION_MULTIPLIER * _DEFAULT_KEY_LEN);
    }

    /**
     * From passed text generates text hashed with SHA1 algorithm for <tt>10 times key length</tt> iterations
     * and encoded in Base64 using passed salt value and passed length of key.
     *
     * @param aTextToHash a text which will be used to perform hashing.
     * @param aSalt       arbitrary text for hash modification.
     * @param aKeyLength  length of hash key. Min = 256, Max = 2048. For different lengths resulting hash will differ.
     * @return empty string in case if <b>aTextToHash</b> is empty or if <b>aSalt</b> is empty, or if <b>aKeyLength</b> is not in interval [256..2048].
     */
    public static String getHash(String aTextToHash, String aSalt, int aKeyLength) {
        if (!MS_CodingUtils.inRange(aKeyLength, KEY_LENGTH_MINIMUM, KEY_LENGTH_MAXIMUM)) return "";
        byte[] salt = aSalt.getBytes();
        return hash(aTextToHash, salt, aKeyLength, true, _ITERATION_MULTIPLIER * aKeyLength);
    }
}
