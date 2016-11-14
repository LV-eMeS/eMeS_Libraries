package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_ScriptRunner;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) {
//        MS_ScriptRunner.runScript("#hold#win#d#release#win"); //TODO some problem with Windows key recognition
//        MS_ScriptRunner.runScript("#wshow#Rīki##wshow#firefox#");
//        MS_ScriptRunner.runScript("#run#D:/Dropbox/IT lietiņas/Mani tūļi/Taimeris/eMeS_Timer.exe,5#");
        MS_ScriptRunner.runScript(":;###"); //TODO this cannot recognize even semicolon
    }
}
