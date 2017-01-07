package lv.emes.libraries.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/** 
 * Module is designed to combine different common programming actions.
 * @version 1.7.
 */
public final class MS_Tools {
	public static final String getSystemUserName = System.getProperty("user.name");
	public static final String getSystemUserCurrentWorkingDir = System.getProperty("user.dir") + "/";
	public static final String getSystemUserHomeDir = System.getProperty("user.home") + "/";
	public static final String getSystemOS = System.getProperty("os.name");
	
	public static int randomNumber(int aFrom, int aTill){
		int swapper;
		if (aTill<aFrom){
			swapper = aFrom;
		    aFrom = aTill;
		    aTill = swapper;
		}
		Random random = new Random();
		return random.nextInt(aTill - aFrom + 1) + aFrom;
	}
	
	//import static lv.emes.tools.MS_Tools.*;
	/** Checks if number <b>aNumber</b> is in interval [<b>aRangeMin</b>, <b>aRangeMax</b>].
	 * @param aNumber = 5
	 * @param aRangeMin = 1
	 * @param aRangeMax = 5
	 * @return true
	 */
	public static boolean inRange(int aNumber, int aRangeMin, int aRangeMax) {
		return Math.min(aRangeMin, aRangeMax) <= aNumber && Math.max(aRangeMin, aRangeMax) >= aNumber;
	}

	/**
	 * Simply prints object to console output using object's <b>toString</b> method.
	 * Before <b>toString</b> method a text <code>"-----DEBUG: "+<b>text</b>+" -----"</code> will be printed to easy find this debug text in console window.
	 *
	 * @param obj an object with <b>toString</b> method implemented.
	 * @param text text to be printed with debug object (to use Ctrl + F to find text in console output).
	 */
	public static void debugObjectConsoleOutput(Object obj, String text) {
		System.out.println("-----DEBUG: " + text + " -----");
		System.out.println(obj);
		System.out.println("-----DEBUG-----");
	}

	/**
	 * Delays application activity for some time.
	 * Method is using Thread.sleep().
	 * @param miliseconds amount of miliseconds that will delay application.
	 */
	public static void sleep(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delays application activity for some time.
	 * Method is using Thread.sleep().
	 * @param miliseconds amount of miliseconds that will delay application.
	 */
	public static void pause(long miliseconds) {
		sleep(miliseconds);
	}

	/**
	 * Simply returns opposite value of given boolean variable <b>bool</b>.
	 * @param bool a boolean which we have to inverse.
	 * @return boolean with opposite value of passed boolean.
	 */
	public static Boolean inverseBoolean(Boolean bool) {
		return ! bool;
	}

	public static String readlnStringFromConsole() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		} catch (IOException e) {
			return "";
		}
	}

	public static Double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}