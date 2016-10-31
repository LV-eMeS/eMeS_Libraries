package lv.emes.libraries.other;

import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.MS_Tools;

public class ModuleTest {
	public static void main(String[] args) {
		System.out.println("cDiactriticChCount is: "+MS_StringTools.cDiactriticChCount);
		System.out.println(MS_Tools.getSystemUserName); 
		
		IStringConverterTest sConverter = s -> Integer.parseInt(s); //pārrakstām abstrakto metodi
		Integer skaitlis = sConverter.doAbstractAction("158");
		int sk = skaitlis.intValue() + 2;
		System.out.println(sk);
		
		//rakstām citu metodi citam objektam, kurš izpildīs pavisam citu funkciju
		IStringConverterTest cloneNumber = s ->  Integer.parseInt(s.concat(s));
		Integer sk2 = cloneNumber.doAbstractAction("158");
		System.out.println(sk2);
		
		//un vēl vienu metodi, izmantojot šo pašu interfeisu
		IStringConverterTest multiplyBy2 = s -> { 
			Integer res = Integer.parseInt(s);
			res = res.intValue()*2;
			return res; 
		}; 
		Integer sk3 = multiplyBy2.doAbstractAction("158");
		System.out.println(sk3);
	}

}
