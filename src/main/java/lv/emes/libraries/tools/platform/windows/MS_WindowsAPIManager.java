package lv.emes.libraries.tools.platform.windows;

import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.tools.platform.MS_IncompatibleOSException;
import lv.emes.libraries.tools.platform.MS_OperatingSystem;
import lv.emes.libraries.tools.platform.MS_PlatformIndependentTools;

import static com.sun.jna.platform.win32.WinUser.KEYBDINPUT.KEYEVENTF_EXTENDEDKEY;

/**
 * Handles functions for OS volume.
 * <p>Methods:
 * <ul>
 *     <li>volumeUp</li>
 *     <li>volumeDown</li>
 *     <li>setVolume</li>
 *     <li>turnMonitor</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.4.
 */
public final class MS_WindowsAPIManager {

    //PRIVATE VARIABLES
    private static String nircmdFileName = "";
    private static final String TEMP_DIRECTORY_FOR_NIRCMD = "eMeS_SystemVolumeManager";

    //PRIVATE METHODS
    private static void checkOSAndThrowExceptionIfIncompatible() throws MS_IncompatibleOSException {
        if (MS_PlatformIndependentTools.getOS().getOSID() != MS_OperatingSystem._OS_WINDOWS_ID)
            throw new MS_IncompatibleOSException("Cannot change Windows system volume in different operating system.");
    }

    //PUBLIC METHODS

    /**
     * Creates copy of "nircmd.exe" file that can be found in resources and returns full path to it.
     * If file is already created the existing file's path is returned.
     *
     * @return path to temporary copy of "nircmd.exe".
     */
    private static String getNircmdFileName() {
        if (nircmdFileName.equals(""))
            nircmdFileName = MS_FileSystemTools.extractResourceToTmpFolder(MS_FileSystemTools._NIRCMD_FILE_FOR_WINDOWS, TEMP_DIRECTORY_FOR_NIRCMD, false);
        return nircmdFileName;
    }

    /**
     * Turns system value up by <b>level</b> specified.
     * Windows only function.
     *
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-5000.
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void volumeUp(Integer level) throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        String parameters = "changesysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Turns system value down by <b>level</b> specified.
     * <br><u>Warning</u>: Windows only function.
     *
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-5000.
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void volumeDown(Integer level) throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        level = level * -1;
        String parameters = "changesysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Sets system value to <b>level</b> specified.
     * <br><u>Warning</u>: Windows only function.
     *
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-40000.
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void setVolume(Integer level) throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        String parameters = "setsysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Toggles system sound mute state.
     * <br><u>Warning</u>: Windows only function.
     *
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void muteVolume() throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        String parameters = "mutesysvolume 2"; //the “2” argument tells nircmd to toggle mute
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Turns monitor on or off depending on presented parameter <b>state</b>.
     *
     * @param state case insensitive "ON" or "OFF".
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void turnMonitor(String state) throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        String parameters = "cmdwait 100 monitor " + state;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Executes Nircmd command.
     * Result is the same as in the command line command "nircmd <b>cmdTextAndArgs</b>" would've been executed.
     *
     * @param cmdTextAndArgs command and arguments (if any) delimited with space as usual command line command.
     * @throws MS_IncompatibleOSException if trying to use this method in different OS than Windows.
     */
    public static void executeNircmd(String cmdTextAndArgs) throws MS_IncompatibleOSException {
        checkOSAndThrowExceptionIfIncompatible();
        MS_FileSystemTools.executeApplication(getNircmdFileName(), cmdTextAndArgs);
    }

    public static void fireMediaEvent(MediaEventTypeEnum eventType) {
        MS_User32.INSTANCE.keybd_event(eventType.getWindowsAPIKeyCode(), (byte) 0, KEYEVENTF_EXTENDEDKEY, 0);
    }
}