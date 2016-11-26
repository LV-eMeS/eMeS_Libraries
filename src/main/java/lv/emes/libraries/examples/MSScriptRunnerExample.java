package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.platform.IFuncStringInputMethod;
import lv.emes.libraries.tools.platform.IFuncStringMaskedInputMethod;
import lv.emes.libraries.tools.platform.IFuncStringOutputMethod;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

import static lv.emes.libraries.file_system.MS_FileSystemTools.getFilenameWithoutExtension;
import static lv.emes.libraries.file_system.MS_FileSystemTools.getShortFilename;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) throws AWTException {
        //For building
        String commandText = MS_TextFile.getFileTextAsString(args[0], "");
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.setPathToLoggerFile(getFilenameWithoutExtension(getShortFilename(args[0])) + ".log");
        runner.setVariableInputMethod(IFuncStringInputMethod.CONSOLE);
        runner.setPasswordInputMethod(IFuncStringMaskedInputMethod.CONSOLE);
        runner.setOutputMethod(IFuncStringOutputMethod.CONSOLE);
        runner.runScript();

        //for testing:
//        String commandText =
//                "variable#User&Ievadiet, lūdzu, savu vārdu!!!#" +
//                        "variable#vecums&Ievadiet, lūdzu, cik jums ir gadu!!!#" +
//                "pause#1500#" +
//                "TEXT#$User$ age is: $vecums$#";
//        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
//        runner.runScript();
    }
}