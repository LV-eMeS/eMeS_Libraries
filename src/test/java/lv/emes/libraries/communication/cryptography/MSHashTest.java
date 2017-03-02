package lv.emes.libraries.communication.cryptography;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import lv.emes.libraries.communication.cryptography.MS_Hash;

public class MSHashTest {	
	@Test
	public void test() {
		String str1 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", 256);
		String str11 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", 263);
		String str2 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", 512);
		String str3 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", 1048);
		String str4 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", "");
		String str5 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", "a");
		String str6 = MS_Hash.getHash("https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html", 0);		
		assertTrue(str1.length() < str2.length());
		assertTrue(str1.length() == str11.length());
		assertTrue(str1.equals(str11));
		assertTrue(! str1.equals(str3));
		assertTrue(str4.equals(""));
		assertTrue(! str5.equals(""));
		assertTrue(str6.equals(""));
		String str71 = MS_Hash.getHash("aa", "a");
		String str72 = MS_Hash.getHash("a", "aa");
		String str73 = MS_Hash.getHash("a", "aa");
		assertTrue(! str71.equals(str72));
		assertTrue(str73.equals(str72)); 
	}
}
