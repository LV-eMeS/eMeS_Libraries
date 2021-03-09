package lv.emes.libraries.communication.cryptography;

import org.junit.Test;

import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_CryptographyUtilsTest {

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
		assertThat(decrcText).isEqualTo(TEXT);

		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, KEY);
		assertThat(decrcText).isEqualTo(TEXT);
	}

	@Test
	public void testInvalidKey() throws Exception {
		encText = MS_CryptographyUtils.encrypt(TEXT, KEY, MAC_KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, "Invalid", MAC_KEY);
		assertThat(decrcText).isNotEqualTo(TEXT);

		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		decrcText = MS_CryptographyUtils.decrypt(encText, "Invalid");
		assertThat(decrcText).isNotEqualTo(TEXT);
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
		assertThat(encText2).isNotEqualTo(encText);
		//just to check, if encrypted text can be decrypted back
		assertThat(MS_CryptographyUtils.decrypt(encText, KEY, MAC_KEY)).isEqualTo(TEXT);
		assertThat(MS_CryptographyUtils.decrypt(encText2, KEY, MAC_KEY)).isEqualTo(TEXT);


		encText = MS_CryptographyUtils.encrypt(TEXT, KEY);
		encText2 = MS_CryptographyUtils.encrypt(TEXT, KEY);
		assertThat(encText2).isNotEqualTo(encText);
		assertThat(MS_CryptographyUtils.decrypt(encText, KEY)).isEqualTo(TEXT);
		assertThat(MS_CryptographyUtils.decrypt(encText2, KEY)).isEqualTo(TEXT);
	}
}
