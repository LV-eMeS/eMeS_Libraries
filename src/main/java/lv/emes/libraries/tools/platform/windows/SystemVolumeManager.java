package lv.emes.libraries.tools.platform.windows;

import lv.emes.libraries.file_system.MS_FileSystemTools;

/**
 * Description.
 * <p>Methods:
 *
 * @author eMeS
 * @version 0.1.
 */
public final class SystemVolumeManager {
    //PRIVATE VARIABLES
    private static String nircmdFileName = "";

    //PUBLIC METHODS

    /**
     * Creates copy of "nircmd.exe" file that can be found in resources and returns full path to it.
     * If file is already created the existing file's path is returned.
     * @return path to temporary copy of "nircmd.exe".
     */
    public static String getNircmdFileName() {
        if (nircmdFileName.equals(""))
            nircmdFileName = MS_FileSystemTools.extractResourceToTmpFolder(MS_FileSystemTools.NIRCMD_FILE_FOR_WINDOWS);
        return nircmdFileName;
    }

    /**
     * Turns system value up by <b>level</b> specified.
     * Windows only function.
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-5000.
     */
    public static void volumeUp(Integer level) {
        String parameters = "changesysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Turns system value down by <b>level</b> specified.
     * Windows only function.
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-5000.
     */
    public static void volumeDown(Integer level) {
        level = level * -1;
        String parameters = "changesysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }

    /**
     * Sets system value to <b>level</b> specified.
     * Windows only function.
     * @param level nircmd.exe parameter that matches volume level. Recommended: 1000-40000.
     */
    public static void setVolume(Integer level) {
        String parameters = "setsysvolume " + level;
        MS_FileSystemTools.executeApplication(getNircmdFileName(), parameters);
    }
}