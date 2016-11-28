package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.MS_Tools;
import lv.emes.libraries.tools.platform.IFuncStringOutputMethod;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

/**
 * @author eMeS
 */
public class MSScriptRunnerExecutable {
    public static void main(String[] args) throws AWTException {
        //For building
        String commandText = MS_TextFile.getFileTextAsString(args[0], "");
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.setVariableInputMethod(MS_Tools::readlnStringFromConsole);
        runner.setPasswordInputMethod(MS_Tools::readlnStringFromConsole);
        runner.setOutputMethod(IFuncStringOutputMethod.CONSOLE);
        runner.runScript();

//        for testing:
//        String commandText = "run#cmd#pause#100#text#rundll32.exe user32.dll, LockWorkStation#ent#";
//        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
//        runner.runScript();
    }
}