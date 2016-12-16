package lv.emes.libraries.tools.platform;

/** 
 * This module simply detects OS in which Java program is launched and translates it in commonly recognizable format.
 * <p>Static methods:
 * <ul>
 *     <li>getOS</li>
 * </ul>
 * @version 1.1.
 * @author eMeS
 */
public class MS_PlatformIndependentTools {
	//PRIVATE VARIABLES	
	private static MS_OperatingSystem os = new MS_OperatingSystem();
	
	//PRIVATE METHODS	
	//PUBLIC METHODS
	/**
	 * @return device OS recognizable object.
	 */
	public static MS_OperatingSystem getOS() {
		return os;
	}
}
