package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.platform.IFuncStringInputMethod;
import lv.emes.libraries.tools.platform.IFuncStringOutputMethod;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

/**
 * @author eMeS
 */
public class MSScriptRunnerExecutable {
    public static void main(String[] args) throws AWTException {
        //for testing:
//        String commandText = "variable#varrrr&Please, write something!#say#$varrrr$";
//        String commandText = "media#prev#";

        //For building
        String commandText = MS_TextFile.getFileTextAsString(args[0], "");
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
//        runner.setPathToLoggerFile("MSScriptRunnerExecutable.log");
        runner.setVariableInputMethod(IFuncStringInputMethod.JOPTION_PANE);
        runner.setPasswordInputMethod(IFuncStringInputMethod.JOPTION_PANE_MASKED);
        runner.setOutputMethod(IFuncStringOutputMethod.JOPTION_PANE);
        runner.runScript();
    }
}