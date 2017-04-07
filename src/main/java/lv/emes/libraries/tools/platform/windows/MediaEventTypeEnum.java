package lv.emes.libraries.tools.platform.windows;

import java.util.HashMap;
import java.util.Map;

import static lv.emes.libraries.tools.platform.windows.MS_User32.*;

/**
 * This enum describes supported types of actions that can be executed for Windows API media events.
 * <p>Setters and getters:
 * <ul>
 * <li>getWindowsAPIKeyCode</li>
 * </ul>
 * <p>Static methods:
 * <ul>
 * <li>getByKey</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public enum MediaEventTypeEnum {

    PLAY_PAUSE_MUSIC(VK_MEDIA_PLAY_PAUSE), STOP_MUSIC(VK_MEDIA_STOP), NEXT_TRACK(VK_MEDIA_NEXT_TRACK), PREV_TRACK(VK_MEDIA_PREV_TRACK);

    public static final String PLAY_MUSIC_KEY = "PLAY";
    public static final String PAUSE_MUSIC_KEY = "PAUSE";
    public static final String PLAY_PAUSE_MUSIC_KEY = "PLAYPAUSE";
    public static final String STOP_MUSIC_KEY = "STOP";
    public static final String NEXT_TRACK_KEY = "NEXT";
    public static final String PREVIOUS_TRACK_KEY = "PREV";

    private int windowsAPIKeyCode;
    private static Map<String, MediaEventTypeEnum> typesByKey = initKeys();

    private static Map<String, MediaEventTypeEnum> initKeys() {
        Map<String, MediaEventTypeEnum> res = new HashMap<>();
        res.put(PLAY_MUSIC_KEY, PLAY_PAUSE_MUSIC);
        res.put(PAUSE_MUSIC_KEY, PLAY_PAUSE_MUSIC);
        res.put(PLAY_PAUSE_MUSIC_KEY, PLAY_PAUSE_MUSIC);
        res.put(STOP_MUSIC_KEY, STOP_MUSIC);
        res.put(NEXT_TRACK_KEY, NEXT_TRACK);
        res.put(PREVIOUS_TRACK_KEY, PREV_TRACK);
        return res;
    }

    MediaEventTypeEnum(int windowsAPIKeyCode) {
        this.windowsAPIKeyCode = windowsAPIKeyCode;
    }

    /**
     * Gets enum by specific key.
     * @param key one of:
     *            <ul>
     *            <li>PLAY</li>
     *            <li>PAUSE</li>
     *            <li>PLAYPAUSE</li>
     *            <li>STOP</li>
     *            <li>NEXT</li>
     *            <li>PREV</li>
     *            </ul>
     * @return valid media event type enum or null if key is invalid.
     */
    public static MediaEventTypeEnum getByKey(String key) {
        return typesByKey.get(key.toUpperCase());
    }

    public int getWindowsAPIKeyCode() {
        return windowsAPIKeyCode;
    }
}
