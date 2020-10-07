package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.MS_ScriptPreProcessor;
import lv.emes.libraries.tools.platform.MS_InputOutputMethodDefaults;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

/**
 * @version 4.0.
 * @author eMeS
 */
public class MSScriptRunnerExecutable {

    public static void main(String[] args) {
        String commandText = "";
        //for testing:
//        commandText = "run#https://www.di.fm/channels#pause#7000#mc#80&231#ML#pause#1500#mc#336&258#ML#pause#1500#mc#560&375#ML#";
//        commandText = commandText + "mc#Integer(8-1 * 183 + 283)&440#ML#";
//        commandText = commandText + "run#notepad.exe#pause#300#text#1 + 2 = Integer(1+2)";
//        commandText = commandText + "comb#win&D#";

        //For building
        commandText = MS_TextFile.getFileTextAsString(args[0], ""); //uncomment when testing
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.setPreProcessor(new MS_ScriptPreProcessor());
        runner.setPathToLoggerFile("MSScriptRunnerExecutable.log"); //ATM we are letting user to log info by himself
        runner.setVariableInputMethod(MS_InputOutputMethodDefaults._INPUT_JOPTION_PANE);
        runner.setPasswordInputMethod(MS_InputOutputMethodDefaults._INPUT_JOPTION_PANE_MASKED);
        runner.setOutputMethod(MS_InputOutputMethodDefaults._OUTPUT_JOPTION_PANE);
        runner.runScript();
    }
}