package lv.emes.libraries.examples;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) throws AWTException {
//        MS_ScriptRunner.runScript("#hold#ctrl#esc#release#ctrl"); //TODO some problem with Windows key recognition
//        MS_ScriptRunner.runScript("#wshow#Rīki##wshow#firefox#");
//        MS_ScriptRunner.runScript("#run#D:/Dropbox/IT lietiņas/Mani tūļi/Taimeris/eMeS_Timer.exe,5#");
//        MS_ScriptRunner.runScript("text#Let's see, if \"ME\" can do this now!"); //TODO this cannot recognize even semicolon
//        MS_ScriptRunner.runScript("test#");
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_QUOTEDBL);

    }
}
