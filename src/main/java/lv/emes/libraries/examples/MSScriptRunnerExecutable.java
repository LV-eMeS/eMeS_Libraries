package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.platform.MS_InputOutputMethodDefaults;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

/**
 * @version 2.2.
 * @author eMeS
 */
public class MSScriptRunnerExecutable {

    public static void main(String[] args) {
        String commandText = "";
        //for testing:
//        commandText = "mute#";
//        commandText = commandText + "appendln#DD:/tmp/$test$.txt&Line 1#" + "appendln#D:/tmp/f.txt&Line 2 + $test$#";

        //For building
        commandText = MS_TextFile.getFileTextAsString(args[0], "");
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.setPathToLoggerFile("MSScriptRunnerExecutable.log"); //ATM we are letting user to log info by himself
        runner.setVariableInputMethod(MS_InputOutputMethodDefaults._INPUT_JOPTION_PANE);
        runner.setPasswordInputMethod(MS_InputOutputMethodDefaults._INPUT_JOPTION_PANE_MASKED);
        runner.setOutputMethod(MS_InputOutputMethodDefaults._OUTPUT_JOPTION_PANE);
        runner.runScript();
    }
}