package lv.emes.libraries.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class Java8Test {

	public static void main(String[] args) {
		List<String> stringCollection = new ArrayList<> ();
		stringCollection.add("ddd2");
		stringCollection.add("aaa2");
		stringCollection.add("bbb1");
		stringCollection.add("aaal");
		stringCollection.add("bbb3");
		stringCollection.add("ccc");
		stringCollection.add("bbb2");
		stringCollection.add("dddl");
		
		
		List<String> stringCollection2 = new ArrayList<> ();
		Comparator<String> comp = (s, e) -> s.compareTo(e);
		stringCollection
			.stream() //atgriež sarakstu kā plūsmu 
			.filter((s) -> s.startsWith("b")) //atgriež jau filtrētu plūsmu pēc nosacījuma, kas kā atribūts definēts predikāta veidā
			.sorted(comp)
			.forEach((text) -> {
				//System.out.println(text);
				stringCollection2.add(text);
			});
			
		//http://www.oracle.com/technetwork/articles/java/java8-optional-2175753.html
		stringCollection2.add("abc");
		Collections.sort(stringCollection2, (s1, s2) -> s1.compareTo(s2));
		stringCollection2.forEach(s -> System.out.println(s));		
		
		String str1 = new String("The null string is present");
		Consumer<String> printlnString1 = s -> System.out.println(s + "???");
		Optional<String> optionalString1 = Optional.ofNullable(str1); //ja būs null, tad exception nebūs
		optionalString1.ifPresent(printlnString1);
		
		String str2 = null;
		Optional<String> optionalString2 = Optional.ofNullable(str2); 
		optionalString2.ifPresent(s -> System.out.println(s));		
		System.out.println(optionalString2.isPresent());
		
		Optional<String> optionalString3 = Optional.of(null); //ja būs null, tad būs exception
		optionalString3.ifPresent(System.out::println);
	}

}
