package lv.emes.libraries.tools.platform;

import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.MS_CodingTools;

/** 
 * An recognizable and commonly used OS token between eMeS libraries.
 * <p>Setters and getters:
 * <ul>
 *     <li>getFullOSName</li>
 *     <li>getShortOSName</li>
 *     <li>getOSID</li>
 * </ul>
 * @version 1.0.
 * @author eMeS
 */
public class MS_OperatingSystem {
	//PUBLIC STRUCTURES, EXCEPTIONS, PROPERTIES AND CONSTANTS
	public final static String _OS_WINDOWS = "Windows";
	public final static String _OS_UNIX = "Unix";
	public final static String _OS_ANDROID = "Android";
	public final static String _OS_MAC = "Mac";
	
	public final static int _OS_WINDOWS_ID = 1;
	public final static int _OS_UNIX_ID = 2;
	public final static int _OS_ANDROID_ID = 3;
	public final static int _OS_MAC_ID = 4;

	//PRIVATE VARIABLES
	private String fullOSName = "";
	private String shortOSName = "";
	private int osID = 0;

	//CONSTRUCTORS
	public MS_OperatingSystem() {
		fullOSName = MS_CodingTools.getSystemOS;
		if (MS_StringTools.textContains(fullOSName, "windows", false)) {
			shortOSName = _OS_WINDOWS;
			osID = _OS_WINDOWS_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "buntu", false)) {
			shortOSName = _OS_UNIX;
			osID = _OS_UNIX_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "droid", false)) {
			shortOSName = _OS_ANDROID;
			osID = _OS_ANDROID_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "mac", false)) {
			shortOSName = _OS_MAC;
			osID = _OS_MAC_ID;
		}
	}

	//STATIC CONSTRUCTORS
	//PRIVATE METHODS
	//PROTECTED METHODS
	//PUBLIC METHODS
	public String getShortOSName() {
		return this.shortOSName;
	}
	
	public String getFullOSName() {
		return this.fullOSName;
	}
	
	public int getOSID() {
		return this.osID;
	}
	
	//STATIC METHODS
}
