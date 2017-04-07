package lv.emes.libraries.examples;

import lv.emes.libraries.tools.MS_CodingTools;
import lv.emes.libraries.tools.platform.windows.MS_WindowsAPIManager;
import lv.emes.libraries.tools.platform.windows.MediaEventTypeEnum;

/**
 *
 * @author eMeS
 * @version 1.0.
 */
public class MSWindowsAPIManagerExample {

    public static void main(String[] args) {
        //when music is playing in some media player
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.PREV_TRACK);
        MS_CodingTools.sleep(4000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.STOP_MUSIC);
        MS_CodingTools.sleep(3000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.PLAY_PAUSE_MUSIC);
        MS_CodingTools.sleep(3000);
        MS_WindowsAPIManager.fireMediaEvent(MediaEventTypeEnum.NEXT_TRACK);
    }
}
