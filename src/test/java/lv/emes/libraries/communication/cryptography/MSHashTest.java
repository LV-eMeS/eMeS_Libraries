package lv.emes.libraries.communication.cryptography;

import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class MSHashTest {

	@Test
	public void testHashing() {
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
		assertNotEquals(str1, str11); //if length is changed hash will differ as well
		assertNotEquals(str2, str22); //same goes for different base key length
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

	@Test
	public void testHashingPerformance() {
		final String ASSERTION_MESSAGE = "Hashing time exceeds expected range";
		LocalTime startTime;
		LocalTime endTime;

		//test with KEY_LENGTH_MINIMUM
		startTime = LocalTime.now();
		System.out.println("Hashing performance test started at: " + MS_DateTimeUtils.timeToStr(startTime));

		MS_Hash.getHash(ASSERTION_MESSAGE, MS_Hash.KEY_LENGTH_MINIMUM); //all the hashing action should take less than 0,6 seconds

		endTime = LocalTime.now();
		System.out.println("Hashing performance test ended at: " + MS_DateTimeUtils.timeToStr(endTime));
		assertTrue(ASSERTION_MESSAGE, startTime.plus(Duration.ofMillis(600)).isAfter(endTime));

		//test with KEY_LENGTH_MINIMUM
		startTime = LocalTime.now();
		System.out.println("Hashing performance test started at: " + MS_DateTimeUtils.timeToStr(startTime));

		MS_Hash.getHash(ASSERTION_MESSAGE, MS_Hash.KEY_LENGTH_MEDIUM); //all the hashing action should take less than 1 second

		endTime = LocalTime.now();
		System.out.println("Hashing performance test ended at: " + MS_DateTimeUtils.timeToStr(endTime));
		assertTrue(ASSERTION_MESSAGE, startTime.plus(Duration.ofSeconds(1)).isAfter(endTime));

		//test with KEY_LENGTH_MAXIMUM
		startTime = LocalTime.now();
		System.out.println("Hashing performance test started at: " + MS_DateTimeUtils.timeToStr(startTime));

		MS_Hash.getHash(ASSERTION_MESSAGE, MS_Hash.KEY_LENGTH_MAXIMUM); //all the hashing action should take less than 1,25 seconds

		endTime = LocalTime.now();
		System.out.println("Hashing performance test ended at: " + MS_DateTimeUtils.timeToStr(endTime));
		assertTrue(ASSERTION_MESSAGE, startTime.plus(Duration.ofMillis(1250)).isAfter(endTime));
	}
}
