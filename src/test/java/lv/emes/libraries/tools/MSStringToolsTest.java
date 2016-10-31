package lv.emes.libraries.tools;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.EnumSet;

import static lv.emes.libraries.tools.MS_StringTools.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MSStringToolsTest {

	@Test
	public void test01PrimitiveFunctions() {	
		//test constants
		System.out.println("My test message"+MS_StringTools.cLineBrake+"is in new line");		
		System.out.println("My test message"+MS_StringTools.cTabSpace+"is delimited with tab");
		System.out.println("cDiactriticChCount is: "+MS_StringTools.cDiactriticChCount);	
		
		//test genRandomString
		MS_StringTools.SetForCodeGenParams.addAll(Collections.synchronizedSet(EnumSet.allOf(TSymbolTypeForGenerator.class)));
		MS_StringTools.SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgSpecialSymbol);
		MS_StringTools.SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgNormalSymbol);
		MS_StringTools.SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgSmallLetter);
		MS_StringTools.SetForCodeGenParams.remove(TSymbolTypeForGenerator.stfgBigLetter);
		
		MS_StringTools.SetForCodeGenParams.add(TSymbolTypeForGenerator.stfgNormalSymbol);
		System.out.println(
				MS_StringTools.getRandomString(100, MS_StringTools.SetForCodeGenParams)
				);	
		assertTrue(true);
	}
	
	@Test
	public void test02Substrings() {
		//test substrings
		assertFalse(MS_StringTools.isSubstring("please", "Test me, PLEASE!"));
		assertFalse(MS_StringTools.isSubstring("please", "Test me, PLEASE!", true));
		assertTrue(MS_StringTools.isSubstring("please", "Test me, PLEASE!", false));
		assertTrue(MS_StringTools.isSubstring("PLEaSE", "Test me, PLEASE!", false));
		
		assertTrue(MS_StringTools.textContains("My text contains", "contains"));
		assertTrue(MS_StringTools.textContains("My text contains", "contains", true));
		assertFalse(MS_StringTools.textContains("My text CONTAINS", "contains"));
		assertTrue(MS_StringTools.textContains("My text CONTAINS", "contains", false));
		assertTrue(MS_StringTools.textContains("My text contains", "c"));
		assertTrue(MS_StringTools.textContains("My text cOntains", "O"));
		assertFalse(MS_StringTools.textContains("My text contains", "O"));
	}
	
	@Test
	public void test03Replaces() {		
		assertTrue(MS_StringTools.replaceInString("Mana manna garšo labi", "na", "XXX", true).
				equals("MaXXX manXXX garšo labi"));
		assertTrue(MS_StringTools.replaceInString("Mana manna garšo labi", "NA", "XXX").
				equals("MaXXX manXXX garšo labi"));
		assertTrue(MS_StringTools.replaceInString("aaa", "aa", "b").equals("ba"));
		assertTrue(MS_StringTools.replaceInString("foo.bar", ".", "").equals("foobar"));
	}
	
	@Test
	public void test04OtherSmallOperations() {
		//test other small operations		
		assertTrue(MS_StringTools.getInversedText("ABC123cba").equals("abc321CBA"));
		String testMyText = "my Text mmM";
		assertTrue(MS_StringTools.getTextWithCapitalizedFirstLetter(testMyText).equals("My Text mmM"));
		assertTrue(MS_StringTools.hasOnlyDigitsInText("0631"));
		assertTrue(isCharASCIILetter('a'));
		assertFalse(isCharASCIILetter('ā'));
		assertTrue(MS_StringTools.hasOnlyASCIILettersInText("ZabcdEFG"));
		assertFalse(MS_StringTools.hasOnlyASCIILettersInText("1"));
		assertTrue(removeLV("ō, kāds brīnumiņš!").equals("o, kads brinumins!"));
		assertTrue(removePunctuation("Viens teikums. Otrs teikums! Trešais... Ceturtais arī vēl būs? Nu tad jau redzēs //?''\\").
				equals("Viens teikums Otrs teikums Trešais Ceturtais arī vēl būs Nu tad jau redzēs ''"));
		System.out.println(getHashFromString("My test -> String; Dīvains tekstiņš. Русский язык."));
		assertEquals(8, MS_StringTools.pos("Pattern X in this text", "X"));

		assertEquals("given", MS_StringTools.getSubstring("given text", 0, 5));
		assertEquals("XXX", MS_StringTools.getSubstring("AbcdXXXefg", 4, 7));
		assertEquals("", MS_StringTools.getSubstring("AbcdXXXefg", 100, 7));
		assertEquals("text to test", MS_StringTools.getSubstring(" A text to test", 3, 700));
		assertEquals("a", MS_StringTools.getSubstring("a", 0, 1));
		assertEquals("a", MS_StringTools.getSubstring("a", 0, 2));
		assertEquals("", MS_StringTools.getSubstring("a", 0, 0));
		assertEquals("", MS_StringTools.getSubstring("a", 1, 1));
		assertEquals(MS_StringTools.cTabSpace, MS_StringTools.getTabSpace(1));
		assertEquals(MS_StringTools.cTabSpace + MS_StringTools.cTabSpace, MS_StringTools.getTabSpace(2));
		assertEquals(MS_StringTools.cTabSpace + MS_StringTools.cTabSpace + MS_StringTools.cTabSpace, MS_StringTools.getTabSpace(3));
		assertEquals("", MS_StringTools.getTabSpace(-33));
		assertEquals("", MS_StringTools.getTabSpace(0));
		//Apsveicami! Viss ir notestēts! Viss darbojas ^_^
	}
}
