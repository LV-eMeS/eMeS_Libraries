package lv.emes.libraries.tools.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

/**
 * This interface extends User32 from JNA platform and adds additional functionality to it.
 * Currently media key event support is added, but other keyboard event functions can be used as well.
 * <p>Public methods:
 * <ul>
 * <li>keybd_event</li>
 * </ul>
 * <p>Properties:
 * <ul>
 * <li>INSTANCE</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public interface MS_User32 extends User32 {
    MS_User32 INSTANCE = Native.loadLibrary("user32.dll", MS_User32.class);

    /**
     * Does Windows API keybd_event call with presented parameters.
     *
     * @param bVk         A virtual-key code. The code must be a value in the range 1 to 254.
     * @param bScan       A hardware scan code for the key.
     * @param dwFlags     Controls various aspects of function operation. This parameter can be one or more of the following values.
     * @param dwExtraInfo An additional value associated with the key stroke.
     * @see <a href="https://msdn.microsoft.com/en-us/library/ms646304(v=vs.85).aspx">msdn.microsoft.com keybd_event function</a>
     */
    void keybd_event(int/*byte*/ bVk, byte bScan, int dwFlags, int dwExtraInfo);

    int VK_MEDIA_NEXT_TRACK = 0xB0;
    int VK_MEDIA_PREV_TRACK = 0xB1;
    int VK_MEDIA_STOP = 0xB2;
    int VK_MEDIA_PLAY_PAUSE = 0xB3;
}
