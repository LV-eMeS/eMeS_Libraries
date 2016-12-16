package lv.emes.libraries.tools.platform;

import lv.emes.libraries.tools.MS_StringTools;
import lv.emes.libraries.tools.MS_Tools;

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
	public final static String OS_WINDOWS = "Windows";
	public final static String OS_UNIX = "Unix";
	public final static String OS_ANDROID = "Android";
	public final static String OS_MAC = "Mac";
	
	public final static int OS_WINDOWS_ID = 1;
	public final static int OS_UNIX_ID = 2;
	public final static int OS_ANDROID_ID = 3;
	public final static int OS_MAC_ID = 4;

	//PRIVATE VARIABLES
	private String fullOSName = "";
	private String shortOSName = "";
	private int osID = 0;

	//CONSTRUCTORS
	public MS_OperatingSystem() {
		fullOSName = MS_Tools.getSystemOS;
		if (MS_StringTools.textContains(fullOSName, "windows", false)) {
			shortOSName = OS_WINDOWS;
			osID = OS_WINDOWS_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "buntu", false)) {
			shortOSName = OS_UNIX;
			osID = OS_UNIX_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "droid", false)) {
			shortOSName = OS_ANDROID;
			osID = OS_ANDROID_ID;
		}
		else if (MS_StringTools.textContains(fullOSName, "mac", false)) {
			shortOSName = OS_MAC;						
			osID = OS_MAC_ID;
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
