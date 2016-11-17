package lv.emes.libraries.tools.platform;

import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.MS_KeyCodeDictionary;
import lv.emes.libraries.tools.MS_TimeTools;
import lv.emes.libraries.tools.MS_Tools;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.platform.windows.ApplicationWindow;

import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static lv.emes.libraries.tools.platform.MS_KeyStrokeExecutor.getInstance;

/**
 * A class that accepts specific commands and forms script that can be launched in order to perform different
 * platform independent user actions like application launching, key stroke simulation, mouse pointer simulations
 * and other user action imitations.
 * <p>Public methods:
 * <ul><li>runScript()</li></ul>
 * <p>Static methods:
 * <ul><li>runScript(String)</li></ul>
 * <p>Setters and getters:
 * <ul>
 * <li>getScriptText</li>
 * <li>setScriptText</li>
 * </ul>
 * <p>Private methods:</p>
 * <ul><li>runImplementationPrimary</li>
 * <li>runImplementationSecondary</li></ul>
 * <p>Available commands for script execution:</p>
 * <ul>
 * <li>TEXT#A Text to write# - does keystroke execution for every printable key written as second parameter</li>
 * <li>RUN#Notepad.exe# - launches application from path given as second parameter like "notepad.exe"</li>
 * <li>WSHOW#notepad# - (platform: Windows) brings first window matching text in task manager as second parameter like "notepad"</li>
 * <li>WHIDE#notepad# - (platform: Windows) minimizes first window matching text in task manager as second parameter like "notepad"</li>
 * <li>PAUSE#1000# - holds script executing for 1 second</li>
 * <li>DI#1000# - defines interval of delaying script command execution for 1 second after each command; to stop this
 * either use DI#0# or PAUSE#X#, and after X miliseconds delaying will be canceled</li>
 * <li>ML# - does left mouse click</li>
 * <li>MLD# - does left mouse press and hold</li>
 * <li>MLU# - does left mouse release up</li>
 * <li>MR# - does right mouse click</li>
 * <li>MRD# - does right mouse press and hold</li>
 * <li>MRU# - does right mouse release up</li>
 * <li>MW# or WHEEL# - does mouse wheel click</li>
 * <li>MC#500,400# - sets mouse new location</li>
 * <li>MM#-50,20# - moves mouse for 50 pixels to the left and 20 pixels down</li>
 * <li>HOLD#CTRL# - holds CTRL key until RELEASE command is executed</li>
 * <li>RELEASE#CTRL# - releases CTRL key (does button up for CTRL key code)</li>
 * <li>SS#CTRL# - does HOLD + RELEASE for given key (ctrl in this case)</li>
 * <li>VARIABLE#Please, enter username of application X to log in!# - does promting for
 * user input and informs user with text passed as second parameter</li>
 * <li>PASSWORD#Please, enter password of application X to log in!# - does promting for
 * user secure input (characters will be replaced by *** when inputting)</li>
 * <li>LOGGING#D:/logs/ScriptRunner.log# - enables logging of errors during script execution;
 * all the errors will be logged in file ScriptRunner.log</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_ScriptRunner {
    public final static Map<String, Integer> COMMANDS = new HashMap<>();
    public final static int CMD_NR_WRITE_TEXT = 1;
    public final static int CMD_NR_RUN_APPLICATION = 2;
    public final static int CMD_NR_SHOW_WINDOW_OS_WINDOWS = 3;
    public final static int CMD_NR_HIDE_WINDOW_OS_WINDOWS = 4;
    public final static int CMD_NR_PAUSE = 5;
    public final static int CMD_NR_SET_DELAY_INTERVAL = 6;
    public final static int CMD_NR_MOUSE_LEFT = 7;
    public final static int CMD_NR_MOUSE_RIGHT = 8;
    public final static int CMD_NR_MOUSE_LEFT_DOWN = 9;
    public final static int CMD_NR_MOUSE_LEFT_UP = 10;
    public final static int CMD_NR_MOUSE_RIGHT_DOWN = 11;
    public final static int CMD_NR_MOUSE_RIGHT_UP = 12;
    public final static int CMD_NR_MOUSE_WHEEL_CLICK = 13;
    public final static int CMD_NR_MOUSE_SET_COORDINATES = 14;
    public final static int CMD_NR_MOUSE_MOVE_FOR_COORDINATES = 15;
    public final static int CMD_NR_PUSH_AND_HOLD_BUTTON = 16;
    public final static int CMD_NR_RELEASE_HOLD_BUTTON = 17;
    public final static int CMD_NR_HOLD_AND_RELEASE_BUTTON = 18;
    public final static int CMD_NR_VARIABLE_PROMPT = 19;
    public final static int CMD_NR_PASSWORD_PROMPT = 20;
    public final static int CMD_NR_SET_LOGGING = 21;

    static {
        COMMANDS.put("TEXT", CMD_NR_WRITE_TEXT);
        COMMANDS.put("RUN", CMD_NR_RUN_APPLICATION);
        COMMANDS.put("WSHOW", CMD_NR_SHOW_WINDOW_OS_WINDOWS);
        COMMANDS.put("SHOW", CMD_NR_SHOW_WINDOW_OS_WINDOWS); //synonym for Windows OS only
        COMMANDS.put("WHIDE", CMD_NR_HIDE_WINDOW_OS_WINDOWS);
        COMMANDS.put("HIDE", CMD_NR_HIDE_WINDOW_OS_WINDOWS); //synonym for Windows OS only
        COMMANDS.put("PAUSE", CMD_NR_PAUSE);
        COMMANDS.put("DI", CMD_NR_SET_DELAY_INTERVAL);
        COMMANDS.put("ML", CMD_NR_MOUSE_LEFT);
        COMMANDS.put("MLD", CMD_NR_MOUSE_LEFT_DOWN);
        COMMANDS.put("MLU", CMD_NR_MOUSE_LEFT_UP);
        COMMANDS.put("MR", CMD_NR_MOUSE_RIGHT);
        COMMANDS.put("MRD", CMD_NR_MOUSE_RIGHT_DOWN);
        COMMANDS.put("MRU", CMD_NR_MOUSE_RIGHT_UP);
        COMMANDS.put("MW", CMD_NR_MOUSE_WHEEL_CLICK);
        COMMANDS.put("WHEEL", CMD_NR_MOUSE_WHEEL_CLICK); //synonym
        COMMANDS.put("MC", CMD_NR_MOUSE_SET_COORDINATES);
        COMMANDS.put("MM", CMD_NR_MOUSE_MOVE_FOR_COORDINATES);
        COMMANDS.put("HOLD", CMD_NR_PUSH_AND_HOLD_BUTTON); //similar like sswin or ssctrl
        COMMANDS.put("RELEASE", CMD_NR_RELEASE_HOLD_BUTTON); //similar like sswin or ssctrl
        COMMANDS.put("SS", CMD_NR_HOLD_AND_RELEASE_BUTTON);
        COMMANDS.put("VARIABLE", CMD_NR_VARIABLE_PROMPT); //like: variable+variable description+
        COMMANDS.put("PASSWORD", CMD_NR_PASSWORD_PROMPT); //like: password+password description+
        COMMANDS.put("LOGGING", CMD_NR_SET_LOGGING); //by default logging is off, but with this you can set path to a log file where errors will be logged
    }

    private final static int CMD_SEC_EXECUTE_TEXT = 101;
    private final static int CMD_SEC_RUN_APPLICATION = 102;
    private final static int CMD_SEC_SHOW_WINDOW_OS_WINDOWS = 103;
    private final static int CMD_SEC_HIDE_WINDOW_OS_WINDOWS = 104;
    private final static int CMD_SEC_PAUSE = 105;
    private final static int CMD_SEC_SET_DELAY_INTERVAL = 106;
    private final static int CMD_SEC_MOUSE_SET_COORDINATES = 107;
    private final static int CMD_SEC_MOUSE_MOVE_FOR_COORDINATES = 108;
    private final static int CMD_SEC_PUSH_AND_HOLD_BUTTON = 109;
    private final static int CMD_SEC_RELEASE_HOLD_BUTTON = 110;
    private final static int CMD_SEC_HOLD_AND_RELEASE_BUTTON = 111;
    private final static int CMD_SEC_VARIABLE_PROMPT = 112;
    private final static int CMD_SEC_PASSWORD_PROMPT = 113;
    private final static int CMD_SEC_SET_LOGGING = 114;

    public final static char DELIMITER_OF_CMDS = '#';
    public final static char SECOND_DELIMITER_OF_CMDS = ';';

    private String fscript = "";
    private MS_StringList fCommandList;
    private boolean commandNotFoundTryKeyPressing = false;
    private boolean paused = false;
    private long delay = 0;
    private boolean primaryCommandReading = true;
    private int secondaryCmd = 0;

    public String getPathToLoggerFile() {
        return pathToLoggerFile;
    }

    public void setPathToLoggerFile(String pathToLoggerFile) {
        this.pathToLoggerFile = pathToLoggerFile;
    }

    private String pathToLoggerFile = "";

    public MS_ScriptRunner() {
        fCommandList = new MS_StringList();
    }

    public MS_ScriptRunner(String scriptText) {
        this();
        setScriptText(scriptText);
    }

    public void runScript() {
        if (getInstance().isCapsLockToggled())
            getInstance().keyPress("CAPS"); //caps lock during script executing is not needed at all

        fCommandList.doWithEveryItem((cmd, index) -> {
            try {
                System.out.println(cmd);
                if (delay > 0) {
                    MS_Tools.sleep(delay); //delay interval can be set using command "di"
                    if (paused) { //if script was paused then this is the place to remove pause
                        paused = false;
                        delay = 0; //in next iteration pause will not be used anymore
                    }
                }

                if ((cmd.length() > 0) && (cmd.charAt(0) != '/')) { //ignore commands starting with comment
                    commandNotFoundTryKeyPressing = false;
                    if (primaryCommandReading) {
                        cmd = cmd.toUpperCase(); //whole script is case insensitive ^_^
                        //starting to check for every possible command
                        runImplementationPrimary(COMMANDS.get(cmd));
                    } else {
                        runImplementationSecondary(cmd);
                    }

                    if (commandNotFoundTryKeyPressing)
                        getInstance().keyPress(cmd);
                }
            } catch (Exception e) {
                e.printStackTrace();
                primaryCommandReading = true; //if command fails then lets try to read next command as primary command!
                if (!pathToLoggerFile.equals("")) {
                    MS_TextFile tmpLogFile = new MS_TextFile(pathToLoggerFile);
                    Date now = new Date();
                    tmpLogFile.appendln(MS_TimeTools.eMeSDateTimeToStr(now) + String.format(" : Command [%d] '%s' failed to execute with message:", index + 1, cmd), false);
                    tmpLogFile.appendln(e.toString(), true);
                    //after this loop continues executing next commands
                }
            }
        });
    }

    private void runImplementationPrimary(Integer cmdNumber) {
        if (cmdNumber == null) {
            commandNotFoundTryKeyPressing = true;
            return;
        }
        switch (cmdNumber) {
            case CMD_NR_WRITE_TEXT:
                primaryCommandReading = false; //there will be need to read and execute user text keystrokes
                secondaryCmd = CMD_SEC_EXECUTE_TEXT;
                break;
            case CMD_NR_RUN_APPLICATION:
                primaryCommandReading = false; //there will be need to read application name
                secondaryCmd = CMD_SEC_RUN_APPLICATION;
                break;
            case CMD_NR_SHOW_WINDOW_OS_WINDOWS:
                primaryCommandReading = false; //there will be need to read application name
                secondaryCmd = CMD_SEC_SHOW_WINDOW_OS_WINDOWS;
                break;
            case CMD_NR_HIDE_WINDOW_OS_WINDOWS:
                primaryCommandReading = false; //there will be need to read application name
                secondaryCmd = CMD_SEC_HIDE_WINDOW_OS_WINDOWS;
                break;
            case CMD_NR_PAUSE:
                primaryCommandReading = false; //there will be need to read pause delay as miliseconds
                secondaryCmd = CMD_SEC_PAUSE;
                break;
            case CMD_NR_SET_DELAY_INTERVAL:
                primaryCommandReading = false; //there will be need to read delay after each command
                secondaryCmd = CMD_SEC_SET_DELAY_INTERVAL;
                break;
            case CMD_NR_MOUSE_LEFT:
                getInstance().mouseLeftClick();
                break;
            case CMD_NR_MOUSE_RIGHT:
                getInstance().mouseRightClick();
                break;
            case CMD_NR_MOUSE_LEFT_DOWN:
                getInstance().mouseLeftDown();
                break;
            case CMD_NR_MOUSE_RIGHT_DOWN:
                getInstance().mouseRightDown();
                break;
            case CMD_NR_MOUSE_LEFT_UP:
                getInstance().mouseLeftUp();
                break;
            case CMD_NR_MOUSE_RIGHT_UP:
                getInstance().mouseRightUp();
                break;
            case CMD_NR_MOUSE_WHEEL_CLICK:
                getInstance().mouseWheelClick();
                break;
            case CMD_NR_MOUSE_SET_COORDINATES:
                primaryCommandReading = false; //read mouse X;Y
                secondaryCmd = CMD_SEC_MOUSE_SET_COORDINATES;
                break;
            case CMD_NR_MOUSE_MOVE_FOR_COORDINATES:
                primaryCommandReading = false; //read mouse X;Y
                secondaryCmd = CMD_SEC_MOUSE_MOVE_FOR_COORDINATES;
                break;
            case CMD_NR_PUSH_AND_HOLD_BUTTON:
                primaryCommandReading = false; //read button to push and hold code
                secondaryCmd = CMD_SEC_PUSH_AND_HOLD_BUTTON;
                break;
            case CMD_NR_RELEASE_HOLD_BUTTON:
                primaryCommandReading = false; //read button to push and hold code
                secondaryCmd = CMD_SEC_RELEASE_HOLD_BUTTON;
                break;
            case CMD_NR_HOLD_AND_RELEASE_BUTTON:
                primaryCommandReading = false;
                secondaryCmd = CMD_SEC_HOLD_AND_RELEASE_BUTTON;
                break;
            case CMD_NR_VARIABLE_PROMPT:
                primaryCommandReading = false; //read variable description
                secondaryCmd = CMD_SEC_VARIABLE_PROMPT;
                break;
            case CMD_NR_PASSWORD_PROMPT:
                primaryCommandReading = false; //read password description
                secondaryCmd = CMD_SEC_PASSWORD_PROMPT;
                break;
            case CMD_NR_SET_LOGGING:
                primaryCommandReading = false; //read path to logger
                secondaryCmd = CMD_SEC_SET_LOGGING;
                break;
            default:
                commandNotFoundTryKeyPressing = true;
        }
    }

    private void runImplementationSecondary(String commandText) {
        MS_StringList params;
        switch (secondaryCmd) {
            case CMD_SEC_EXECUTE_TEXT:
                MS_KeyStrokeExecutor exec = getInstance();
                for (int i = 0; i < commandText.length(); i++) {
                    try {
                        char singleCharOfText = commandText.charAt(i);
                        boolean doShiftPress = MS_KeyCodeDictionary.needToPushShiftToWriteChar(singleCharOfText);
                        if (doShiftPress)
                            getInstance().keyDown("SHIFT");
                        String key = Character.toString(singleCharOfText);
                        exec.keyPress(key);
                        if (doShiftPress)
                            getInstance().keyUp("SHIFT");
                    } catch (Exception e) {
                        //TODO catch this one!
                    }
                }
                break;
            case CMD_SEC_RUN_APPLICATION:
                params = new MS_StringList(commandText, ',');
                MS_FileSystemTools.executeApplication(params.get(0), params.get(1));
                break;
            case CMD_SEC_SHOW_WINDOW_OS_WINDOWS:
                ApplicationWindow.showApplicationWindow(commandText);
                //TODO throw error if false
                break;
            case CMD_SEC_HIDE_WINDOW_OS_WINDOWS:
                ApplicationWindow.hideApplicationWindow(commandText);
                //TODO throw error if false
                break;
            case CMD_SEC_PAUSE:
                delay = Long.parseLong(commandText);
                paused = true;
                break;
            case CMD_SEC_SET_DELAY_INTERVAL:
                delay = Long.parseLong(commandText);
                break;
            case CMD_SEC_MOUSE_SET_COORDINATES:
                params = new MS_StringList(commandText, ',');
                getInstance().mouseSetCoords(
                        new Point(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)))
                );
                break;
            case CMD_SEC_MOUSE_MOVE_FOR_COORDINATES:
                params = new MS_StringList(commandText, ',');
                getInstance().mouseMove(
                        new Point(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)))
                );
                break;
            case CMD_SEC_PUSH_AND_HOLD_BUTTON:
                getInstance().keyDown(commandText);
                //TODO do windows key press etc
                break;
            case CMD_SEC_RELEASE_HOLD_BUTTON:
                getInstance().keyUp(commandText);
                //TODO do windows key press etc
                break;
            case CMD_SEC_HOLD_AND_RELEASE_BUTTON:
                getInstance().keyDown(commandText);
                getInstance().keyUp(commandText);
                //TODO do windows key press etc
                break;
            case CMD_SEC_VARIABLE_PROMPT:
                //TODO
                break;
            case CMD_SEC_PASSWORD_PROMPT:
                //TODO
                break;
            default:
                commandNotFoundTryKeyPressing = true;
        }
        primaryCommandReading = true; //after this always go to new command reading
    }

    public static void runScript(String scriptText) {
        MS_ScriptRunner runner = new MS_ScriptRunner(scriptText);
        runner.runScript();
    }

    public String getScriptText() {
        return fscript;
    }

    public void setScriptText(String script) {
        this.fscript = fscript;
        fCommandList = new MS_StringList();
        fCommandList.delimiter = DELIMITER_OF_CMDS;
        fCommandList.secondDelimiter = SECOND_DELIMITER_OF_CMDS;
        fCommandList.fromString(script);
    }
}
