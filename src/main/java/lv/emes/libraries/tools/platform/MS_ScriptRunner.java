package lv.emes.libraries.tools.platform;

import lv.emes.libraries.file_system.MS_FileSystemTools;
import lv.emes.libraries.file_system.MS_TextFile;
import lv.emes.libraries.tools.MS_CodingTools;
import lv.emes.libraries.tools.MS_KeyCodeDictionary;
import lv.emes.libraries.tools.MS_TimeTools;
import lv.emes.libraries.tools.lists.MS_StringList;
import lv.emes.libraries.tools.platform.windows.MS_ApplicationWindow;
import lv.emes.libraries.tools.platform.windows.MS_WindowsAPIManager;
import lv.emes.libraries.tools.platform.windows.MediaEventTypeEnum;

import java.awt.*;
import java.util.*;

import static lv.emes.libraries.tools.platform.MS_KeyStrokeExecutor.getInstance;
import static lv.emes.libraries.tools.platform.ScriptParsingError.*;

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
 * <li><code>TEXT#A Text to write#</code> - does keystroke execution for every printable key written as second parameter.</li>
 * <li><code>TEXT#User age is: $user_input_age$#</code> - does keystroke execution for text: "User age is: " and
 * variable or password defined in map <b>userVariables</b> with key "$user_input_age$".</li>
 * <li><code>TEXT#Special case of writing '$;'#</code> - does keystroke execution for text:"Special case of writing '$'"
 * <br><u>Note</u>: be careful, because this is the only way to write symbol $ - by adding semicolon after it.</li>
 * <li><code>RUN#Notepad.exe#</code> - launches application from path given as second parameter like "notepad.exe".</li>
 * <li><code>RUN#path_to_some_executable_with_parameters.exe&amp;cmd_line_param1 param2 param3#</code> - launches application with passed command line parameters.</li>
 * <li><code>WSHOW#notepad#</code> - (platform: Windows) brings first window matching text in task manager as second parameter like "notepad".</li>
 * <li><code>WHIDE#notepad#</code> - (platform: Windows) minimizes first window matching text in task manager as second parameter like "notepad".</li>
 * <li><code>PAUSE#1000#</code> - holds script executing for 1 second.</li>
 * <li><code>DI#1000#</code> - defines interval of delaying script command execution for 1 second after each command; to stop this
 * either use DI#0# or PAUSE#X#, and after X miliseconds delaying will be canceled.</li>
 * <li><code>ML#</code> - does left mouse click.</li>
 * <li><code>MLD#</code> - does left mouse press and hold.</li>
 * <li><code>MLU#</code> - does left mouse release up.</li>
 * <li><code>MR#</code> - does right mouse click.</li>
 * <li><code>MRD#</code> - does right mouse press and hold.</li>
 * <li><code>MRU#</code> - does right mouse release up.</li>
 * <li><code>MW#</code> or <code>WHEEL#</code> - does mouse wheel click.</li>
 * <li><code>MC#500&amp;400#</code> - sets mouse new location.</li>
 * <li><code>MM#-50&amp;20#</code> - moves mouse for 50 pixels to the left and 20 pixels down.</li>
 * <li><code>HOLD#CTRL#</code> - holds CTRL key until RELEASE command is executed.</li>
 * <li><code>RELEASE#CTRL#</code> - releases CTRL key (does button up for CTRL key code).</li>
 * <li><code>SS#CTRL#</code> - does HOLD + RELEASE for given key (ctrl in this case).</li>
 * <li><code>SAY#Hello, World!#</code> - prints "Hello, World!" using currently set output method.</li>
 * <li><code>VARIABLE#username_4_login&amp;Please, enter username of application X to log in!#</code> - does promting for
 * user input and informs user with text passed as second parameter.
 * <br>This will save in <b>userVariables</b> as map with key=username_4_login; value=user input text.
 * <br>Those variables will be used in <b>TEXT</b> command and recognized by "$" symbols before and after variable name.</li>
 * <li><code>PASSWORD#password_4_login&amp;Please, enter password of application X to log in!#</code> - does promting for
 * user secure input (characters will be replaced by *** when inputting)
 * <br>This will save in <b>userVariables</b> as map with key=username_4_login; value=user input text.
 * <br>Those variables will be used in <b>TEXT</b> command and recognized by "$" symbols before and after variable name.</li>
 * <li><code>LOGGING#D:/logs/ScriptRunner.log#</code> - enables logging of errors during script execution;
 * all the errors will be logged in file ScriptRunner.log;
 * by default this feature is turned off.</li>
 * <li><code>LOG#test.log#</code> - enables logging in same folder for file with name "test.log"; synonym of <b>LOGGING</b>.</li>
 * <li><code>VOL#22000#</code> - sets system volume to 22000.</li>
 * <li><code>VOLUME#22000#</code> - sets system volume to 22000.</li>
 * <li><code>VOLU#1000#</code> - increases system volume by 1000.</li>
 * <li><code>VOLD#1000#</code> - decreases system volume by 1000.</li>
 * <li><code>MONITOR#ON#</code> - switches on monitor.</li>
 * <li><code>MONITOR#OFF#</code> - switches off monitor.</li>
 * <li><code>MUSIC#NEXT#</code> - plays next media track.</li>
 * <li><code>MUSIC#PREV#</code> - plays previous media track.</li>
 * <li><code>MUSIC#PLAY#</code> or <code>MUSIC#PAUSE#</code> or <code>MUSIC#PLAYPAUSE#</code> - plays or pauses media track.</li>
 * <li><code>MUSIC#STOP#</code> - stops playing music.</li>
 * <li><code>COMBINATION#Ctrl&amp;Alt&amp;Delete#</code> - does Ctrl+Alt+Del keystroke combination.</li>
 * <li><code>COMB#Win&amp;D#</code> - does Windows+D keystroke combination. A synonim of <b>COMBINATION</b>.</li>
 * </ul>
 *
 * @author eMeS
 * @version 1.4.
 */
public class MS_ScriptRunner {

    private final static Map<String, Integer> COMMANDS = new HashMap<>();
    private final static int CMD_NR_WRITE_TEXT = 1;
    private final static int CMD_NR_RUN_APPLICATION = 2;
    private final static int CMD_NR_SHOW_WINDOW_OS_WINDOWS = 3;
    private final static int CMD_NR_HIDE_WINDOW_OS_WINDOWS = 4;
    private final static int CMD_NR_PAUSE = 5;
    private final static int CMD_NR_SET_DELAY_INTERVAL = 6;
    private final static int CMD_NR_MOUSE_LEFT = 7;
    private final static int CMD_NR_MOUSE_RIGHT = 8;
    private final static int CMD_NR_MOUSE_LEFT_DOWN = 9;
    private final static int CMD_NR_MOUSE_LEFT_UP = 10;
    private final static int CMD_NR_MOUSE_RIGHT_DOWN = 11;
    private final static int CMD_NR_MOUSE_RIGHT_UP = 12;
    private final static int CMD_NR_MOUSE_WHEEL_CLICK = 13;
    private final static int CMD_NR_MOUSE_SET_COORDINATES = 14;
    private final static int CMD_NR_MOUSE_MOVE_FOR_COORDINATES = 15;
    private final static int CMD_NR_PUSH_AND_HOLD_BUTTON = 16;
    private final static int CMD_NR_RELEASE_HOLD_BUTTON = 17;
    private final static int CMD_NR_HOLD_AND_RELEASE_BUTTON = 18;
    private final static int CMD_NR_VARIABLE_PROMPT = 19;
    private final static int CMD_NR_PASSWORD_PROMPT = 20;
    private final static int CMD_NR_SET_LOGGING = 21;
    private final static int CMD_NR_SET_VOLUME_TO = 22;
    private final static int CMD_NR_VOLUME_UP = 23;
    private final static int CMD_NR_VOLUME_DOWN = 24;
    private final static int CMD_NR_MONITOR = 25;
    private final static int CMD_NR_MUSIC = 26;
    private final static int CMD_NR_COMBINATION = 27;
    private final static int CMD_NR_SAY = 28;

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
        COMMANDS.put("LOG", CMD_NR_SET_LOGGING); //synonym
        COMMANDS.put("VOL", CMD_NR_SET_VOLUME_TO);
        COMMANDS.put("VOLUME", CMD_NR_SET_VOLUME_TO); //synonym
        COMMANDS.put("VOLU", CMD_NR_VOLUME_UP);
        COMMANDS.put("VOLD", CMD_NR_VOLUME_DOWN);
        COMMANDS.put("MONITOR", CMD_NR_MONITOR); //monitor on or off
        COMMANDS.put("MUSIC", CMD_NR_MUSIC); //music play/pause/stop, previous/next track
        COMMANDS.put("MEDIA", CMD_NR_MUSIC); //synonym
        COMMANDS.put("COMBINATION", CMD_NR_COMBINATION);
        COMMANDS.put("COMB", CMD_NR_COMBINATION);
        COMMANDS.put("SAY", CMD_NR_SAY);
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
    private final static int CMD_SEC_SET_VOLUME_TO = 115;
    private final static int CMD_SEC_VOLUME_UP = 116;
    private final static int CMD_SEC_VOLUME_DOWN = 117;
    private final static int CMD_SEC_MONITOR = 118;
    private final static int CMD_SEC_MUSIC = 119;
    private final static int CMD_SEC_COMBINATION = 120;
    private final static int CMD_SEC_SAY = 121;

    private final static char DELIMITER_OF_CMDS = '#';
    private final static char DELIMITER_OF_CMDS_SECOND = ';';
    private final static char DELIMITER_OF_PARAMETERS = '&';
    private final static char DELIMITER_OF_VARIABLES = '$';

    private String fscript = "";
    private MS_StringList fCommandList;
    private Map<String, String> userVariables = new HashMap<>();
    private boolean commandNotFoundTryKeyPressing = false;
    private boolean paused = false;
    private long delay = 0;
    private boolean primaryCommandReading = true;
    private int secondaryCmd = 0;
    private MS_IFuncStringInputMethod variableInputMethod = MS_InputOutputMethodDefaults._INPUT_CONSOLE;
    private MS_IFuncStringInputMethod passwordInputMethod = MS_InputOutputMethodDefaults._INPUT_CONSOLE;
    private MS_IFuncStringOutputMethod outputMethod = MS_InputOutputMethodDefaults._OUTPUT_CONSOLE;

    public String getPathToLoggerFile() {
        return pathToLoggerFile;
    }

    public void setPasswordInputMethod(MS_IFuncStringInputMethod passwordInputMethod) {
        this.passwordInputMethod = passwordInputMethod;
    }

    public void setPathToLoggerFile(String pathToLoggerFile) {
        this.pathToLoggerFile = pathToLoggerFile;
    }

    public void setVariableInputMethod(MS_IFuncStringInputMethod variableInputMethod) {
        this.variableInputMethod = variableInputMethod;
    }

    public void setOutputMethod(MS_IFuncStringOutputMethod outputMethod) {
        this.outputMethod = outputMethod;
    }

    private String pathToLoggerFile = "";

    public MS_ScriptRunner() {
    }

    public MS_ScriptRunner(String scriptText) {
        this();
        setScriptText(scriptText);
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
            case CMD_NR_SET_VOLUME_TO:
                primaryCommandReading = false; //read volume parameter
                secondaryCmd = CMD_SEC_SET_VOLUME_TO;
                break;
            case CMD_NR_VOLUME_UP:
                primaryCommandReading = false; //read volume parameter
                secondaryCmd = CMD_SEC_VOLUME_UP;
                break;
            case CMD_NR_VOLUME_DOWN:
                primaryCommandReading = false; //read volume parameter
                secondaryCmd = CMD_SEC_VOLUME_DOWN;
                break;
            case CMD_NR_MONITOR:
                primaryCommandReading = false; //read on or off parameter
                secondaryCmd = CMD_SEC_MONITOR;
                break;
            case CMD_NR_MUSIC:
                primaryCommandReading = false; //read play, pause, stop, next, prev parameters
                secondaryCmd = CMD_SEC_MUSIC;
                break;
            case CMD_NR_COMBINATION:
                primaryCommandReading = false; //read volume parameter
                secondaryCmd = CMD_SEC_COMBINATION;
                break;
            case CMD_NR_SAY:
                primaryCommandReading = false; //read text to say
                secondaryCmd = CMD_SEC_SAY;
                break;
            default:
                commandNotFoundTryKeyPressing = true;
        }
    }

    private void runImplementationSecondary(String commandParamsAsText) throws Exception {
        MS_StringList params;
        String tmpStr;
        String tmpStr2;
        Integer volumeLevelParameter;
        switch (secondaryCmd) {
            case CMD_SEC_EXECUTE_TEXT:
                commandParamsAsText = extractCommandContainingVariables(commandParamsAsText);

                MS_KeyStrokeExecutor exec = getInstance();
                for (int i = 0; i < commandParamsAsText.length(); i++) {
                    try {
                        char singleCharOfText = commandParamsAsText.charAt(i);
                        boolean doShiftPress = MS_KeyCodeDictionary.needToPushShiftToWriteChar(singleCharOfText);
                        if (doShiftPress)
                            getInstance().keyDown("SHIFT");
                        String key = Character.toString(singleCharOfText);
                        exec.keyPress(key);
                        if (doShiftPress)
                            getInstance().keyUp("SHIFT");
                    } catch (Exception e) {
//                        throw new ScriptParsingError("Unknown error trying to write text");
                    }
                }
                break;
            case CMD_SEC_RUN_APPLICATION:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                StringBuilder appParams = new StringBuilder();
                if (params.count() > 2)
                    for (int i = 1; i < params.count(); i++)
                        appParams.append(params.get(i));
                MS_FileSystemTools.executeApplication(params.get(0), appParams.toString());
                break;
            case CMD_SEC_SHOW_WINDOW_OS_WINDOWS:
                if (!MS_ApplicationWindow.showApplicationWindow(commandParamsAsText))
                    throw new ScriptParsingError(String.format(_ERROR_FAILED_TO_SHOW_WINDOW, commandParamsAsText));
                break;
            case CMD_SEC_HIDE_WINDOW_OS_WINDOWS:
                if (!MS_ApplicationWindow.hideApplicationWindow(commandParamsAsText)) {
                    throw new ScriptParsingError(String.format(_ERROR_FAILED_TO_HIDE_WINDOW, commandParamsAsText));
                }
                break;
            case CMD_SEC_PAUSE:
                delay = Long.parseLong(commandParamsAsText);
                paused = true;
                break;
            case CMD_SEC_SET_DELAY_INTERVAL:
                delay = Long.parseLong(commandParamsAsText);
                break;
            case CMD_SEC_MOUSE_SET_COORDINATES:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                if (params.count() != 2)
                    throw new ScriptParsingError(String.format(_ERROR_PARAMETER_COUNT, 2));
                getInstance().mouseSetCoords(
                        new Point(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)))
                );
                break;
            case CMD_SEC_MOUSE_MOVE_FOR_COORDINATES:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                if (params.count() != 2)
                    throw new ScriptParsingError(String.format(_ERROR_PARAMETER_COUNT, 2));
                getInstance().mouseMove(
                        new Point(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)))
                );
                break;
            case CMD_SEC_PUSH_AND_HOLD_BUTTON:
                getInstance().keyDown(commandParamsAsText);
                break;
            case CMD_SEC_RELEASE_HOLD_BUTTON:
                getInstance().keyUp(commandParamsAsText);
                break;
            case CMD_SEC_HOLD_AND_RELEASE_BUTTON:
                getInstance().keyDown(commandParamsAsText);
                getInstance().keyUp(commandParamsAsText);
                break;
            case CMD_SEC_VARIABLE_PROMPT:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                if (params.count() != 2)
                    throw new ScriptParsingError(String.format(_ERROR_PARAMETER_COUNT, 2));
                //put variable in userVariables
                tmpStr2 = variableInputMethod.readString(params.get(1));
                tmpStr = userVariables.put(params.get(0), tmpStr2);
                if (tmpStr != null)
                    throw new ScriptParsingError(String.format(_WARNING_USER_VARIABLE_OVERRIDDEN, tmpStr, tmpStr2));
                break;
            case CMD_SEC_PASSWORD_PROMPT:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                if (params.count() != 2)
                    throw new ScriptParsingError(String.format(_ERROR_PARAMETER_COUNT, 2));

                //put variable in userVariables
                tmpStr2 = passwordInputMethod.readString(params.get(1));
                tmpStr = userVariables.put(params.get(0), tmpStr2);
                if (tmpStr != null)
                    throw new ScriptParsingError(String.format(_WARNING_USER_VARIABLE_OVERRIDDEN, params.get(0), tmpStr, tmpStr2));
                break;
            case CMD_SEC_SET_LOGGING:
                setPathToLoggerFile(commandParamsAsText);
                break;
            case CMD_SEC_SET_VOLUME_TO:
                try {
                    volumeLevelParameter = new Integer(commandParamsAsText);
                } catch (NumberFormatException e) {
                    throw new ScriptParsingError(String.format(_ERROR_WRONG_NUMBER_INPUT, commandParamsAsText));
                }
                MS_WindowsAPIManager.setVolume(volumeLevelParameter);
                break;
            case CMD_SEC_VOLUME_UP:
                try {
                    volumeLevelParameter = new Integer(commandParamsAsText);
                } catch (NumberFormatException e) {
                    throw new ScriptParsingError(String.format(_ERROR_WRONG_NUMBER_INPUT, commandParamsAsText));
                }
                MS_WindowsAPIManager.volumeUp(volumeLevelParameter);
                break;
            case CMD_SEC_VOLUME_DOWN:
                try {
                    volumeLevelParameter = new Integer(commandParamsAsText);
                } catch (NumberFormatException e) {
                    throw new ScriptParsingError(String.format(_ERROR_WRONG_NUMBER_INPUT, commandParamsAsText));
                }
                MS_WindowsAPIManager.volumeDown(volumeLevelParameter);
                break;
            case CMD_SEC_MONITOR:
                commandParamsAsText = commandParamsAsText.toLowerCase();
                if (commandParamsAsText.equals("off") || commandParamsAsText.equals("on"))
                    MS_WindowsAPIManager.turnMonitor(commandParamsAsText);
                else
                    throw new ScriptParsingError(_ERROR_FAILED_TO_SWITCH_MONITOR);
                break;
            case CMD_SEC_MUSIC:
                MediaEventTypeEnum eventType = MediaEventTypeEnum.getByKey(commandParamsAsText);
                if (eventType == null)
                    throw new ScriptParsingError("Unsupported media event. Expected one of [PLAY, PAUSE, PLAYPAUSE, STOP, NEXT, PREV].");
                MS_WindowsAPIManager.fireMediaEvent(eventType);
            case CMD_SEC_COMBINATION:
                params = new MS_StringList(commandParamsAsText, DELIMITER_OF_PARAMETERS);
                if (params.count() < 2)
                    throw new ScriptParsingError(String.format(_ERROR_PARAMETER_COUNT, 2));

                //first push all the buttons down
                params.first();
                while (params.currentIndexInsideTheList()) {
                    getInstance().keyDown(params.get(params.getIndexOfCurrent()));
                    params.next();
                }
                //after release all the buttons
                params.last();
                while (params.currentIndexInsideTheList()) {
                    getInstance().keyUp(params.get(params.getIndexOfCurrent()));
                    params.prev();
                }
                break;
            case CMD_SEC_SAY:
                commandParamsAsText = extractCommandContainingVariables(commandParamsAsText);
                outputMethod.writeString(commandParamsAsText);
                break;
            default:
                commandNotFoundTryKeyPressing = true;
        }
        primaryCommandReading = true; //after this always go to new command reading
    }

    /**
     * Extracting input from script as text where instead of variable names there is put actual values of variables.
     *
     * @param incomingCommand command data like: <b>Something before $variable_name$ and something after</b>.
     * @return if <b>variable_name=this text</b> then it's transformed to: <b>Something before this text and something after</b>.
     */
    private String extractCommandContainingVariables(String incomingCommand) {
        MS_StringList listOfVariables = new MS_StringList();
        listOfVariables.delimiter = DELIMITER_OF_VARIABLES;
        listOfVariables.secondDelimiter = DELIMITER_OF_CMDS_SECOND;
        listOfVariables.fromString(incomingCommand);

        StringBuilder sb = new StringBuilder();
        //every even element of string list will be a variable because variables are like $pass$
        listOfVariables.forEachItem((str, i) -> {
            if (i % 2 == 1) { //an even element should be altered
                str = userVariables.get(str);
            }
            sb.append(str);
        });
        return sb.toString();
    }

    public void runScript() {
        userVariables.clear();
        if (getInstance().isCapsLockToggled())
            getInstance().keyPress("CAPS"); //caps lock during script executing is not needed at all

        fCommandList.forEachItem((cmd, index) -> {
            try {
//                System.out.println(cmd);
                if (delay > 0) {
                    MS_CodingTools.sleep(delay); //delay interval can be set using command "di"
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
//                e.printStackTrace();
                System.out.println(e.toString());
                primaryCommandReading = true; //if command fails then lets try to read next command as primary command!
                if (!pathToLoggerFile.equals("")) {
                    MS_TextFile tmpLogFile = new MS_TextFile(pathToLoggerFile);
                    Date now = new Date();
                    tmpLogFile.appendln(MS_TimeTools.dateTimeToStr(now) + String.format(" : Command [%d] '%s' failed to execute with message:", index + 1, cmd), false);
                    tmpLogFile.appendln(e.toString(), true);
                    //after this loop continues executing next commands
                }
            }
        });
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
        fCommandList.secondDelimiter = DELIMITER_OF_CMDS_SECOND;
        fCommandList.fromString(script);
    }
}
