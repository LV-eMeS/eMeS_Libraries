package lv.emes.libraries.tools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.EnumSet;

import static lv.emes.libraries.tools.MS_StringTools.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSStringToolsTest {

	@Test
	public void test01PrimitiveFunctions() {	
		//test constants
		System.out.println("My test message"+ _LINE_BRAKE +"is in new line");
		System.out.println(_TAB_SPACE + "My test message begins with tab");
		System.out.println("_DIACTRITIC_CHAR_COUNT is: "+ _DIACTRITIC_CHAR_COUNT);
		
		//test genRandomString
		SetForCodeGenParams.addAll(Collections.synchronizedSet(EnumSet.allOf(TSymbolTypeForGenerator.class)));
		SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgSpecialSymbol);
		SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgNormalSymbol);
		SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgSmallLetter);
		SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgBigLetter);
		
		SetForCodeGenParams.add(TSymbolTypeForGenerator.stfgNormalSymbol);
		System.out.println(
				getRandomString(100, SetForCodeGenParams)
				);	
		assertEquals(12, getRandomString(12).length());
	}
	
	@Test
	public void test02Substrings() {
		//test substrings
		assertFalse(isSubstring("please", "Test me, PLEASE!"));
		assertFalse(isSubstring("please", "Test me, PLEASE!", true));
		assertTrue(isSubstring("please", "Test me, PLEASE!", false));
		assertTrue(isSubstring("PLEaSE", "Test me, PLEASE!", false));
		
		assertTrue(textContains("My text contains", "contains"));
		assertTrue(textContains("My text contains", "contains", true));
		assertFalse(textContains("My text CONTAINS", "contains"));
		assertTrue(textContains("My text CONTAINS", "contains", false));
		assertTrue(textContains("My text contains", "c"));
		assertTrue(textContains("My text cOntains", "O"));
		assertFalse(textContains("My text contains", "O"));
	}

	@Test
	public void test03Replaces() {
		assertTrue(replaceInString("Mana manna garšo labi", "na", "XXX", true).
				equals("MaXXX manXXX garšo labi"));
		assertTrue(replaceInString("Mana manna garšo labi", "NA", "XXX").
				equals("MaXXX manXXX garšo labi"));
		assertTrue(replaceInString("aaa", "aa", "b").equals("ba"));
		assertTrue(replaceInString("foo.bar", ".", "").equals("foobar"));
	}

	@Test
	public void test04Array() {
		String[] arr;
		arr = getStringArray();
		assertEquals(0, arr.length);

		arr = getStringArray("test");
		assertEquals(1, arr.length);
		assertEquals("test", arr[0]);

		arr = getStringArray("test1", "test2");
		assertEquals(2, arr.length);
		assertEquals("test1", arr[0]);
		assertEquals("test2", arr[1]);

		arr = getStringArray("test1", "test2", "test3");
		assertEquals(3, arr.length);
		assertEquals("test1", arr[0]);
		assertEquals("test2", arr[1]);
		assertEquals("test3", arr[2]);
	}
	
	@Test
	public void test05OtherSmallOperations() {
		//test other small operations		
		assertTrue(getInversedText("ABC123cba").equals("abc321CBA"));
		String testMyText = "my Text mmM";
		assertTrue(getTextWithCapitalizedFirstLetter(testMyText).equals("My Text mmM"));
		assertTrue(hasOnlyDigitsInText("0631"));
		assertTrue(isCharASCIILetter('a'));
		assertFalse(isCharASCIILetter('ā'));
		assertTrue(hasOnlyASCIILettersInText("ZabcdEFG"));
		assertFalse(hasOnlyASCIILettersInText("1"));
		assertTrue(removeLV("ō, kāds brīnumiņš!").equals("o, kads brinumins!"));
		assertTrue(removePunctuation("Viens teikums. Otrs teikums! Trešais... Ceturtais arī vēl būs? Nu tad jau redzēs //?''\\").
				equals("Viens teikums Otrs teikums Trešais Ceturtais arī vēl būs Nu tad jau redzēs ''"));
		System.out.println(getHashFromString("My test -> String; Dīvains tekstiņš. Русский язык."));
		assertEquals(8, pos("Pattern X in this text", "X"));

		assertEquals("given", getSubstring("given text", 0, 5));
		assertEquals("XXX", getSubstring("AbcdXXXefg", 4, 7));
		assertEquals("", getSubstring("AbcdXXXefg", 100, 7));
		assertEquals("text to test", getSubstring(" A text to test", 3, 700));
		assertEquals("a", getSubstring("a", 0, 1));
		assertEquals("a", getSubstring("a", 0, 2));
		assertEquals("", getSubstring("a", 0, 0));
		assertEquals("", getSubstring("a", 1, 1));
		assertEquals(_TAB_SPACE, getTabSpace(1));
		assertEquals(_TAB_SPACE + _TAB_SPACE, MS_StringTools.getTabSpace(2));
		assertEquals(_TAB_SPACE + _TAB_SPACE + _TAB_SPACE, getTabSpace(3));
		assertEquals("", getTabSpace(-33));
		assertEquals("", getTabSpace(0));
		assertEquals(8, getPosition("X", "Pattern X in this text"));

		//Apsveicami! Viss ir notestēts! Viss darbojas ^_^
	}
}
