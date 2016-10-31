package lv.emes.libraries.tools;

import java.net.URL; 
import java.util.Random;

/** 
 * Module is designed to combine different common programming actions.
 * @version 1.4.
 */
public final class MS_Tools {
	/**
	 * finds out, which jar file holds given class
	 * Example: aPathToClass = "org/apache/http/message/BasicLineFormatter.class"
	 */
	public static String getJarByPathToResource(String aPathToClass){
		ClassLoader classLoader = MS_Tools.class.getClassLoader();
		URL resource = classLoader.getResource(aPathToClass);
		return resource.toString();
	}

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
}
