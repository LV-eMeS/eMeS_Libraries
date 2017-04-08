package lv.emes.libraries.tools.platform;

import lv.emes.libraries.tools.MS_CodingTools;

import javax.swing.*;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_InputOutputMethodDefaults {
    //some default input methods
    public static final MS_IFuncStringInputMethod INPUT_CONSOLE = MS_CodingTools::readStringFromConsole;
    public static final MS_IFuncStringInputMethod INPUT_JOPTION_PANE = MS_CodingTools::readStringFromJOptionPane;
    //TODO input method for JavaFX
    //TODO input method for Android
    //masked inputs
    public static final MS_IFuncStringInputMethod INPUT_JOPTION_PANE_MASKED = MS_CodingTools::readStringFromJPasswordField;
    //TODO input method for JavaFX with input mask *****
    //TODO input method for Android with input mask *****

    //some default output methods
    public static final MS_IFuncStringOutputMethod OUTPUT_CONSOLE = System.out::println;
    public static final MS_IFuncStringOutputMethod OUTPUT_JOPTION_PANE = (str) -> JOptionPane.showMessageDialog(null, str);
    //TODO output method for JavaFX
    //TODO output method for Android
}
