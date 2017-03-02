package lv.emes.libraries.communication.cryptography;

import org.junit.Assert;
import org.junit.Test;

import java.security.GeneralSecurityException;

public class MSCryptographyUtilsTest {
	private static final String KEY = "test key 4 encryption.~";
	private static final String MAC_KEY = "test MAC key 4 encryption. (^_^)";
	private static final String TEXT = "Default text that is going to be encrypted.";

	@Test
	public void testValidEncryption() throws Exception {
		String encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		String decrcText = MS_CryptographyUtils.decrypt(encText, KEY, MAC_KEY);
		Assert.assertEquals(TEXT, decrcText);
	}

	@Test
	public void testInvalidKey() throws Exception {
		String encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		String decrcText = MS_CryptographyUtils.decrypt(encText, "Invalid", MAC_KEY);
		Assert.assertNotEquals(TEXT, decrcText);
	}

	@Test(expected = GeneralSecurityException.class)
	public void testInvalidMacKey() throws Exception {
		String encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		MS_CryptographyUtils.decrypt(encText, KEY, "Invalid");
	}

	@Test
	public void testIdenticalTextsNotEqualWhenEncrypted() throws Exception {
		String encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		String encText2 = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		Assert.assertNotEquals(encText, encText2);

	}
}
