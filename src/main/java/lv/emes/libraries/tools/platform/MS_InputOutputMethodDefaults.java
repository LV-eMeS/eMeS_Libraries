package lv.emes.libraries.tools.platform;

import lv.emes.libraries.utilities.MS_CodingUtils;

import javax.swing.*;

/**
 * Due to incompatibility with Android API &lt;24 this class is introduced to hold static interface implementations containing lambda expressions.
 *
 * @author eMeS
 * @version 1.0.
 */
public class MS_InputOutputMethodDefaults {
    //some default input methods
    public static final MS_IFuncStringInputMethod _INPUT_CONSOLE = MS_CodingUtils::readStringFromConsole;
    public static final MS_IFuncStringInputMethod _INPUT_JOPTION_PANE = MS_CodingUtils::readStringFromJOptionPane;
    //TODO input method for JavaFX
    //TODO input method for Android
    //masked inputs
    public static final MS_IFuncStringInputMethod _INPUT_JOPTION_PANE_MASKED = MS_CodingUtils::readStringFromJPasswordField;
    //TODO input method for JavaFX with input mask *****
    //TODO input method for Android with input mask *****

    //some default output methods
    public static final MS_IFuncStringOutputMethod _OUTPUT_CONSOLE = System.out::println;
    public static final MS_IFuncStringOutputMethod _OUTPUT_JOPTION_PANE = (str) -> JOptionPane.showMessageDialog(null, str);
    //TODO output method for JavaFX
    //TODO output method for Android
}
