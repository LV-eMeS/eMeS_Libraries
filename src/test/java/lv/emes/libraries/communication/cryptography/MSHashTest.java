package lv.emes.libraries.communication.cryptography;

import org.junit.Test;

import static org.junit.Assert.*;

public class MSHashTest {

	@Test
	public void test() {
		final String TEXT = "https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html";
		String str0 = MS_Hash.getHash("", MS_Hash.KEY_LENGTH_MINIMUM);
		String str1 = MS_Hash.getHash(TEXT, MS_Hash.KEY_LENGTH_MINIMUM);
		String str11 = MS_Hash.getHash(TEXT, 263);
		String str2 = MS_Hash.getHash(TEXT, MS_Hash.KEY_LENGTH_MEDIUM);
		String str22 = MS_Hash.getHash(TEXT, 768);
		String str3 = MS_Hash.getHash(TEXT, MS_Hash.KEY_LENGTH_LONG);
		String str4 = MS_Hash.getHash(TEXT, MS_Hash.KEY_LENGTH_MAXIMUM);
		String str5 = MS_Hash.getHash(TEXT, "");
		String str6 = MS_Hash.getHash(TEXT, "a");
		String str7 = MS_Hash.getHash(TEXT, 0);

		assertEquals(0, str0.length());
		assertEquals(44, str1.length());
		assertEquals(88, str2.length());
		assertEquals(172, str3.length());
		assertEquals(344, str4.length());

		assertTrue(str1.length() < str2.length());
		assertTrue(str2.length() < str22.length());
		assertEquals(str1.length(), str11.length());
		assertTrue(str1.equals(str11));
		assertTrue(! str1.equals(str3));
		assertEquals("", str5);
		assertNotEquals("", str6);
		assertEquals(44, str6.length());
		assertEquals("", str7);

		String str71 = MS_Hash.getHash("aa", "a");
		String str72 = MS_Hash.getHash("a", "aa");
		String str73 = MS_Hash.getHash("a", "aa");
		assertTrue(! str71.equals(str72));
		assertTrue(str73.equals(str72)); 
	}
}
