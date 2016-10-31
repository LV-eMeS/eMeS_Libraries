package lv.emes.libraries.tools.platform;

/** 
 * This module simply detects OS in which Java program is launched and translates it in commonly recognizable format.
 * <p>Methods:
 * -getOS
 * -getKeystrokeExecutor
 * @version 1.0.
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
	public MS_OperatingSystem getOS() {
		return os;
	}
}
