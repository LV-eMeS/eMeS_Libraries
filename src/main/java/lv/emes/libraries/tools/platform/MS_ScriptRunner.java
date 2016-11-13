package lv.emes.libraries.tools.platform;

import com.sun.istack.internal.NotNull;
import lv.emes.libraries.tools.MS_Tools;
import lv.emes.libraries.tools.lists.MS_StringList;

import java.util.HashMap;
import java.util.Map;

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
 * <ul><li>runImplementationPrimary</li></ul>
 * <ul><li>runImplementationSecondary</li></ul>
 *
 * @author eMeS
 * @version 0.1.
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
    public final static int CMD_NR_MOUSE_SET_COORDINATES = 13;
    public final static int CMD_NR_MOUSE_MOVE_FOR_COORDINATES = 14;
    public final static int CMD_NR_PUSH_AND_HOLD_BUTTON = 15;
    public final static int CMD_NR_VARIABLE_PROMPT = 16;
    public final static int CMD_NR_PASSWORD_PROMPT = 17;
    public final static int CMD_NR_SET_LOGGING = 18;

    static {
        COMMANDS.put("TEXT", CMD_NR_WRITE_TEXT);
        COMMANDS.put("RUN", CMD_NR_RUN_APPLICATION);
        COMMANDS.put("WSHOW", CMD_NR_SHOW_WINDOW_OS_WINDOWS);
        COMMANDS.put("WHIDE", CMD_NR_HIDE_WINDOW_OS_WINDOWS);
        COMMANDS.put("PAUSE", CMD_NR_PAUSE);
        COMMANDS.put("DI", CMD_NR_SET_DELAY_INTERVAL);
        COMMANDS.put("ML", CMD_NR_MOUSE_LEFT);
        COMMANDS.put("MLD", CMD_NR_MOUSE_LEFT_DOWN);
        COMMANDS.put("MLU", CMD_NR_MOUSE_LEFT_UP);
        COMMANDS.put("MR", CMD_NR_MOUSE_RIGHT);
        COMMANDS.put("MRD", CMD_NR_MOUSE_RIGHT_DOWN);
        COMMANDS.put("MRU", CMD_NR_MOUSE_RIGHT_UP);
        COMMANDS.put("MC", CMD_NR_MOUSE_SET_COORDINATES);
        COMMANDS.put("MM", CMD_NR_MOUSE_MOVE_FOR_COORDINATES);
        COMMANDS.put("HOLD", CMD_NR_PUSH_AND_HOLD_BUTTON); //similar like sswin or ssctrl
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
    private final static int CMD_SEC_VARIABLE_PROMPT = 110;
    private final static int CMD_SEC_PASSWORD_PROMPT = 111;
    private final static int CMD_SEC_SET_LOGGING = 112;

    public final static char DELIMITER_OF_CMDS = '#';
    public final static char SECOND_DELIMITER_OF_CMDS = ';';

    private String fscript = "";
    private MS_StringList fCommandList;
    private boolean paused = false;
    private long delay = 0;
    private boolean primaryCommandReading = true;
    private int secondaryCmd = 0;
    private String pathToLoggerFile = "";

    public MS_ScriptRunner() {
        fCommandList = new MS_StringList();
    }

    public MS_ScriptRunner(@NotNull String scriptText) {
        this();
        setScriptText(scriptText);
    }

    public void runScript() {
//        shiftState := [];
//        TextMode := false;
//        AppMode := false;
//        TODO if eMeSIsCapsLockToogled then
//        eMeSPressKey('CAPS'); //caps lock mums pilnībā nav vajadzīgs

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

                if (cmd.length() > 0) {
                    if (primaryCommandReading) {
                        cmd = cmd.toUpperCase(); //whole script is case insensitive ^_^
                        //starting to check for every possible command
                        runImplementationPrimary(COMMANDS.get(cmd));
                    } else {
                        runImplementationSecondary(cmd);
                    }
                }
            } catch (Exception e) {
                if (!pathToLoggerFile.equals("")) {
                    //TODO catch errors and write them into logger file
                    //after this loop continues executing next commands
                }
//                fCommandList.breakDoWithEveryItem();
            }
        });
    }

    private void runImplementationPrimary(Integer cmdNumber) {
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
                MS_KeyStrokeExecutor.getInstance().mouseLeftClick();
                break;
            case CMD_NR_MOUSE_RIGHT:
                MS_KeyStrokeExecutor.getInstance().mouseRightClick();
                break;
            case CMD_NR_MOUSE_LEFT_DOWN:
                MS_KeyStrokeExecutor.getInstance().mouseLeftDown();
                break;
            case CMD_NR_MOUSE_RIGHT_DOWN:
                MS_KeyStrokeExecutor.getInstance().mouseRightDown();
                break;
            case CMD_NR_MOUSE_LEFT_UP:
                MS_KeyStrokeExecutor.getInstance().mouseLeftUp();
                break;
            case CMD_NR_MOUSE_RIGHT_UP:
                MS_KeyStrokeExecutor.getInstance().mouseRightUp();
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
        }
    }

    private void runImplementationSecondary(String commandText) {
        switch (secondaryCmd) {
            case CMD_SEC_EXECUTE_TEXT:
                //TODO
                break;
            case CMD_SEC_RUN_APPLICATION:

                break;
            case CMD_SEC_SHOW_WINDOW_OS_WINDOWS:

                break;
            case CMD_SEC_HIDE_WINDOW_OS_WINDOWS:

                break;
            case CMD_SEC_PAUSE:
                delay = Long.parseLong(commandText);
                paused = true;
                break;
            case CMD_SEC_SET_DELAY_INTERVAL:
                delay = Long.parseLong(commandText);
                break;
            case CMD_SEC_MOUSE_SET_COORDINATES:

                break;
            case CMD_SEC_MOUSE_MOVE_FOR_COORDINATES:

                break;
            case CMD_SEC_PUSH_AND_HOLD_BUTTON:
                //TODO
                break;
            case CMD_SEC_VARIABLE_PROMPT:
                //TODO
                break;
            case CMD_SEC_PASSWORD_PROMPT:
                //TODO
                break;
        }
        primaryCommandReading = true; //after this always go to new command reading
    }

    public static void runScript(@NotNull String scriptText) {
        MS_ScriptRunner runner = new MS_ScriptRunner(scriptText);
        runner.runScript();
    }

    public String getScriptText() {
        return fscript;
    }

    public void setScriptText(@NotNull String script) {
        this.fscript = fscript;
        fCommandList = new MS_StringList();
        fCommandList.delimiter = DELIMITER_OF_CMDS;
        fCommandList.secondDelimiter = SECOND_DELIMITER_OF_CMDS;
        fCommandList.fromString(script);
    }
}
