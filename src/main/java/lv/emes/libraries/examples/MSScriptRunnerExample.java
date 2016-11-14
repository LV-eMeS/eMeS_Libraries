package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) throws AWTException {
        MS_ScriptRunner.runScript("text#Lets see, if I can do this now!"); //TODO this cannot recognize apostrophes
//        MS_ScriptRunner.runScript("hold#alt#num3#num9#release#alt#a#"); //34 - quotes, 39 - apostrophe
//        MS_ScriptRunner.runScript("test#");
    }
}