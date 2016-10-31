package lv.emes.libraries.other;

@FunctionalInterface
/**
 * Interfeisam jābūt tikai vienai vienīgai abstraktai metodei, kura tiks izmantota, lai to pārrakstītu funkcionāli.
 * @author Maris
 * IStringConverterTest sConverter = (s) -> Integer.parseInt(s);
 * Integer skaitlis = sConverter.convert("158"); //tādējādi var konvertēt skaitļis no String uz Integer.
 */
public interface IStringConverterTest {
	Integer doAbstractAction(String from);
}
