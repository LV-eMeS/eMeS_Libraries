package lv.emes.libraries.examples;

import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.platform.MS_ScriptRunner;

import java.awt.*;

import static lv.emes.libraries.file_system.MS_FileSystemTools.*;

/**
 * @author eMeS
 */
public class MSScriptRunnerExample {
    public static void main(String[] args) throws AWTException {
        String commandText = MS_TextFile.getFileTextAsString(args[0], "");
        MS_ScriptRunner runner = new MS_ScriptRunner(commandText);
        runner.setPathToLoggerFile(getFilenameWithoutExtension(getShortFilename(args[0])) + ".log");
        runner.runScript();
    }
}