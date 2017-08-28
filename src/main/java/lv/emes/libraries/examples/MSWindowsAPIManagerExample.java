package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.windows.MS_WindowsAPIManager;
import lv.emes.libraries.tools.platform.windows.MediaEventTypeEnum;
import lv.emes.libraries.utilities.MS_CodingUtils;

/**
 *
 * @author eMeS
 * @version 1.0.
 */
public class MSWindowsAPIManagerExample {

    public static void main(String[] args) {
        //when music is playing in some media player
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.PREV_TRACK);
        MS_CodingUtils.sleep(4000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.STOP_MUSIC);
        MS_CodingUtils.sleep(3000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.PLAY_PAUSE_MUSIC);
        MS_CodingUtils.sleep(3000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.NEXT_TRACK);
    }
}
