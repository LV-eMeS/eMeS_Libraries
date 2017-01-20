package lv.emes.libraries.examples;

import static lv.emes.libraries.tools.MS_CodingTools.*;

public class MSToolsExample {

	public static void main(String[] args) {
		String s = Character.toString((char)9835);
		System.out.println(s);		
		System.out.println(randomNumber(0, 3));	
		System.out.println(inRange(2, 1, 1));			
	}
}
