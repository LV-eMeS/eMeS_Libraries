package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.platform.MS_InputOutputMethodDefaults;
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
        runner.setVariableInputMethod(MS_InputOutputMethodDefaults.INPUT_JOPTION_PANE);
        runner.setPasswordInputMethod(MS_InputOutputMethodDefaults.INPUT_JOPTION_PANE_MASKED);
        runner.setOutputMethod(MS_InputOutputMethodDefaults.OUTPUT_JOPTION_PANE);
        runner.runScript();
    }
}