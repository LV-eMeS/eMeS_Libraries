package lv.emes.libraries.other;

import lv.emes.libraries.tools.MS_TimeTools;
import lv.emes.libraries.tools.MS_TimeTools.IncorrectStartingYearException;
import lv.emes.libraries.tools.MS_Tools;
import org.junit.Test;

public class ExceptionTest {
	private void throwingSomeError() throws Exception {
		int rnd = MS_Tools.randomNumber(1, 2);
		if (rnd == 1)
			throw new MS_TimeTools.IncorrectStartingYearException(2005, "19.05.2016 17:00");
		else
			throw new Exception();
		}
	
	@Test
	public void testSomething() {
		try {
			throwingSomeError();
			System.out.println("You will never see me! :P");
		} catch (IncorrectStartingYearException e) {
			System.out.println("MSExc_IncorrectStartingYear");
		} catch (Exception e) {
			System.out.println("Exception");
		} finally {
			System.out.println("Finally");
		}
		System.out.println("Even after try..catch..finally");
	}
}
