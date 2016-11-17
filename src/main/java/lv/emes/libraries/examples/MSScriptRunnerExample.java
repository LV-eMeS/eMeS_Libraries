package lv.emes.libraries.examples;

import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) throws AWTException {
        //For building
//        String commandText = MS_TextFile.getFileTextAsString(args[0], "");
//        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
//        runner.setPathToLoggerFile(getFilenameWithoutExtension(getShortFilename(args[0])) + ".log");
//        runner.setInputMethod(IFuncStringInputMethod.CONSOLE);
//        runner.runScript();
        //for testing:
        String commandText = "pause#pidarasi!";
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.runScript();
    }
}