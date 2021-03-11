package lv.emes.libraries.communication.cryptography;

import lv.emes.libraries.utilities.MS_DateTimeUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MS_HashTest {

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

		assertThat(str0.length()).isEqualTo(0);
		assertThat(str1.length()).isEqualTo(44);
		assertThat(str2.length()).isEqualTo(88);
		assertThat(str3.length()).isEqualTo(172);
		assertThat(str4.length()).isEqualTo(344);

		assertThat(str1.length() < str2.length()).isTrue();
		assertThat(str2.length() < str22.length()).isTrue();
		assertThat(str11.length()).isEqualTo(str1.length());
		assertThat(str11).isNotEqualTo(str1); //if length is changed hash will differ as well
		assertThat(str22).isNotEqualTo(str2); //same goes for different base key length
		assertThat(str3).isNotEqualTo(str1);
		assertThat(str5).isEqualTo("");
		assertThat(str6).isNotEqualTo("");
		assertThat(str6.length()).isEqualTo(44);
		assertThat(str7).isEqualTo("");

		String str71 = MS_Hash.getHash("aa", "a");
		String str72 = MS_Hash.getHash("a", "aa");
		String str73 = MS_Hash.getHash("a", "aa");
		assertThat(!str71.equals(str72)).isTrue();
		assertThat(str73.equals(str72)).isTrue();
	}

	@Test
	public void testHashingSameValueReturnsSameHash() {
		final String TEXT = "https://docs.oracle.com/javase/7/docs/api/java/lang/Byte.html";
		final String salt = "salt";
		assertThat(MS_Hash.getHash(TEXT)).isEqualTo(MS_Hash.getHash(TEXT));
		assertThat(MS_Hash.getHash(TEXT, salt, MS_Hash.KEY_LENGTH_MINIMUM)).isEqualTo(MS_Hash.getHash(TEXT, salt, MS_Hash.KEY_LENGTH_MINIMUM));
		assertThat(MS_Hash.getHash(TEXT, salt)).isEqualTo(MS_Hash.getHash(TEXT, salt));
		assertThat(new MS_Hash(salt, MS_Hash.KEY_LENGTH_MINIMUM, null, false).hash((TEXT)))
				.isEqualTo(new MS_Hash(salt, MS_Hash.KEY_LENGTH_MINIMUM, null, false).hash((TEXT)));
		assertThat(new MS_Hash(salt, MS_Hash.KEY_LENGTH_MINIMUM, 1, false).hash((TEXT)))
				.isEqualTo(new MS_Hash(salt, MS_Hash.KEY_LENGTH_MINIMUM, 1, false).hash((TEXT)));
		assertThat(new MS_Hash(null, null, null, false).hash((TEXT)))
				.isEqualTo(new MS_Hash(null, null, null, false).hash((TEXT)));
		assertThat(new MS_Hash(null, null, null, true).hash((TEXT)))
				.isEqualTo(new MS_Hash(null, null, null, true).hash((TEXT)));
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
		assertThat(startTime.plus(Duration.ofMillis(600)).isAfter(endTime)).as(ASSERTION_MESSAGE).isTrue();

		//test with KEY_LENGTH_MINIMUM
		startTime = LocalTime.now();
		System.out.println("Hashing performance test started at: " + MS_DateTimeUtils.timeToStr(startTime));

		MS_Hash.getHash(ASSERTION_MESSAGE, MS_Hash.KEY_LENGTH_MEDIUM); //all the hashing action should take less than 1 second

		endTime = LocalTime.now();
		System.out.println("Hashing performance test ended at: " + MS_DateTimeUtils.timeToStr(endTime));
		assertThat(startTime.plus(Duration.ofSeconds(1)).isAfter(endTime)).as(ASSERTION_MESSAGE).isTrue();

		//test with KEY_LENGTH_MAXIMUM
		startTime = LocalTime.now();
		System.out.println("Hashing performance test started at: " + MS_DateTimeUtils.timeToStr(startTime));

		MS_Hash.getHash(ASSERTION_MESSAGE, MS_Hash.KEY_LENGTH_MAXIMUM); //all the hashing action should take less than 1,25 seconds

		endTime = LocalTime.now();
		System.out.println("Hashing performance test ended at: " + MS_DateTimeUtils.timeToStr(endTime));
		assertThat(startTime.plus(Duration.ofMillis(1250)).isAfter(endTime)).as(ASSERTION_MESSAGE).isTrue();
	}

	@Test
	public void testStaticHashingVsHashInstance() {
		final String aTextToHash = "Test how are different hashes generated in different ways";
		final String aTextToHash2 = "Alternative text";
		SoftAssertions.assertSoftly(softly -> {
			String staticHash1 = MS_Hash.getHash(aTextToHash, MS_Hash.KEY_LENGTH_MINIMUM);
			MS_Hash instance1 = new MS_Hash(null, MS_Hash.KEY_LENGTH_MINIMUM, null, true);
			softly.assertThat(instance1.hash(aTextToHash)).isEqualTo(staticHash1);

			softly.assertThat(instance1.hash(aTextToHash2)).isEqualTo(MS_Hash.getHash(aTextToHash2, MS_Hash.KEY_LENGTH_MINIMUM));

			MS_Hash instance2 = new MS_Hash(null, MS_Hash.KEY_LENGTH_MINIMUM + 1, null, true);
			softly.assertThat(instance2.hash(aTextToHash)).isNotEqualTo(staticHash1);

			// Test encoding
			MS_Hash instance3 = new MS_Hash(null, MS_Hash.KEY_LENGTH_MINIMUM, null, false);
			String hash3 = instance3.hash(aTextToHash);
			System.out.println(hash3);
			softly.assertThat(hash3).isNotEqualTo(staticHash1);

			// Test different salt
			MS_Hash instance4 = new MS_Hash(aTextToHash2, MS_Hash.KEY_LENGTH_MINIMUM, null, false);
			String hash4 = instance4.hash(aTextToHash);
			System.out.println(hash4);
			softly.assertThat(instance4.hash(aTextToHash)).isNotEqualTo(hash3);
		});
	}
}
