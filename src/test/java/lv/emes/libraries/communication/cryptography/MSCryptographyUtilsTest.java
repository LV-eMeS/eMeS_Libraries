package lv.emes.libraries.communication.cryptography;

import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MSCryptographyUtilsTest {

	private static final String KEY = "test key 4 encryption.~";
	private static final String MAC_KEY = "test MAC key 4 encryption. (^_^)";
	private static final String TEXT = "Default text that is going to be encrypted.";

	private String encText;
	private String encText2;
	private String decrcText;

	@Test
	public void testValidEncryption() throws Exception {
		encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, KEY, MAC_KEY);
		assertEquals(TEXT, decrcText);

		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, KEY);
		assertEquals(TEXT, decrcText);
	}

	@Test
	public void testInvalidKey() throws Exception {
		encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, "Invalid", MAC_KEY);
		assertNotEquals(TEXT, decrcText);

		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, "Invalid");
		assertNotEquals(TEXT, decrcText);
	}

	@Test(expected = GeneralSecurityException.class)
	public void testInvalidMacKey() throws Exception {
		encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		MS_CryptographyUtils.decrypt(encText, KEY, "Invalid");
	}

	@Test
	public void testIdenticalTextsNotEqualWhenEncrypted() throws Exception {
		encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		encText2 = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		assertNotEquals(encText, encText2);
		//just to check, if encrypted text can be decrypted back
		assertEquals(TEXT, MS_CryptographyUtils.decrypt(encText, KEY, MAC_KEY));
		assertEquals(TEXT, MS_CryptographyUtils.decrypt(encText2, KEY, MAC_KEY));


		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		encText2 = MS_CryptographyUtils.encrypt(TEXT, KEY);
		assertNotEquals(encText, encText2);
		assertEquals(TEXT, MS_CryptographyUtils.decrypt(encText, KEY));
		assertEquals(TEXT, MS_CryptographyUtils.decrypt(encText2, KEY));
	}
}
